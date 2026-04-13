package com.cisco.quizapp.di

import android.content.Context
import androidx.room.Room
import com.cisco.quizapp.data.local.QuizDatabase
import com.cisco.quizapp.data.local.dao.QuestionDao
import com.cisco.quizapp.data.local.dao.TopicDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideQuizDatabase(@ApplicationContext context: Context): QuizDatabase =
        Room.databaseBuilder(
            context,
            QuizDatabase::class.java,
            QuizDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun provideTopicDao(db: QuizDatabase): TopicDao = db.topicDao()

    @Provides
    @Singleton
    fun provideQuestionDao(db: QuizDatabase): QuestionDao = db.questionDao()
}
