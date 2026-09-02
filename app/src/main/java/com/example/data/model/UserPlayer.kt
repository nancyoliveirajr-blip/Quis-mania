package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "user_players")
@JsonClass(generateAdapter = true)
data class UserPlayer(
    @PrimaryKey val id: String = "local_player_1",
    val username: String = "Mestre Quiz",
    val email: String = "jogador@quizmania.app",
    val level: Int = 1,
    val currentXp: Int = 0,
    val xpToNextLevel: Int = 500,
    val coins: Int = 150,
    val lives: Int = 5,
    val maxLives: Int = 5,
    val lastLifeRefillTimestamp: Long = System.currentTimeMillis(),
    val isVip: Boolean = false,
    val vipExpiryTimestamp: Long? = null,
    val totalMatchesPlayed: Int = 0,
    val totalWins: Int = 0,
    val totalScore: Int = 0,
    val bestStreak: Int = 0,
    val rankingDivision: String = "Bronze",
    val rankingPoints: Int = 100,
    val dailyStreak: Int = 1,
    val lastDailyChallengeDate: String = "",
    val powerUpFiftyFifty: Int = 3,
    val powerUpSkip: Int = 2,
    val powerUpTimeFreeze: Int = 2,
    val isBanned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getLevelTitle(): String {
        return when {
            level >= 50 -> "Grão-Mestre Quiz"
            level >= 35 -> "Gênio Enciclopédico"
            level >= 20 -> "Especialista Mania"
            level >= 10 -> "Competidor Ouro"
            level >= 5 -> "Explorador de Saberes"
            else -> "Iniciante Curioso"
        }
    }
}
