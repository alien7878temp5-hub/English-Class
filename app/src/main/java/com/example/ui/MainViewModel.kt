package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CallSessionEntity
import com.example.data.local.IdiomEntity
import com.example.data.local.RecastEntity
import com.example.data.model.CallMode
import com.example.data.model.CefrLevel
import com.example.data.model.ChatMessage
import com.example.data.model.IeltsTopic
import com.example.data.model.MessageSender
import com.example.data.model.TutorGender
import com.example.data.model.TutorPersona
import com.example.repository.TutorRepository
import com.example.service.SpeechInputEngine
import com.example.service.TtsEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TutorRepository(application)
    val ttsEngine = TtsEngine(application)

    // Tutor and Settings
    private val _selectedGender = MutableStateFlow(TutorGender.MALE)
    val selectedGender: StateFlow<TutorGender> = _selectedGender.asStateFlow()

    private val _currentPersona = MutableStateFlow(TutorPersona.ARTHUR)
    val currentPersona: StateFlow<TutorPersona> = _currentPersona.asStateFlow()

    private val _cefrLevel = MutableStateFlow(CefrLevel.B1)
    val cefrLevel: StateFlow<CefrLevel> = _cefrLevel.asStateFlow()

    private val _callMode = MutableStateFlow(CallMode.DAILY_CHIT_CHAT)
    val callMode: StateFlow<CallMode> = _callMode.asStateFlow()

    // Call State
    private val _isCallActive = MutableStateFlow(false)
    val isCallActive: StateFlow<Boolean> = _isCallActive.asStateFlow()

    private val _callDurationSeconds = MutableStateFlow(0L)
    val callDurationSeconds: StateFlow<Long> = _callDurationSeconds.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _isSpeakerOn = MutableStateFlow(true)
    val isSpeakerOn: StateFlow<Boolean> = _isSpeakerOn.asStateFlow()

    private val _recentMistakes = MutableStateFlow<List<String>>(emptyList())
    val recentMistakes: StateFlow<List<String>> = _recentMistakes.asStateFlow()

    private val _activeSessionRecasts = MutableStateFlow<List<RecastEntity>>(emptyList())
    val activeSessionRecasts: StateFlow<List<RecastEntity>> = _activeSessionRecasts.asStateFlow()

    private val _activeSessionIdioms = MutableStateFlow<List<IdiomEntity>>(emptyList())
    val activeSessionIdioms: StateFlow<List<IdiomEntity>> = _activeSessionIdioms.asStateFlow()

    // Post-call summary dialog
    private val _showDebriefDialog = MutableStateFlow(false)
    val showDebriefDialog: StateFlow<Boolean> = _showDebriefDialog.asStateFlow()

    private val _lastCompletedSession = MutableStateFlow<CallSessionEntity?>(null)
    val lastCompletedSession: StateFlow<CallSessionEntity?> = _lastCompletedSession.asStateFlow()

    // Database Flows
    val allSessions = repository.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRecasts = repository.getAllRecasts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allIdioms = repository.getAllIdioms()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sessionCount = repository.getSessionCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val masteredIdiomCount = repository.getMasteredCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Speech Recognizer
    val speechEngine = SpeechInputEngine(
        context = application,
        onResult = { recognizedText ->
            if (recognizedText.isNotBlank() && !_isMuted.value) {
                sendMessage(recognizedText)
            }
        },
        onError = { /* Logged in engine */ }
    )

    private var callTimerJob: Job? = null
    private var currentSessionId: String = ""

    fun selectTutor(gender: TutorGender) {
        _selectedGender.value = gender
        val persona = TutorPersona.getPersona(gender)
        _currentPersona.value = persona
        ttsEngine.setupVoicePreferences(
            isMale = gender == TutorGender.MALE,
            speechRate = _cefrLevel.value.speechRate,
            pitch = persona.pitch
        )
    }

    fun selectCefrLevel(level: CefrLevel) {
        _cefrLevel.value = level
        ttsEngine.setupVoicePreferences(
            isMale = _selectedGender.value == TutorGender.MALE,
            speechRate = level.speechRate,
            pitch = _currentPersona.value.pitch
        )
    }

    fun selectCallMode(mode: CallMode) {
        _callMode.value = mode
    }

    fun hearSampleGreeting(persona: TutorPersona) {
        ttsEngine.speak(
            text = persona.greeting,
            speechRate = _cefrLevel.value.speechRate,
            pitch = persona.pitch
        )
    }

    fun speakText(text: String) {
        ttsEngine.speak(
            text = text,
            speechRate = _cefrLevel.value.speechRate,
            pitch = _currentPersona.value.pitch
        )
    }

    fun startCall(initialMode: CallMode? = null, topic: IeltsTopic? = null) {
        initialMode?.let { _callMode.value = it }
        currentSessionId = UUID.randomUUID().toString()
        _callDurationSeconds.value = 0L
        _activeSessionRecasts.value = emptyList()
        _activeSessionIdioms.value = emptyList()
        _isCallActive.value = true
        _showDebriefDialog.value = false

        // Start call duration timer
        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            while (isActive && _isCallActive.value) {
                delay(1000)
                _callDurationSeconds.value += 1
            }
        }

        // Persona initial greeting turn
        val greetingText = if (topic != null) {
            "Hello there! ${_currentPersona.value.name} here. Splendid to have you. Today let's tackle IELTS ${topic.part.name.replace("_", " ")} on '${topic.topicTitle}'. Whenever you're ready, take the floor!"
        } else {
            _currentPersona.value.greeting
        }

        val initialMessage = ChatMessage(
            sender = MessageSender.TUTOR,
            text = greetingText
        )
        _messages.value = listOf(initialMessage)

        // Speak British greeting
        ttsEngine.speak(
            text = greetingText,
            speechRate = _cefrLevel.value.speechRate,
            pitch = _currentPersona.value.pitch
        )
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        val userMessage = ChatMessage(
            sender = MessageSender.USER,
            text = userText
        )
        val updatedList = _messages.value + userMessage
        _messages.value = updatedList

        // Query AI Tutor
        viewModelScope.launch {
            _isThinking.value = true
            val turnCount = updatedList.count { it.sender == MessageSender.USER }

            val tutorReply = repository.generateTutorResponse(
                persona = _currentPersona.value,
                level = _cefrLevel.value,
                mode = _callMode.value,
                conversationHistory = updatedList,
                turnCount = turnCount,
                recentMistakes = _recentMistakes.value
            )

            _messages.value = _messages.value + tutorReply
            _isThinking.value = false

            // Process recast notes
            tutorReply.recast?.let { recast ->
                _recentMistakes.value = (_recentMistakes.value + recast.originalPhrase).takeLast(5)
                val recastEntity = RecastEntity(
                    sessionId = currentSessionId,
                    timestamp = System.currentTimeMillis(),
                    originalUserInput = recast.originalPhrase,
                    correctedBritishPhrase = recast.correctedBritishPhrase,
                    tutorExplanation = recast.tutorNote
                )
                _activeSessionRecasts.value = _activeSessionRecasts.value + recastEntity
                repository.saveRecast(recastEntity)
            }

            // Process idioms introduced
            tutorReply.idiomIntroduced?.let { idiom ->
                val idiomEntity = IdiomEntity(
                    id = idiom.idiom.lowercase().replace(" ", "_").replace("'", ""),
                    idiom = idiom.idiom,
                    meaning = idiom.meaning,
                    britishExample = idiom.britishExample,
                    phoneticRP = idiom.phoneticRP,
                    category = "In-Call Practice"
                )
                _activeSessionIdioms.value = _activeSessionIdioms.value + idiomEntity
                repository.saveOrUpdateIdiom(idiomEntity)
            }

            // Speak response via TTS
            ttsEngine.speak(
                text = tutorReply.text,
                speechRate = _cefrLevel.value.speechRate,
                pitch = _currentPersona.value.pitch
            )
        }
    }

    fun toggleMicrophone() {
        if (speechEngine.isListening.value) {
            speechEngine.stopListening()
        } else {
            speechEngine.startListening()
        }
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
        if (_isMuted.value) {
            speechEngine.stopListening()
        }
    }

    fun toggleSpeaker() {
        _isSpeakerOn.value = !_isSpeakerOn.value
    }

    fun endCall() {
        ttsEngine.stop()
        speechEngine.stopListening()
        callTimerJob?.cancel()
        _isCallActive.value = false

        // Compute session score & band estimate
        val userTurnCount = _messages.value.count { it.sender == MessageSender.USER }
        val duration = _callDurationSeconds.value

        val (bandEstimate, fluency, vocab, grammar, pronun) = evaluateSession(
            level = _cefrLevel.value,
            turns = userTurnCount,
            duration = duration,
            recastCount = _activeSessionRecasts.value.size
        )

        val sessionEntity = CallSessionEntity(
            id = currentSessionId,
            timestamp = System.currentTimeMillis(),
            tutorGender = _selectedGender.value.name,
            tutorName = _currentPersona.value.name,
            cefrLevel = _cefrLevel.value.code,
            callMode = _callMode.value.title,
            durationSeconds = duration,
            totalTurns = userTurnCount,
            estimatedBandScore = bandEstimate,
            fluencynScore = fluency,
            vocabularyScore = vocab,
            grammarScore = grammar,
            pronunciationScore = pronun,
            summaryFeedback = "Splendid conversational engagement with ${_currentPersona.value.name}. You completed $userTurnCount conversational turns with focus on Received Pronunciation rhythm."
        )

        _lastCompletedSession.value = sessionEntity
        _showDebriefDialog.value = true

        viewModelScope.launch {
            repository.saveSession(sessionEntity)
        }
    }

    private fun evaluateSession(
        level: CefrLevel,
        turns: Int,
        duration: Long,
        recastCount: Int
    ): BandScoreEvaluation {
        val baseScore = when (level) {
            CefrLevel.A1 -> 3.5f
            CefrLevel.A2 -> 4.5f
            CefrLevel.B1 -> 6.0f
            CefrLevel.B2 -> 7.0f
            CefrLevel.C1 -> 8.0f
            CefrLevel.C2 -> 8.5f
        }
        val fluencyBonus = if (turns >= 5 && duration >= 60) 0.5f else 0.0f
        val calculated = (baseScore + fluencyBonus).coerceAtMost(9.0f)
        val bandStr = "Band ${String.format(java.util.Locale.US, "%.1f", calculated)}"

        return BandScoreEvaluation(
            band = bandStr,
            fluency = (calculated + 0.2f).coerceAtMost(9.0f),
            vocabulary = calculated,
            grammar = (calculated - (recastCount * 0.1f)).coerceAtLeast(4.0f),
            pronunciation = calculated
        )
    }

    fun dismissDebriefDialog() {
        _showDebriefDialog.value = false
    }

    fun toggleIdiomFavorite(idiom: IdiomEntity) {
        viewModelScope.launch {
            repository.updateIdiom(idiom.copy(isFavorite = !idiom.isFavorite))
        }
    }

    fun markIdiomMastered(idiom: IdiomEntity) {
        viewModelScope.launch {
            val newLevel = (idiom.masteryLevel + 1).coerceAtMost(5)
            repository.updateIdiom(idiom.copy(masteryLevel = newLevel))
        }
    }

    fun markRecastMastered(recast: RecastEntity) {
        viewModelScope.launch {
            repository.updateRecast(recast.copy(mastered = true))
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsEngine.shutdown()
        speechEngine.destroy()
        callTimerJob?.cancel()
    }
}

data class BandScoreEvaluation(
    val band: String,
    val fluency: Float,
    val vocabulary: Float,
    val grammar: Float,
    val pronunciation: Float
)
