package com.cisco.quizapp.data.local.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.room.Room
import com.cisco.quizapp.data.local.QuizDatabase
import com.cisco.quizapp.data.local.entity.Question
import com.cisco.quizapp.data.local.entity.Topic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class QuestionDaoTest {

    private lateinit var database: QuizDatabase
    private lateinit var questionDao: QuestionDao
    private lateinit var topicDao: TopicDao

    /** IDs inserted in setUp so tests can reference them immediately. */
    private var tcpTopicId: Long = 0L
    private var udpTopicId: Long = 0L

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun question(
        topicId: Long,
        text: String = "Q?",
        correct: String = "A",
        difficulty: Int = 1
    ) = Question(
        topicId = topicId,
        questionText = text,
        optionA = "Option A",
        optionB = "Option B",
        optionC = "Option C",
        optionD = "Option D",
        correctAnswer = correct,
        explanation = "Because $correct.",
        difficulty = difficulty
    )

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Before
    fun createDb() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, QuizDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        topicDao = database.topicDao()
        questionDao = database.questionDao()

        // Pre-insert parent topics referenced by most tests
        tcpTopicId = topicDao.insertTopic(
            Topic(name = "TCP", description = "", iconName = "ic_tcp", difficultyLevel = 2)
        )
        udpTopicId = topicDao.insertTopic(
            Topic(name = "UDP", description = "", iconName = "ic_udp", difficultyLevel = 1)
        )
    }

    @After
    fun closeDb() = database.close()

    // ── Insert / Read ─────────────────────────────────────────────────────────

    @Test
    fun insertQuestion_getById_returnsMatchingQuestion() = runTest {
        val id = questionDao.insertQuestion(question(tcpTopicId, text = "What is TCP?", correct = "B"))

        val loaded = questionDao.getQuestionById(id)

        assertNotNull(loaded)
        assertEquals("What is TCP?", loaded!!.questionText)
        assertEquals("B", loaded.correctAnswer)
        assertEquals(tcpTopicId, loaded.topicId)
    }

    @Test
    fun getQuestionById_unknownId_returnsNull() = runTest {
        val result = questionDao.getQuestionById(99999L)

        assertNull(result)
    }

    @Test
    fun getAllQuestions_emptyTable_returnsEmptyList() = runTest {
        val result = questionDao.getAllQuestions().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun insertQuestions_getAllQuestions_returnsAll() = runTest {
        questionDao.insertQuestions(listOf(
            question(tcpTopicId, text = "Q1"),
            question(tcpTopicId, text = "Q2"),
            question(udpTopicId, text = "Q3")
        ))

        val all = questionDao.getAllQuestions().first()

        assertEquals(3, all.size)
    }

    // ── Topic filtering ───────────────────────────────────────────────────────

    @Test
    fun getQuestionsByTopic_returnsOnlyThatTopicsQuestions() = runTest {
        questionDao.insertQuestions(listOf(
            question(tcpTopicId, text = "TCP-1"),
            question(tcpTopicId, text = "TCP-2"),
            question(udpTopicId, text = "UDP-1")
        ))

        val tcpQuestions = questionDao.getQuestionsByTopic(tcpTopicId).first()

        assertEquals(2, tcpQuestions.size)
        assertTrue(tcpQuestions.all { it.topicId == tcpTopicId })
    }

    @Test
    fun getQuestionsByTopic_unknownTopic_returnsEmptyList() = runTest {
        questionDao.insertQuestion(question(tcpTopicId))

        val result = questionDao.getQuestionsByTopic(999L).first()

        assertTrue(result.isEmpty())
    }

    // ── Difficulty filtering ──────────────────────────────────────────────────

    @Test
    fun getQuestionsByTopicAndDifficulty_filtersCorrectly() = runTest {
        questionDao.insertQuestions(listOf(
            question(tcpTopicId, text = "easy-1",   difficulty = 1),
            question(tcpTopicId, text = "easy-2",   difficulty = 1),
            question(tcpTopicId, text = "medium-1", difficulty = 2),
            question(tcpTopicId, text = "hard-1",   difficulty = 3)
        ))

        val easy   = questionDao.getQuestionsByTopicAndDifficulty(tcpTopicId, 1).first()
        val medium = questionDao.getQuestionsByTopicAndDifficulty(tcpTopicId, 2).first()
        val hard   = questionDao.getQuestionsByTopicAndDifficulty(tcpTopicId, 3).first()

        assertEquals(2, easy.size)
        assertTrue(easy.all { it.difficulty == 1 })

        assertEquals(1, medium.size)
        assertEquals("medium-1", medium[0].questionText)

        assertEquals(1, hard.size)
        assertEquals("hard-1", hard[0].questionText)
    }

    // ── Random queries ────────────────────────────────────────────────────────

    @Test
    fun getRandomQuestionsForTopic_limitsReturnCount() = runTest {
        // Insert 5 questions for TCP
        questionDao.insertQuestions(List(5) { i -> question(tcpTopicId, text = "Q$i") })

        val result = questionDao.getRandomQuestionsForTopic(tcpTopicId, limit = 3)

        assertEquals(3, result.size)
        assertTrue(result.all { it.topicId == tcpTopicId })
    }

    @Test
    fun getRandomQuestionsForTopic_limitGreaterThanAvailable_returnsAllAvailable() = runTest {
        questionDao.insertQuestions(List(2) { i -> question(tcpTopicId, text = "Q$i") })

        val result = questionDao.getRandomQuestionsForTopic(tcpTopicId, limit = 10)

        assertEquals(2, result.size)
    }

    @Test
    fun getRandomQuestionsByDifficulty_returnsCorrectDifficulty() = runTest {
        questionDao.insertQuestions(listOf(
            question(tcpTopicId, difficulty = 1),
            question(tcpTopicId, difficulty = 1),
            question(udpTopicId, difficulty = 2),
            question(udpTopicId, difficulty = 3)
        ))

        val easyQuestions = questionDao.getRandomQuestionsByDifficulty(difficulty = 1, limit = 5)

        assertEquals(2, easyQuestions.size)
        assertTrue(easyQuestions.all { it.difficulty == 1 })
    }

    // ── Count ─────────────────────────────────────────────────────────────────

    @Test
    fun getQuestionCountForTopic_countsCorrectly() = runTest {
        assertEquals(0, questionDao.getQuestionCountForTopic(tcpTopicId))

        questionDao.insertQuestions(listOf(
            question(tcpTopicId),
            question(tcpTopicId),
            question(udpTopicId)
        ))

        assertEquals(2, questionDao.getQuestionCountForTopic(tcpTopicId))
        assertEquals(1, questionDao.getQuestionCountForTopic(udpTopicId))
    }

    @Test
    fun getTotalQuestionCount_sumsAcrossAllTopics() = runTest {
        questionDao.insertQuestions(listOf(
            question(tcpTopicId),
            question(tcpTopicId),
            question(udpTopicId)
        ))

        assertEquals(3, questionDao.getTotalQuestionCount())
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Test
    fun updateQuestion_persistsNewValues() = runTest {
        val id = questionDao.insertQuestion(
            question(tcpTopicId, text = "Original?", correct = "A", difficulty = 1)
        )
        val existing = questionDao.getQuestionById(id)!!

        questionDao.updateQuestion(
            existing.copy(questionText = "Updated?", correctAnswer = "D", difficulty = 3)
        )

        val reloaded = questionDao.getQuestionById(id)!!
        assertEquals("Updated?", reloaded.questionText)
        assertEquals("D", reloaded.correctAnswer)
        assertEquals(3, reloaded.difficulty)
        // topicId must not drift
        assertEquals(tcpTopicId, reloaded.topicId)
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Test
    fun deleteQuestion_removesOnlyTargetRow() = runTest {
        val id1 = questionDao.insertQuestion(question(tcpTopicId, text = "Q1"))
        val id2 = questionDao.insertQuestion(question(tcpTopicId, text = "Q2"))
        val target = questionDao.getQuestionById(id1)!!

        questionDao.deleteQuestion(target)

        assertNull(questionDao.getQuestionById(id1))
        assertNotNull(questionDao.getQuestionById(id2))
    }

    @Test
    fun deleteQuestionsByTopic_removesAllQuestionsForThatTopic() = runTest {
        questionDao.insertQuestions(listOf(
            question(tcpTopicId, text = "TCP-1"),
            question(tcpTopicId, text = "TCP-2"),
            question(udpTopicId, text = "UDP-1")
        ))

        questionDao.deleteQuestionsByTopic(tcpTopicId)

        assertEquals(0, questionDao.getQuestionCountForTopic(tcpTopicId))
        assertEquals(1, questionDao.getQuestionCountForTopic(udpTopicId)) // UDP unaffected
    }

    @Test
    fun deleteAllQuestions_clearsEntireTable() = runTest {
        questionDao.insertQuestions(List(5) { question(tcpTopicId) })

        questionDao.deleteAllQuestions()

        assertEquals(0, questionDao.getTotalQuestionCount())
    }

    // ── Foreign key / CASCADE ─────────────────────────────────────────────────

    @Test
    fun deleteTopic_cascadeDeletesItsQuestions() = runTest {
        questionDao.insertQuestions(listOf(
            question(tcpTopicId, text = "TCP-1"),
            question(tcpTopicId, text = "TCP-2"),
            question(udpTopicId, text = "UDP-1")
        ))

        // Delete the parent topic — FK CASCADE should remove its questions
        topicDao.deleteTopicById(tcpTopicId)

        assertEquals(0, questionDao.getQuestionCountForTopic(tcpTopicId))
        assertEquals(1, questionDao.getTotalQuestionCount()) // only UDP question survives
    }
}
