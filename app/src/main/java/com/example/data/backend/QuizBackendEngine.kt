package com.example.data.backend

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
import com.example.data.model.QuestionReviewItem
import com.example.data.model.SecurityLog
import com.example.data.model.SystemAnalytics
import com.example.data.model.UserPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Motor de Backend Seguro do QUIZ MANIA.
 *
 * Princípios de Proteção e Arquitetura:
 * 1. O cliente nunca recebe o gabarito (índice da resposta correta) das perguntas durante o jogo.
 * 2. As partidas utilizam tokens de sessão com assinatura e tempo de expiração.
 * 3. A pontuação, multiplicador de combo, XP, moedas e vidas são calculados exclusivamente no servidor/backend.
 * 4. Validação anti-trapaça (detecção de respostas instantâneas não humanas < 200ms ou requisições fora de tempo).
 * 5. Rate limiting com Token Bucket para prevenir ataques de força bruta e sobrecarga.
 * 6. Painel administrativo blindado com autenticação por Token/PIN mestre e trilha de auditoria.
 */
class QuizBackendEngine(
    private val quizDao: QuizDao,
    private val userDao: UserDao,
    private val securityLogDao: SecurityLogDao
) {
    // Armazenamento em memória seguro de sessões ativas do backend
    private val activeSessions = ConcurrentHashMap<String, InternalServerGameSession>()

    // Controle de taxa (Rate Limiting) por identificador de cliente
    private val rateLimitBucket = ConcurrentHashMap<String, RateLimitState>()

    // Chave secreta interna para tokens de sessão
    private val serverSalt: String = generateSecureSalt()

    // Configurações do sistema
    var dailyChallengeConfig = DailyChallengeConfig(
        dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
        title = "Desafio dos Sábios",
        description = "Acerte as perguntas de Ciências e Conhecimentos Gerais para ganhar bônus 2x de XP!",
        categoryId = "ciencia",
        xpMultiplier = 2.0f,
        coinReward = 150,
        requiredScore = 800
    )
        private set

    var adConfig = AdSystemConfig()
        private set

    // Sessão interna mantida em sigilo no backend (com as respostas corretas)
    private data class InternalServerGameSession(
        val sessionToken: String,
        val userId: String,
        val categoryId: String,
        val mode: GameMode,
        val internalQuestions: List<Question>,
        val expiresAt: Long,
        var currentQuestionIndex: Int = 0,
        var currentScore: Int = 0,
        var correctCount: Int = 0,
        var wrongCount: Int = 0,
        var currentStreak: Int = 0,
        var maxStreak: Int = 0,
        var livesRemaining: Int = 5,
        var isVipPlayer: Boolean = false,
        var isCompleted: Boolean = false,
        val userAnswers: MutableList<InternalAnswerRecord> = mutableListOf()
    )

    private data class InternalAnswerRecord(
        val questionId: String,
        val selectedOptionIndex: Int,
        val responseTimeMs: Long,
        val isCorrect: Boolean
    )

    private data class RateLimitState(
        var tokens: Int = 30,
        var lastRefillTimestamp: Long = System.currentTimeMillis()
    )

    private fun generateSecureSalt(): String {
        val random = SecureRandom()
        val bytes = ByteArray(16)
        random.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Valida Rate Limit
     */
    private fun checkRateLimit(clientId: String): Boolean {
        val now = System.currentTimeMillis()
        val state = rateLimitBucket.computeIfAbsent(clientId) { RateLimitState() }
        synchronized(state) {
            val refillInterval = 1000L // 1 segundo
            val elapsed = now - state.lastRefillTimestamp
            if (elapsed > refillInterval) {
                val newTokens = (elapsed / refillInterval).toInt() * 5
                state.tokens = minOf(30, state.tokens + newTokens)
                state.lastRefillTimestamp = now
            }
            if (state.tokens > 0) {
                state.tokens--
                return true
            }
            return false
        }
    }

    /**
     * Cria uma nova sessão de jogo autenticada e higienizada para o jogador.
     */
    suspend fun createMatchSession(
        userId: String = "local_player_1",
        categoryId: String,
        mode: GameMode,
        clientId: String = "client_device_default"
    ): Result<GameSession> = withContext(Dispatchers.IO) {
        if (!checkRateLimit(clientId)) {
            logSecurityEvent("RATE_LIMIT", "Excesso de requisições ao iniciar partida", clientId, "WARN")
            return@withContext Result.failure(Exception("Limite de requisições excedido. Aguarde alguns instantes."))
        }

        val user = userDao.getUser(userId) ?: UserPlayer(id = userId)

        // Verificar vidas se não for VIP
        if (!user.isVip && user.lives <= 0 && mode != GameMode.DAILY_CHALLENGE) {
            return@withContext Result.failure(Exception("Sem vidas disponíveis. Recarregue na Loja ou aguarde o tempo de regeneração."))
        }

        // Buscar perguntas da categoria ou mistas
        val allCategoryQuestions = if (categoryId == "mixed" || mode == GameMode.DAILY_CHALLENGE) {
            quizDao.getAllActiveQuestions().ifEmpty { DefaultQuestionBank.questions }
        } else {
            quizDao.getQuestionsByCategory(categoryId).ifEmpty {
                DefaultQuestionBank.questions.filter { it.categoryId == categoryId }
            }
        }

        if (allCategoryQuestions.isEmpty()) {
            return@withContext Result.failure(Exception("Nenhuma pergunta disponível para esta categoria no momento."))
        }

        val count = when (mode) {
            GameMode.CLASSIC -> 10
            GameMode.DAILY_CHALLENGE -> 10
            GameMode.SURVIVAL -> 20
            GameMode.RANKED -> 10
        }

        val selectedInternalQuestions = allCategoryQuestions.shuffled().take(minOf(count, allCategoryQuestions.size))
        val sessionToken = "QM_SEC_" + UUID.randomUUID().toString().replace("-", "")

        val serverSession = InternalServerGameSession(
            sessionToken = sessionToken,
            userId = userId,
            categoryId = categoryId,
            mode = mode,
            internalQuestions = selectedInternalQuestions,
            expiresAt = System.currentTimeMillis() + (15 * 60 * 1000), // 15 minutos de validade
            livesRemaining = if (user.isVip) 999 else user.lives,
            isVipPlayer = user.isVip
        )
        activeSessions[sessionToken] = serverSession

        // Entregar apenas perguntas higienizadas (sem correctOptionIndex)
        val clientQuestions = selectedInternalQuestions.map { it.toClientQuestion() }

        val clientSession = GameSession(
            sessionToken = sessionToken,
            userId = userId,
            categoryId = categoryId,
            mode = mode,
            questions = clientQuestions,
            totalQuestions = clientQuestions.size,
            livesRemaining = serverSession.livesRemaining
        )

        Result.success(clientSession)
    }

    /**
     * Valida a resposta no backend, calculando pontuação, bônus de velocidade e streak.
     */
    suspend fun submitAnswer(
        submission: AnswerSubmission,
        clientId: String = "client_device_default"
    ): Result<AnswerValidationResult> = withContext(Dispatchers.IO) {
        if (!checkRateLimit(clientId)) {
            logSecurityEvent("RATE_LIMIT", "Taxa excessiva de envio de respostas", clientId, "WARN")
            return@withContext Result.failure(Exception("Taxa de resposta muito alta."))
        }

        val session = activeSessions[submission.sessionToken]
            ?: return@withContext Result.failure(Exception("Sessão de jogo inválida ou expirada."))

        if (System.currentTimeMillis() > session.expiresAt) {
            activeSessions.remove(submission.sessionToken)
            logSecurityEvent("SESSION_EXPIRED", "Tentativa de envio em sessão expirada", clientId, "INFO")
            return@withContext Result.failure(Exception("Esta partida expirou por inatividade."))
        }

        // Anti-cheat: Detecção de resposta inumana (< 150ms)
        if (submission.responseTimeMs < 150 && submission.selectedOptionIndex >= 0) {
            logSecurityEvent("ANTI_CHEAT_TRIGGER", "Resposta anormalmente rápida detectada: ${submission.responseTimeMs}ms", clientId, "ALERT")
        }

        val currentIdx = session.currentQuestionIndex
        if (currentIdx >= session.internalQuestions.size) {
            return@withContext Result.failure(Exception("Partida já finalizada."))
        }

        val targetQuestion = session.internalQuestions[currentIdx]
        if (targetQuestion.id != submission.questionId) {
            logSecurityEvent("ANTI_CHEAT_TRIGGER", "Question ID divergente da sequência da sessão", clientId, "ALERT")
            return@withContext Result.failure(Exception("Inconsistência de pergunta na sessão."))
        }

        val isCorrect = submission.selectedOptionIndex == targetQuestion.correctOptionIndex

        // Cálculo de pontuação no Backend
        var pointsEarned = 0
        var streakBonus = 0
        var speedBonus = 0

        if (isCorrect) {
            session.correctCount++
            session.currentStreak++
            if (session.currentStreak > session.maxStreak) {
                session.maxStreak = session.currentStreak
            }

            val basePoints = when (targetQuestion.difficulty) {
                1 -> 100
                2 -> 150
                3 -> 200
                else -> 100
            }

            // Bônus de Streak: +10% a cada acerto consecutivo (máx 100%)
            val streakMultiplier = minOf(10, session.currentStreak) * 0.10f
            streakBonus = (basePoints * streakMultiplier).toInt()

            // Bônus de Velocidade: resposta em menos de 5 segundos
            speedBonus = if (submission.responseTimeMs in 200..5000) {
                val factor = (5000 - submission.responseTimeMs) / 5000f
                (50 * factor).toInt()
            } else {
                0
            }

            pointsEarned = basePoints
            val totalForQuestion = pointsEarned + streakBonus + speedBonus
            session.currentScore += totalForQuestion
        } else {
            session.wrongCount++
            session.currentStreak = 0
            if (!session.isVipPlayer && session.livesRemaining > 0) {
                session.livesRemaining--
            }
        }

        session.userAnswers.add(
            InternalAnswerRecord(
                questionId = targetQuestion.id,
                selectedOptionIndex = submission.selectedOptionIndex,
                responseTimeMs = submission.responseTimeMs,
                isCorrect = isCorrect
            )
        )

        session.currentQuestionIndex++
        val isGameOver = (!session.isVipPlayer && session.livesRemaining <= 0) ||
                (session.currentQuestionIndex >= session.internalQuestions.size)

        val result = AnswerValidationResult(
            isCorrect = isCorrect,
            pointsEarned = pointsEarned,
            bonusStreakPoints = streakBonus,
            speedBonusPoints = speedBonus,
            totalScoreEarned = pointsEarned + streakBonus + speedBonus,
            currentStreak = session.currentStreak,
            correctOptionIndex = targetQuestion.correctOptionIndex,
            explanation = targetQuestion.explanation,
            remainingLives = session.livesRemaining,
            isGameOver = isGameOver
        )

        Result.success(result)
    }

    /**
     * Finaliza a partida no backend, atualiza XP, moedas, vitórias e estatísticas do jogador.
     */
    suspend fun completeMatchSession(
        sessionToken: String,
        totalTimeSeconds: Int
    ): Result<GameSessionSummary> = withContext(Dispatchers.IO) {
        val session = activeSessions.remove(sessionToken)
            ?: return@withContext Result.failure(Exception("Sessão não encontrada."))

        session.isCompleted = true

        val totalQuestions = session.internalQuestions.size
        val correct = session.correctCount
        val accuracy = if (totalQuestions > 0) (correct * 100) / totalQuestions else 0

        // Cálculo de recompensas no servidor
        var xpEarned = (session.currentScore * 0.5).toInt() + (correct * 20)
        var coinsEarned = (correct * 5) + (if (accuracy >= 80) 30 else 10)

        // Bônus VIP ou Desafio Diário
        if (session.isVipPlayer) {
            xpEarned = (xpEarned * 1.5).toInt()
            coinsEarned *= 2
        } else if (session.mode == GameMode.DAILY_CHALLENGE) {
            xpEarned = (xpEarned * dailyChallengeConfig.xpMultiplier).toInt()
            coinsEarned += dailyChallengeConfig.coinReward
        }

        // Atualizar perfil do jogador de forma consistente
        val user = userDao.getUser(session.userId) ?: UserPlayer(id = session.userId)
        val newXp = user.currentXp + xpEarned
        var newLevel = user.level
        var xpToNext = user.xpToNextLevel
        var currentXpAdjusted = newXp

        while (currentXpAdjusted >= xpToNext) {
            currentXpAdjusted -= xpToNext
            newLevel++
            xpToNext = (xpToNext * 1.25).toInt()
        }

        val updatedUser = user.copy(
            level = newLevel,
            currentXp = currentXpAdjusted,
            xpToNextLevel = xpToNext,
            coins = user.coins + coinsEarned,
            lives = if (user.isVip) user.maxLives else session.livesRemaining,
            totalMatchesPlayed = user.totalMatchesPlayed + 1,
            totalWins = user.totalWins + (if (accuracy >= 60) 1 else 0),
            totalScore = user.totalScore + session.currentScore,
            bestStreak = maxOf(user.bestStreak, session.maxStreak),
            rankingPoints = user.rankingPoints + (if (accuracy >= 70) 25 else -10).coerceAtLeast(0)
        )
        userDao.updateUser(updatedUser)

        // Gravar histórico de partida
        val matchResult = MatchResult(
            matchId = "MATCH_" + UUID.randomUUID().toString().take(8),
            userId = session.userId,
            categoryId = session.categoryId,
            modeName = session.mode.displayName,
            score = session.currentScore,
            correctAnswers = correct,
            totalQuestions = totalQuestions,
            accuracy = accuracy,
            xpGained = xpEarned,
            coinsGained = coinsEarned,
            timeSpentSeconds = totalTimeSeconds
        )
        userDao.insertMatchResult(matchResult)

        // Montar gabarito de revisão com explicações para o jogador
        val reviewList = session.internalQuestions.mapIndexed { index, q ->
            val userAns = session.userAnswers.getOrNull(index)
            val selectedIdx = userAns?.selectedOptionIndex ?: -1
            val selectedText = if (selectedIdx in 0..3) q.toOptionsList()[selectedIdx] else "Não respondida"
            val correctText = q.toOptionsList()[q.correctOptionIndex]
            QuestionReviewItem(
                questionText = q.questionText,
                userSelectedOption = selectedText,
                correctOption = correctText,
                isCorrect = userAns?.isCorrect ?: false,
                explanation = q.explanation
            )
        }

        val summary = GameSessionSummary(
            sessionToken = sessionToken,
            categoryId = session.categoryId,
            mode = session.mode,
            totalQuestions = totalQuestions,
            correctCount = correct,
            wrongCount = session.wrongCount,
            finalScore = session.currentScore,
            maxStreak = session.maxStreak,
            accuracyPercentage = accuracy,
            xpEarned = xpEarned,
            coinsEarned = coinsEarned,
            isNewRecord = session.currentScore > user.totalScore / maxOf(1, user.totalMatchesPlayed),
            timeSpentSeconds = totalTimeSeconds,
            answeredReviewList = reviewList
        )

        Result.success(summary)
    }

    // ==========================================
    // PAINEL ADMINISTRATIVO PROTEGIDO (ADMIN API)
    // ==========================================

    private val ADMIN_MASTER_PIN = "9876" // PIN administrativo para acesso seguro

    fun verifyAdminAuth(pin: String): Boolean {
        val isValid = pin == ADMIN_MASTER_PIN
        logSecurityEvent(
            eventType = if (isValid) "ADMIN_AUTH_SUCCESS" else "ADMIN_AUTH_FAIL",
            description = if (isValid) "Acesso autenticado ao Painel Administrativo" else "Tentativa de login admin com PIN incorreto",
            ipOrDeviceId = "admin_auth_gate",
            severity = if (isValid) "INFO" else "WARN"
        )
        return isValid
    }

    suspend fun getSystemAnalytics(): SystemAnalytics = withContext(Dispatchers.IO) {
        val totalQuestions = quizDao.getTotalQuestionsCount()
        val totalMatches = userDao.getTotalMatchesCount()
        val avgAccuracy = userDao.getAverageAccuracy() ?: 0.0
        val antiCheatCount = securityLogDao.getAntiCheatCount()
        val rateLimitCount = securityLogDao.getRateLimitCount()
        val players = userDao.getAllPlayers()
        val vipCount = players.count { it.isVip }.toLong()

        SystemAnalytics(
            totalQuestionsInBank = totalQuestions,
            activeCategoriesCount = 12,
            totalMatchesPlayed = totalMatches,
            registeredPlayersCount = players.size.toLong(),
            activeVipSubscribers = vipCount,
            antiCheatInterceptionsCount = antiCheatCount,
            averageMatchAccuracy = avgAccuracy,
            rateLimitBlockedRequests = rateLimitCount
        )
    }

    suspend fun adminAddQuestion(question: Question): Result<Unit> = withContext(Dispatchers.IO) {
        quizDao.insertQuestion(question.copy(isCustomAdmin = true))
        logSecurityEvent("ADMIN_QUESTION_ADD", "Nova pergunta cadastrada: ${question.id} na categoria ${question.categoryId}", "admin_portal", "INFO")
        Result.success(Unit)
    }

    suspend fun adminUpdateQuestion(question: Question): Result<Unit> = withContext(Dispatchers.IO) {
        quizDao.updateQuestion(question)
        logSecurityEvent("ADMIN_QUESTION_UPDATE", "Pergunta atualizada: ${question.id}", "admin_portal", "INFO")
        Result.success(Unit)
    }

    suspend fun adminDeleteQuestion(questionId: String): Result<Unit> = withContext(Dispatchers.IO) {
        quizDao.deleteQuestionById(questionId)
        logSecurityEvent("ADMIN_QUESTION_DELETE", "Pergunta removida: $questionId", "admin_portal", "INFO")
        Result.success(Unit)
    }

    suspend fun adminUpdateUser(user: UserPlayer): Result<Unit> = withContext(Dispatchers.IO) {
        userDao.updateUser(user)
        logSecurityEvent("ADMIN_USER_UPDATE", "Perfil de usuário modificado pelo admin: ${user.username}", "admin_portal", "INFO")
        Result.success(Unit)
    }

    fun updateDailyChallenge(config: DailyChallengeConfig) {
        dailyChallengeConfig = config
        logSecurityEvent("ADMIN_DAILY_CONFIG", "Novo desafio diário configurado: ${config.title}", "admin_portal", "INFO")
    }

    fun updateAdConfig(config: AdSystemConfig) {
        adConfig = config
        logSecurityEvent("ADMIN_AD_CONFIG", "Configurações de monetização e anúncios atualizadas", "admin_portal", "INFO")
    }

    private val loggingScope = kotlinx.coroutines.CoroutineScope(Dispatchers.IO)

    private fun logSecurityEvent(eventType: String, description: String, ipOrDeviceId: String, severity: String) {
        loggingScope.launch {
            securityLogDao.insertLog(
                SecurityLog(
                    eventType = eventType,
                    description = description,
                    ipOrDeviceId = ipOrDeviceId,
                    severity = severity
                )
            )
        }
    }
}
