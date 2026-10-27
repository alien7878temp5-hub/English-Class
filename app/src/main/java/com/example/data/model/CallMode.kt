package com.example.data.model

enum class CallMode(
    val title: String,
    val subtitle: String,
    val iconName: String,
    val promptInstruction: String
) {
    DAILY_CHIT_CHAT(
        title = "Casual British Chit-Chat",
        subtitle = "Everyday life, weather, tea culture & relaxed banter",
        iconName = "Coffee",
        promptInstruction = "Engage in natural, warm British everyday conversation. Discuss daily life, British culture, tea, plans, and small talk. Keep it relaxed and conversational."
    ),
    BRITISH_IDIOMS(
        title = "British Slang & Idioms Masterclass",
        subtitle = "Scaffolded authentic slang ('chuffed', 'proper', 'dodgy')",
        iconName = "AutoAwesome",
        promptInstruction = "Prioritize teaching and using authentic British idioms and slang (such as 'gutted', 'chuffed', 'proper', 'dodgy', 'not my cup of tea', 'bits and bobs', 'throw a spanner in the works'). Introduce 1 idiom every 3-4 turns and verify understanding."
    ),
    IELTS_SIMULATOR(
        title = "IELTS Speaking Simulator",
        subtitle = "Band 8.5+ structured exam coaching (Parts 1, 2 & 3)",
        iconName = "School",
        promptInstruction = "Act as an official IELTS Speaking examiner with warm British encouragement. Alternate between Part 1 everyday questions, Part 2 cue card prompts, and Part 3 analytical questions. Grade vocabulary range and coherence."
    ),
    RECAST_AND_PRONUNCIATION(
        title = "Gentle Recasting & RP Clinic",
        subtitle = "Pronunciation nuance & real-time subtle corrections",
        iconName = "RecordVoiceOver",
        promptInstruction = "Focus intensely on subtle grammatical recasting and British Received Pronunciation tips (e.g. non-rhotic 'r', pure vowels, 'a' in 'bath/path'). Gently recast any errors immediately without interrupting the flow."
    )
}
