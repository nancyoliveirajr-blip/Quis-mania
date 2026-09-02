package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "match_history")
@JsonClass(generateAdapter = true)
data class MatchResult(
    @PrimaryKey val matchId: String,
    val userId: String,
    val categoryId: String,
    val modeName: String,
    val score: Int,
    val correctAnswers: Int,
    val totalQuestions: Int,
    val accuracy: Int,
    val xpGained: Int,
    val coinsGained: Int,
    val timeSpentSeconds: Int,
    val playedAt: Long = System.currentTimeMillis()
)
