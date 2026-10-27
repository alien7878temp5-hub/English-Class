package com.example.repository

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.data.local.AppDatabase
import com.example.data.local.CallSessionEntity
import com.example.data.local.IdiomEntity
import com.example.data.local.RecastEntity
import com.example.data.model.CallMode
import com.example.data.model.CefrLevel
import com.example.data.model.ChatMessage
import com.example.data.model.IdiomData
import com.example.data.model.MessageSender
import com.example.data.model.RecastData
import com.example.data.model.TutorPersona
import com.example.network.ContentItem
import com.example.network.GeminiApiService
import com.example.network.GeminiRequest
import com.example.network.GenerationConfig
import com.example.network.PartItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONObject

class TutorRepository(context: Context) {
    private val TAG = "TutorRepository"
    private val db = AppDatabase.getDatabase(context)
    private val dao = db.tutorDao()
    private val geminiApi = GeminiApiService.create()

    fun getAllSessions(): Flow<List<CallSessionEntity>> = dao.getAllSessions()
    fun getAllRecasts(): Flow<List<RecastEntity>> = dao.getAllRecasts()
    fun getAllIdioms(): Flow<List<IdiomEntity>> = dao.getAllIdioms()
    fun getMasteredCount(): Flow<Int> = dao.getMasteredIdiomCount()
    fun getSessionCount(): Flow<Int> = dao.getSessionCount()

    suspend fun saveSession(session: CallSessionEntity) = withContext(Dispatchers.IO) {
        dao.insertSession(session)
    }

    suspend fun saveRecast(recast: RecastEntity) = withContext(Dispatchers.IO) {
        dao.insertRecast(recast)
    }

    suspend fun updateRecast(recast: RecastEntity) = withContext(Dispatchers.IO) {
        dao.updateRecast(recast)
    }

    suspend fun saveOrUpdateIdiom(idiom: IdiomEntity) = withContext(Dispatchers.IO) {
        dao.insertOrUpdateIdiom(idiom)
    }

    suspend fun updateIdiom(idiom: IdiomEntity) = withContext(Dispatchers.IO) {
        dao.updateIdiom(idiom)
    }

    suspend fun generateTutorResponse(
        persona: TutorPersona,
        level: CefrLevel,
        mode: CallMode,
        conversationHistory: List<ChatMessage>,
        turnCount: Int,
        recentMistakes: List<String>
    ): ChatMessage = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val hasValidKey = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

        if (hasValidKey) {
            try {
                val apiResponse = callGemini(persona, level, mode, conversationHistory, turnCount, recentMistakes, apiKey)
                if (apiResponse != null) {
                    return@withContext apiResponse
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gemini API call encountered issue: ${e.message}, falling back to local British engine", e)
            }
        }

        // Resilient, rich local British tutor engine fallback
        return@withContext generateLocalBritishTutorResponse(
            persona, level, mode, conversationHistory, turnCount, recentMistakes
        )
    }

    private suspend fun callGemini(
        persona: TutorPersona,
        level: CefrLevel,
        mode: CallMode,
        conversationHistory: List<ChatMessage>,
        turnCount: Int,
        recentMistakes: List<String>,
        apiKey: String
    ): ChatMessage? {
        val systemPrompt = buildSystemPrompt(persona, level, mode, turnCount, recentMistakes)

        val contents = mutableListOf<ContentItem>()
        // Pass recent dialogue history (last 6 turns for rapid latency)
        val recentHistory = conversationHistory.takeLast(6)
        for (msg in recentHistory) {
            val role = if (msg.sender == MessageSender.USER) "user" else "model"
            contents.add(ContentItem(role = role, parts = listOf(PartItem(text = msg.text))))
        }

        // Add instruction for current turn to output JSON
        val request = GeminiRequest(
            systemInstruction = ContentItem(parts = listOf(PartItem(text = systemPrompt))),
            contents = contents,
            generationConfig = GenerationConfig(
                temperature = 0.7,
                topP = 0.85,
                maxOutputTokens = 350
            )
        )

        val response = geminiApi.generateContent(apiKey = apiKey, request = request)
        val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: return null

        return parseGeminiTutorJson(rawText, persona)
    }

    private fun buildSystemPrompt(
        persona: TutorPersona,
        level: CefrLevel,
        mode: CallMode,
        turnCount: Int,
        recentMistakes: List<String>
    ): String {
        val shouldInjectIdiom = (turnCount > 0 && turnCount % 3 == 0)

        return """
        You are "${persona.name}", an elite, patient, and warm British English tutor speaking through a real-time voice call simulator on an Android app.
        Your ultimate goal is to guide the learner from their current level up to IELTS Band 8.5+ native-like fluency, focusing strictly on Received Pronunciation (RP) / Modern British English, natural conversational flow, idioms, and British slang.

        COMMUNICATION STYLE & VOICE:
        - Accent: Modern Received Pronunciation (Standard Southern British / RP).
        - Tone: Engaging, encouraging, articulate, and conversational (like a friendly private tutor on a phone call).
        - Pace & Vocabulary: Adapt strictly to learner's chosen CEFR level: ${level.code} (${level.label}, target: ${level.ieltsBand}). Description: ${level.description}. Focus: ${level.pedagogicalFocus}.
        - Format: Keep your turns conversational and concise (1 to 3 sentences per turn). Do NOT deliver long monologues; this is a live phone conversation.

        PEDAGOGICAL RULES & MEMORY:
        1. Gentle Correction (Recasting): When the user makes a grammatical, lexical, or pronunciation error, do not lecture them. Naturally echo the correct form in your next reply, or briefly explain: "A Brit would usually say '...' here."
        2. British Idioms & Slang (Scaffolding): ${if (shouldInjectIdiom) "THIS IS TURN #$turnCount: Introduce 1 authentic British slang or idiom (e.g. 'gutted', 'chuffed', 'proper', 'dodgy', 'not my cup of tea', 'bits and bobs', 'knackered', 'throw a spanner in the works'). Verify they understood it." else "Keep conversational flow natural. Use British mannerisms like 'Grand', 'Spot on', 'Rather'."}
        3. Spaced Recall: ${if (recentMistakes.isNotEmpty()) "Recall previous mistake: ${recentMistakes.last()} and weave it casually into conversation." else "None"}
        4. IELTS Alignment: Mode is ${mode.title}. ${mode.promptInstruction}

        OUTPUT FORMAT:
        Respond strictly with a JSON object in this exact schema:
        {
          "tutorReply": "1-3 sentences spoken by ${persona.name}",
          "recastOriginal": "user's error or null",
          "recastCorrection": "corrected British phrasing or null",
          "recastExplanation": "brief tutor tip or null",
          "idiom": "British idiom if introduced or null",
          "idiomMeaning": "meaning of the idiom or null",
          "idiomPhonetic": "RP phonetic transcription or null",
          "isSpacedRecall": false
        }
        """.trimIndent()
    }

    private fun parseGeminiTutorJson(rawText: String, persona: TutorPersona): ChatMessage {
        var cleanJson = rawText.trim()
        if (cleanJson.startsWith("```json")) {
            cleanJson = cleanJson.removePrefix("```json").trim()
        }
        if (cleanJson.startsWith("```")) {
            cleanJson = cleanJson.removePrefix("```").trim()
        }
        if (cleanJson.endsWith("```")) {
            cleanJson = cleanJson.removeSuffix("```").trim()
        }

        return try {
            val json = JSONObject(cleanJson)
            val reply = json.optString("tutorReply", persona.greeting)
            val recastOrig = json.optString("recastOriginal").takeIf { it.isNotBlank() && it != "null" }
            val recastCorr = json.optString("recastCorrection").takeIf { it.isNotBlank() && it != "null" }
            val recastExp = json.optString("recastExplanation").takeIf { it.isNotBlank() && it != "null" }

            val recastData = if (recastOrig != null && recastCorr != null) {
                RecastData(
                    originalPhrase = recastOrig,
                    correctedBritishPhrase = recastCorr,
                    tutorNote = recastExp ?: "A Brit would usually say '$recastCorr'."
                )
            } else null

            val idiomStr = json.optString("idiom").takeIf { it.isNotBlank() && it != "null" }
            val idiomMean = json.optString("idiomMeaning").takeIf { it.isNotBlank() && it != "null" }
            val idiomPhonetic = json.optString("idiomPhonetic").takeIf { it.isNotBlank() && it != "null" }

            val idiomData = if (idiomStr != null && idiomMean != null) {
                IdiomData(
                    idiom = idiomStr,
                    meaning = idiomMean,
                    britishExample = reply,
                    phoneticRP = idiomPhonetic ?: "/ˈbrɪt.ɪʃ/"
                )
            } else null

            ChatMessage(
                sender = MessageSender.TUTOR,
                text = reply,
                recast = recastData,
                idiomIntroduced = idiomData,
                isSpacedRecall = json.optBoolean("isSpacedRecall", false)
            )
        } catch (e: Exception) {
            Log.w(TAG, "JSON parse fallback for raw response: $rawText", e)
            ChatMessage(
                sender = MessageSender.TUTOR,
                text = rawText.replace("{", "").replace("}", "").take(250)
            )
        }
    }

    private fun generateLocalBritishTutorResponse(
        persona: TutorPersona,
        level: CefrLevel,
        mode: CallMode,
        history: List<ChatMessage>,
        turnCount: Int,
        recentMistakes: List<String>
    ): ChatMessage {
        val lastUserMessage = history.lastOrNull { it.sender == MessageSender.USER }?.text?.lowercase().orEmpty()

        // 1. Detect common learner errors for gentle recasting
        var recast: RecastData? = null
        if (lastUserMessage.contains("i have been in london") && !lastUserMessage.contains("i have been to london")) {
            recast = RecastData(
                originalPhrase = "I have been in London",
                correctedBritishPhrase = "I have been to London",
                tutorNote = "In British English, we say 'been to' when you have visited and returned."
            )
        } else if (lastUserMessage.contains("i make a photo") || lastUserMessage.contains("make photo")) {
            recast = RecastData(
                originalPhrase = "make a photo",
                correctedBritishPhrase = "take a photograph",
                tutorNote = "A Brit would usually say 'take a photograph' or 'snap a picture'."
            )
        } else if (lastUserMessage.contains("i am agree") || lastUserMessage.contains("i'm agree")) {
            recast = RecastData(
                originalPhrase = "I am agree",
                correctedBritishPhrase = "I agree",
                tutorNote = "'Agree' is already a verb in English; we say 'I agree completely'."
            )
        } else if (lastUserMessage.contains("i go to home")) {
            recast = RecastData(
                originalPhrase = "go to home",
                correctedBritishPhrase = "go home",
                tutorNote = "We omit 'to' before home: 'I am heading home'."
            )
        }

        // 2. Idiom injection every 3rd turn
        val shouldInjectIdiom = (turnCount > 0 && turnCount % 3 == 0)
        var idiomData: IdiomData? = null

        val responseText = when {
            recast != null -> {
                val greeting = if (persona.gender == com.example.data.model.TutorGender.MALE) "Quite right! And as a Brit might say," else "Spot on! In Britain we'd phrase it,"
                "$greeting '${recast.correctedBritishPhrase}'. You're making grand headway. What thoughts come to mind next?"
            }
            shouldInjectIdiom -> {
                val idiomsPool = listOf(
                    IdiomData("Chuffed to bits", "Delighted, pleased, proud", "I'm chuffed to bits with your pronunciation progress today!", "/tʃʌft tuː bɪts/"),
                    IdiomData("Proper", "Authentic, thorough, genuine", "That was a proper good explanation you gave just now.", "/ˈprɒp.ə/"),
                    IdiomData("Bits and bobs", "Assorted small items or topics", "We have covered quite a few useful bits and bobs so far.", "/bɪts ənd bɒbz/"),
                    IdiomData("Not my cup of tea", "Not suited to one's taste", "Public speaking isn't everyone's cup of tea, but you carry yourself splendidly.", "/nɒt maɪ kʌp əv tiː/"),
                    IdiomData("Bob's your uncle", "And there you have it, simple as that", "Keep that steady RP vowel rhythm, and Bob's your uncle!", "/bɒbz jɔːr ˈʌŋ.kəl/")
                )
                val chosen = idiomsPool[(turnCount / 3) % idiomsPool.size]
                idiomData = chosen
                "${chosen.britishExample} Have you ever come across the phrase '${chosen.idiom}' before?"
            }
            mode == CallMode.IELTS_SIMULATOR -> {
                when (turnCount % 4) {
                    0 -> "In IELTS Speaking, examiners look for fluent discourse markers. Could you describe an event from your recent memory that was particularly memorable?"
                    1 -> "Fascinating description. To what extent do you feel that modern technology alters the way we share such experiences with others?"
                    2 -> "That's a sound argument. Let's delve into Part 3: do you think future generations will place higher value on heritage traditions or digital convenience?"
                    else -> "Splendid articulation. You used a wonderful range of grammatical structures there. How would you summarise your perspective in one sentence?"
                }
            }
            lastUserMessage.contains("hello") || lastUserMessage.contains("hi") || lastUserMessage.contains("morning") -> {
                "Hello there! It is an absolute pleasure to connect with you. How has your week been shaping up so far?"
            }
            lastUserMessage.contains("weather") || lastUserMessage.contains("rain") || lastUserMessage.contains("cold") -> {
                "Ah, British weather—the ultimate conversational ice-breaker! It is drizzling lightly here in England, but nothing a hot cup of tea can't cure. How is the climate where you are?"
            }
            lastUserMessage.contains("ielts") || lastUserMessage.contains("exam") || lastUserMessage.contains("score") -> {
                "For that coveted Band 8.5+, focus on natural collocation and idiomatic flow rather than rehearsed answers. What topic do you find most challenging in the speaking test?"
            }
            else -> {
                val reflections = if (persona.gender == com.example.data.model.TutorGender.MALE) {
                    listOf(
                        "I see precisely what you mean. That reminds me of an old British perspective on the matter. How do you feel about that?",
                        "Splendid observation! Your cadence is sounding noticeably more natural today. Would you elaborate on that point?",
                        "Spot on! In Received Pronunciation, keeping your vowel sounds open and crisp gives you immense authority. Shall we explore that further?",
                        "Grand! You phrased that with real clarity. Tell me, what would be your ideal way to spend an autumn afternoon in Britain?"
                    )
                } else {
                    listOf(
                        "That is wonderfully put. You articulated your point with such clarity! What led you to that conclusion?",
                        "Brilliant thought! Your conversational rhythm is flowing so nicely. Could you tell me a little more about that?",
                        "Splendid! Notice how naturally you connected those thoughts together. Shall we take that a step further?",
                        "Fabulous effort! I'm genuinely impressed by your lexical choice there. What other aspects of British culture fascinate you?"
                    )
                }
                reflections[turnCount % reflections.size]
            }
        }

        return ChatMessage(
            sender = MessageSender.TUTOR,
            text = responseText,
            recast = recast,
            idiomIntroduced = idiomData,
            isSpacedRecall = false
        )
    }
}
