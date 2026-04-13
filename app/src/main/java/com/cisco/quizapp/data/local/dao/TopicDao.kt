package com.cisco.quizapp.data.local.dao

import androidx.room.*
import com.cisco.quizapp.data.local.entity.Topic
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {

    @Query("SELECT * FROM topics ORDER BY name ASC")
    fun getAllTopics(): Flow<List<Topic>>

    @Query("SELECT * FROM topics WHERE id = :id")
    suspend fun getTopicById(id: Long): Topic?

    @Query("SELECT * FROM topics WHERE difficultyLevel = :level ORDER BY name ASC")
    fun getTopicsByDifficulty(level: Int): Flow<List<Topic>>

    @Query("SELECT COUNT(*) FROM topics")
    suspend fun getTopicCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: Topic): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopics(topics: List<Topic>): List<Long>

    @Update
    suspend fun updateTopic(topic: Topic)

    @Delete
    suspend fun deleteTopic(topic: Topic)

    @Query("DELETE FROM topics WHERE id = :id")
    suspend fun deleteTopicById(id: Long)

    @Query("DELETE FROM topics")
    suspend fun deleteAllTopics()
}
