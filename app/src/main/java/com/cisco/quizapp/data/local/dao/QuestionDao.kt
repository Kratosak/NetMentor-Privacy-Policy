package com.cisco.quizapp.data.local.dao

import androidx.room.*
import com.cisco.quizapp.data.local.entity.Question
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {

    @Query("SELECT * FROM questions ORDER BY id ASC")
    fun getAllQuestions(): Flow<List<Question>>

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getQuestionById(id: Long): Question?

    @Query("SELECT * FROM questions WHERE topicId = :topicId ORDER BY difficulty ASC")
    fun getQuestionsByTopic(topicId: Long): Flow<List<Question>>

    @Query("SELECT * FROM questions WHERE topicId = :topicId AND difficulty = :difficulty ORDER BY id ASC")
    fun getQuestionsByTopicAndDifficulty(topicId: Long, difficulty: Int): Flow<List<Question>>

    @Query("SELECT * FROM questions WHERE topicId = :topicId ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomQuestionsForTopic(topicId: Long, limit: Int): List<Question>

    @Query("SELECT * FROM questions WHERE difficulty = :difficulty ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomQuestionsByDifficulty(difficulty: Int, limit: Int): List<Question>

    @Query("SELECT COUNT(*) FROM questions WHERE topicId = :topicId")
    suspend fun getQuestionCountForTopic(topicId: Long): Int

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getTotalQuestionCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: Question): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<Question>): List<Long>

    @Update
    suspend fun updateQuestion(question: Question)

    @Delete
    suspend fun deleteQuestion(question: Question)

    @Query("DELETE FROM questions WHERE id = :id")
    suspend fun deleteQuestionById(id: Long)

    @Query("DELETE FROM questions WHERE topicId = :topicId")
    suspend fun deleteQuestionsByTopic(topicId: Long)

    @Query("DELETE FROM questions")
    suspend fun deleteAllQuestions()
}
