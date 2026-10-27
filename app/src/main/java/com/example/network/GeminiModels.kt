package com.example.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @Json(name = "contents") val contents: List<ContentItem>,
    @Json(name = "systemInstruction") val systemInstruction: ContentItem? = null,
    @Json(name = "generationConfig") val generationConfig: GenerationConfig? = null
)

@JsonClass(generateAdapter = true)
data class ContentItem(
    @Json(name = "role") val role: String? = null,
    @Json(name = "parts") val parts: List<PartItem>
)

@JsonClass(generateAdapter = true)
data class PartItem(
    @Json(name = "text") val text: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    @Json(name = "temperature") val temperature: Double = 0.7,
    @Json(name = "topP") val topP: Double = 0.9,
    @Json(name = "maxOutputTokens") val maxOutputTokens: Int = 300
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<CandidateItem>? = null,
    @Json(name = "error") val error: GeminiError? = null
)

@JsonClass(generateAdapter = true)
data class CandidateItem(
    @Json(name = "content") val content: ContentItem? = null,
    @Json(name = "finishReason") val finishReason: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiError(
    @Json(name = "code") val code: Int? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "status") val status: String? = null
)
