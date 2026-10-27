package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "call_sessions")
data class CallSessionEntity(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val tutorGender: String,
    val tutorName: String,
    val cefrLevel: String,
    val callMode: String,
    val durationSeconds: Long,
    val totalTurns: Int,
    val estimatedBandScore: String,
    val fluencynScore: Float,
    val vocabularyScore: Float,
    val grammarScore: Float,
    val pronunciationScore: Float,
    val summaryFeedback: String
)

@Entity(tableName = "recast_notes")
data class RecastEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String,
    val timestamp: Long,
    val originalUserInput: String,
    val correctedBritishPhrase: String,
    val tutorExplanation: String,
    val mastered: Boolean = false
)

@Entity(tableName = "learned_idioms")
data class IdiomEntity(
    @PrimaryKey val id: String,
    val idiom: String,
    val meaning: String,
    val britishExample: String,
    val phoneticRP: String,
    val category: String,
    val timesPracticed: Int = 1,
    val masteryLevel: Int = 1, // 1 to 5
    val lastReviewedTimestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
