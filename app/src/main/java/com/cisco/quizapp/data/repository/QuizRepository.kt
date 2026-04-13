package com.cisco.quizapp.data.repository

import com.cisco.quizapp.data.local.dao.QuestionDao
import com.cisco.quizapp.data.local.dao.TopicDao
import com.cisco.quizapp.data.local.entity.Question
import com.cisco.quizapp.data.local.entity.Topic
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepository @Inject constructor(
    private val topicDao: TopicDao,
    private val questionDao: QuestionDao
) {

    // ── Topics ────────────────────────────────────────────────────────────────

    fun getAllTopics(): Flow<List<Topic>> = topicDao.getAllTopics()

    suspend fun getTopicById(id: Long): Topic? = topicDao.getTopicById(id)

    fun getTopicsByDifficulty(level: Int): Flow<List<Topic>> =
        topicDao.getTopicsByDifficulty(level)

    suspend fun getTopicCount(): Int = topicDao.getTopicCount()

    suspend fun insertTopic(topic: Topic): Long = topicDao.insertTopic(topic)

    suspend fun insertTopics(topics: List<Topic>): List<Long> =
        topicDao.insertTopics(topics)

    suspend fun updateTopic(topic: Topic) = topicDao.updateTopic(topic)

    suspend fun deleteTopic(topic: Topic) = topicDao.deleteTopic(topic)

    suspend fun deleteTopicById(id: Long) = topicDao.deleteTopicById(id)

    suspend fun deleteAllTopics() = topicDao.deleteAllTopics()

    // ── Questions ─────────────────────────────────────────────────────────────

    fun getAllQuestions(): Flow<List<Question>> = questionDao.getAllQuestions()

    suspend fun getQuestionById(id: Long): Question? = questionDao.getQuestionById(id)

    fun getQuestionsByTopic(topicId: Long): Flow<List<Question>> =
        questionDao.getQuestionsByTopic(topicId)

    fun getQuestionsByTopicAndDifficulty(
        topicId: Long,
        difficulty: Int
    ): Flow<List<Question>> = questionDao.getQuestionsByTopicAndDifficulty(topicId, difficulty)

    suspend fun getRandomQuestionsForTopic(topicId: Long, limit: Int): List<Question> =
        questionDao.getRandomQuestionsForTopic(topicId, limit)

    suspend fun getRandomQuestionsByDifficulty(difficulty: Int, limit: Int): List<Question> =
        questionDao.getRandomQuestionsByDifficulty(difficulty, limit)

    suspend fun getQuestionCountForTopic(topicId: Long): Int =
        questionDao.getQuestionCountForTopic(topicId)

    suspend fun getTotalQuestionCount(): Int = questionDao.getTotalQuestionCount()

    suspend fun insertQuestion(question: Question): Long =
        questionDao.insertQuestion(question)

    suspend fun insertQuestions(questions: List<Question>): List<Long> =
        questionDao.insertQuestions(questions)

    suspend fun updateQuestion(question: Question) = questionDao.updateQuestion(question)

    suspend fun deleteQuestion(question: Question) = questionDao.deleteQuestion(question)

    suspend fun deleteQuestionById(id: Long) = questionDao.deleteQuestionById(id)

    suspend fun deleteQuestionsByTopic(topicId: Long) =
        questionDao.deleteQuestionsByTopic(topicId)

    suspend fun deleteAllQuestions() = questionDao.deleteAllQuestions()
}
