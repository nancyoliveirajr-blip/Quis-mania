package com.example.data.repository

import com.example.data.backend.QuizBackendEngine
import com.example.data.local.QuizDao
import com.example.data.local.SecurityLogDao
import com.example.data.local.UserDao
import com.example.data.model.AdSystemConfig
import com.example.data.model.AnswerSubmission
import com.example.data.model.AnswerValidationResult
import com.example.data.model.DailyChallengeConfig
import com.example.data.model.GameMode
import com.example.data.model.GameSession
import com.example.data.model.GameSessionSummary
import com.example.data.model.MatchResult
import com.example.data.model.Question
import com.example.data.model.SecurityLog
import com.example.data.model.SystemAnalytics
import com.example.data.model.UserPlayer
import kotlinx.coroutines.flow.Flow

class QuizRepository(
    val backendEngine: QuizBackendEngine,
    val userDao: UserDao,
    val quizDao: QuizDao,
    val securityLogDao: SecurityLogDao,
    val billingRepository: BillingRepository
) {
    // Flows para observação de estado reativo
    fun getUserFlow(userId: String = "local_player_1"): Flow<UserPlayer?> = userDao.getUserFlow(userId)

    fun getMatchHistoryFlow(): Flow<List<MatchResult>> = userDao.getMatchHistoryFlow()

    fun getAllQuestionsFlow(): Flow<List<Question>> = quizDao.getAllActiveQuestionsFlow()

    fun getSecurityLogsFlow(): Flow<List<SecurityLog>> = securityLogDao.getAllLogsFlow()

    // Partidas e jogo
    suspend fun startMatch(categoryId: String, mode: GameMode, userId: String = "local_player_1"): Result<GameSession> {
        return backendEngine.createMatchSession(userId = userId, categoryId = categoryId, mode = mode)
    }

    suspend fun submitAnswer(submission: AnswerSubmission): Result<AnswerValidationResult> {
        return backendEngine.submitAnswer(submission)
    }

    suspend fun finishMatch(sessionToken: String, totalTimeSeconds: Int): Result<GameSessionSummary> {
        return backendEngine.completeMatchSession(sessionToken, totalTimeSeconds)
    }

    suspend fun consumePowerUp(powerUpType: String, userId: String = "local_player_1"): Boolean {
        val user = userDao.getUser(userId) ?: return false
        val updated = when (powerUpType) {
            "FIFTY_FIFTY" -> if (user.powerUpFiftyFifty > 0) user.copy(powerUpFiftyFifty = user.powerUpFiftyFifty - 1) else return false
            "SKIP" -> if (user.powerUpSkip > 0) user.copy(powerUpSkip = user.powerUpSkip - 1) else return false
            "FREEZE" -> if (user.powerUpTimeFreeze > 0) user.copy(powerUpTimeFreeze = user.powerUpTimeFreeze - 1) else return false
            else -> return false
        }
        userDao.updateUser(updated)
        return true
    }

    // Administração
    fun verifyAdminAuth(pin: String): Boolean = backendEngine.verifyAdminAuth(pin)

    suspend fun getSystemAnalytics(): SystemAnalytics = backendEngine.getSystemAnalytics()

    suspend fun addQuestion(question: Question): Result<Unit> = backendEngine.adminAddQuestion(question)

    suspend fun updateQuestion(question: Question): Result<Unit> = backendEngine.adminUpdateQuestion(question)

    suspend fun deleteQuestion(questionId: String): Result<Unit> = backendEngine.adminDeleteQuestion(questionId)

    suspend fun updateUser(user: UserPlayer): Result<Unit> = backendEngine.adminUpdateUser(user)

    fun getDailyChallengeConfig(): DailyChallengeConfig = backendEngine.dailyChallengeConfig

    fun updateDailyChallenge(config: DailyChallengeConfig) = backendEngine.updateDailyChallenge(config)

    fun getAdConfig(): AdSystemConfig = backendEngine.adConfig

    fun updateAdConfig(config: AdSystemConfig) = backendEngine.updateAdConfig(config)
}
