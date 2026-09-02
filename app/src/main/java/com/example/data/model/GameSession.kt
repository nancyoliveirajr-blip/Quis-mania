package com.example.data.model

import java.util.UUID

enum class GameMode(val displayName: String, val description: String, val defaultQuestionsCount: Int) {
    CLASSIC("Modo Clássico", "10 perguntas da categoria escolhida", 10),
    DAILY_CHALLENGE("Desafio Diário", "Perguntas especiais com bônus de XP e moedas", 10),
    SURVIVAL("Maratona Sobrevivência", "Responda sem errar, cada erro consome uma vida", 20),
    RANKED("Duelo Ranqueado", "Dispute pontos de ranking e suba de elo", 10)
}

/**
 * Sessão de Jogo assinada pelo Backend.
 * Controla tempo de expiração, anti-cheat e estado da partida.
 */
data class GameSession(
    val sessionToken: String = UUID.randomUUID().toString(),
    val userId: String,
    val categoryId: String,
    val mode: GameMode,
    val questions: List<ClientQuestion>,
    val totalQuestions: Int,
    val startedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (10 * 60 * 1000), // 10 minutos max
    var currentQuestionIndex: Int = 0,
    var score: Int = 0,
    var correctAnswersCount: Int = 0,
    var wrongAnswersCount: Int = 0,
    var currentStreak: Int = 0,
    var maxStreak: Int = 0,
    var livesRemaining: Int = 3,
    var isCompleted: Boolean = false,
    var isAntiCheatTriggered: Boolean = false
) {
    fun isExpired(): Boolean = System.currentTimeMillis() > expiresAt
}

/**
 * Resumo final da partida calculado no backend
 */
data class GameSessionSummary(
    val sessionToken: String,
    val categoryId: String,
    val mode: GameMode,
    val totalQuestions: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val finalScore: Int,
    val maxStreak: Int,
    val accuracyPercentage: Int,
    val xpEarned: Int,
    val coinsEarned: Int,
    val isNewRecord: Boolean,
    val timeSpentSeconds: Int,
    val answeredReviewList: List<QuestionReviewItem>
)

data class QuestionReviewItem(
    val questionText: String,
    val userSelectedOption: String,
    val correctOption: String,
    val isCorrect: Boolean,
    val explanation: String
)
