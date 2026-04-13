package com.cisco.quizapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topics")
data class Topic(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val iconName: String,
    /** 1 = Beginner, 2 = Intermediate, 3 = Advanced */
    val difficultyLevel: Int
)
