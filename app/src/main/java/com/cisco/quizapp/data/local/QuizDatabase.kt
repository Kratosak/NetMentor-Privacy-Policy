package com.cisco.quizapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cisco.quizapp.data.local.dao.QuestionDao
import com.cisco.quizapp.data.local.dao.TopicDao
import com.cisco.quizapp.data.local.entity.Question
import com.cisco.quizapp.data.local.entity.Topic

@Database(
    entities = [Topic::class, Question::class],
    version = 2,
    exportSchema = false
)
abstract class QuizDatabase : RoomDatabase() {
    abstract fun topicDao(): TopicDao
    abstract fun questionDao(): QuestionDao

    companion object {
        const val DATABASE_NAME = "quiz_database"
    }
}
