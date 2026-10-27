package com.example.data.model

enum class MessageSender {
    USER,
    TUTOR
}

data class RecastData(
    val originalPhrase: String,
    val correctedBritishPhrase: String,
    val tutorNote: String
)

data class IdiomData(
    val idiom: String,
    val meaning: String,
    val britishExample: String,
    val phoneticRP: String
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val recast: RecastData? = null,
    val idiomIntroduced: IdiomData? = null,
    val isSpacedRecall: Boolean = false
)
