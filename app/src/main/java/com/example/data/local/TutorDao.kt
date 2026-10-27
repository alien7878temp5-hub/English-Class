package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TutorDao {
    // Call Sessions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: CallSessionEntity)

    @Query("SELECT * FROM call_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<CallSessionEntity>>

    @Query("SELECT COUNT(*) FROM call_sessions")
    fun getSessionCount(): Flow<Int>

    // Recasts (Gentle Corrections)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecast(recast: RecastEntity)

    @Query("SELECT * FROM recast_notes ORDER BY timestamp DESC")
    fun getAllRecasts(): Flow<List<RecastEntity>>

    @Query("SELECT * FROM recast_notes WHERE mastered = 0 ORDER BY timestamp DESC LIMIT 5")
    suspend fun getRecentUnmasteredRecasts(): List<RecastEntity>

    @Update
    suspend fun updateRecast(recast: RecastEntity)

    // Idioms
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIdioms(idioms: List<IdiomEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateIdiom(idiom: IdiomEntity)

    @Query("SELECT * FROM learned_idioms ORDER BY idiom ASC")
    fun getAllIdioms(): Flow<List<IdiomEntity>>

    @Query("SELECT * FROM learned_idioms WHERE isFavorite = 1 ORDER BY idiom ASC")
    fun getFavoriteIdioms(): Flow<List<IdiomEntity>>

    @Query("SELECT COUNT(*) FROM learned_idioms WHERE masteryLevel >= 3")
    fun getMasteredIdiomCount(): Flow<Int>

    @Update
    suspend fun updateIdiom(idiom: IdiomEntity)
}
