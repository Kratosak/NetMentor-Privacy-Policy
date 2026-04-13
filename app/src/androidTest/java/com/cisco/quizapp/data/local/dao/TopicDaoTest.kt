package com.cisco.quizapp.data.local.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.room.Room
import com.cisco.quizapp.data.local.QuizDatabase
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
class TopicDaoTest {

    private lateinit var database: QuizDatabase
    private lateinit var dao: TopicDao

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun topic(
        name: String,
        difficulty: Int = 1,
        description: String = "desc",
        icon: String = "ic_test"
    ) = Topic(name = name, description = description, iconName = icon, difficultyLevel = difficulty)

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, QuizDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.topicDao()
    }

    @After
    fun closeDb() = database.close()

    // ── Insert / Read ─────────────────────────────────────────────────────────

    @Test
    fun insertTopic_getById_returnsMatchingTopic() = runTest {
        val id = dao.insertTopic(topic("TCP", difficulty = 2))

        val loaded = dao.getTopicById(id)

        assertNotNull(loaded)
        assertEquals("TCP", loaded!!.name)
        assertEquals(2, loaded.difficultyLevel)
    }

    @Test
    fun getTopicById_unknownId_returnsNull() = runTest {
        val result = dao.getTopicById(999L)

        assertNull(result)
    }

    @Test
    fun insertTopics_getAllTopics_orderedAlphabetically() = runTest {
        dao.insertTopics(listOf(
            topic("UDP"),
            topic("DNS"),
            topic("TCP")
        ))

        val names = dao.getAllTopics().first().map { it.name }

        assertEquals(listOf("DNS", "TCP", "UDP"), names)
    }

    @Test
    fun getAllTopics_emptyTable_returnsEmptyList() = runTest {
        val result = dao.getAllTopics().first()

        assertTrue(result.isEmpty())
    }

    // ── Filtering ─────────────────────────────────────────────────────────────

    @Test
    fun getTopicsByDifficulty_returnsOnlyMatchingLevel() = runTest {
        dao.insertTopics(listOf(
            topic("UDP",        difficulty = 1),
            topic("TCP",        difficulty = 2),
            topic("Subnetting", difficulty = 3),
            topic("OSI Model",  difficulty = 2)
        ))

        val easy        = dao.getTopicsByDifficulty(1).first()
        val intermediate = dao.getTopicsByDifficulty(2).first()
        val hard        = dao.getTopicsByDifficulty(3).first()

        assertEquals(1, easy.size)
        assertEquals("UDP", easy[0].name)

        assertEquals(2, intermediate.size)
        val intermediateNames = intermediate.map { it.name }
        assertTrue(intermediateNames.containsAll(listOf("TCP", "OSI Model")))

        assertEquals(1, hard.size)
        assertEquals("Subnetting", hard[0].name)
    }

    // ── Count ─────────────────────────────────────────────────────────────────

    @Test
    fun getTopicCount_reflectsInsertions() = runTest {
        assertEquals(0, dao.getTopicCount())

        dao.insertTopic(topic("TCP"))
        assertEquals(1, dao.getTopicCount())

        dao.insertTopic(topic("UDP"))
        assertEquals(2, dao.getTopicCount())
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Test
    fun updateTopic_persistsNewValues() = runTest {
        val id = dao.insertTopic(topic("TCP", difficulty = 1, description = "old"))
        val existing = dao.getTopicById(id)!!

        dao.updateTopic(existing.copy(description = "updated", difficultyLevel = 3))

        val reloaded = dao.getTopicById(id)!!
        assertEquals("updated", reloaded.description)
        assertEquals(3, reloaded.difficultyLevel)
        // Name must not change
        assertEquals("TCP", reloaded.name)
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Test
    fun deleteTopic_removesRowFromDatabase() = runTest {
        val id = dao.insertTopic(topic("TCP"))
        val inserted = dao.getTopicById(id)!!

        dao.deleteTopic(inserted)

        assertNull(dao.getTopicById(id))
    }

    @Test
    fun deleteTopicById_removesCorrectRow() = runTest {
        val id1 = dao.insertTopic(topic("TCP"))
        val id2 = dao.insertTopic(topic("UDP"))

        dao.deleteTopicById(id1)

        assertNull(dao.getTopicById(id1))
        assertNotNull(dao.getTopicById(id2))
    }

    @Test
    fun deleteAllTopics_clearsEntireTable() = runTest {
        dao.insertTopics(listOf(topic("TCP"), topic("UDP"), topic("DNS")))

        dao.deleteAllTopics()

        assertEquals(0, dao.getTopicCount())
        assertTrue(dao.getAllTopics().first().isEmpty())
    }

    // ── REPLACE conflict strategy ─────────────────────────────────────────────

    @Test
    fun insertTopic_withSameId_replacesExistingRow() = runTest {
        val id = dao.insertTopic(topic("TCP", difficulty = 1))
        dao.insertTopic(Topic(id = id, name = "TCP-v2", description = "replaced", iconName = "ic_v2", difficultyLevel = 3))

        val loaded = dao.getTopicById(id)!!
        assertEquals("TCP-v2", loaded.name)
        assertEquals(3, loaded.difficultyLevel)
        assertEquals(1, dao.getTopicCount()) // still only one row
    }
}
