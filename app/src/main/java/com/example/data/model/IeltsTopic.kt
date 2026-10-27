package com.example.data.model

enum class IeltsPart {
    PART_1,
    PART_2,
    PART_3
}

data class IeltsTopic(
    val id: String,
    val part: IeltsPart,
    val topicTitle: String,
    val promptDescription: String,
    val bulletPoints: List<String> = emptyList(),
    val suggestedBritishPhrases: List<String> = emptyList(),
    val targetBandLevel: String = "Band 7.5 - 8.5"
)
