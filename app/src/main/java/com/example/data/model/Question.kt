package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

/**
 * Entidade protegida de pergunta (armazenada de forma segura pelo backend/engine).
 * O aplicativo cliente comum nunca recebe esta classe completa com correctOptionIndex durante a partida.
 */
@Entity(tableName = "quiz_questions")
@JsonClass(generateAdapter = true)
data class Question(
    @PrimaryKey val id: String,
    val categoryId: String,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOptionIndex: Int, // 0..3 (Somente o Backend/Admin conhece)
    val explanation: String,
    val difficulty: Int = 1, // 1: Fácil, 2: Médio, 3: Difícil
    val isCustomAdmin: Boolean = false,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toOptionsList(): List<String> = listOf(optionA, optionB, optionC, optionD)

    /**
     * Converte para formato higienizado seguro para o frontend do jogador
     * O índice correto é estritamente omitido!
     */
    fun toClientQuestion(): ClientQuestion {
        return ClientQuestion(
            id = id,
            categoryId = categoryId,
            questionText = questionText,
            options = toOptionsList(),
            difficulty = difficulty
        )
    }
}

/**
 * Modelo de pergunta entregue ao cliente de jogo.
 * Não possui o índice da resposta correta para evitar trapaças e engenharia reversa.
 */
@JsonClass(generateAdapter = true)
data class ClientQuestion(
    val id: String,
    val categoryId: String,
    val questionText: String,
    val options: List<String>,
    val difficulty: Int
)

/**
 * Requisição de validação enviada ao backend
 */
data class AnswerSubmission(
    val sessionToken: String,
    val questionId: String,
    val selectedOptionIndex: Int, // 0..3
    val responseTimeMs: Long // Tempo gasto pelo usuário para responder
)

/**
 * Resposta de validação calculada no backend seguro
 */
data class AnswerValidationResult(
    val isCorrect: Boolean,
    val pointsEarned: Int,
    val bonusStreakPoints: Int,
    val speedBonusPoints: Int,
    val totalScoreEarned: Int,
    val currentStreak: Int,
    val correctOptionIndex: Int, // Revelado somente após a submissão
    val explanation: String,
    val remainingLives: Int,
    val isGameOver: Boolean
)
