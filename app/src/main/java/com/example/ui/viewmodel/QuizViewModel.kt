package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.AnswerSubmission
import com.example.data.model.AnswerValidationResult
import com.example.data.model.GameMode
import com.example.data.model.GameSession
import com.example.data.model.GameSessionSummary
import com.example.data.model.MatchResult
import com.example.data.model.QuizCategory
import com.example.data.model.UserPlayer
import com.example.data.repository.BillingRepository
import com.example.data.repository.QuizRepository
import com.example.data.repository.StoreProduct
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ScreenDestination {
    object Home : ScreenDestination()
    data class Game(val categoryId: String, val mode: GameMode) : ScreenDestination()
    data class Result(val summary: GameSessionSummary) : ScreenDestination()
    object DailyChallenge : ScreenDestination()
    object Leaderboard : ScreenDestination()
    object Store : ScreenDestination()
    object Profile : ScreenDestination()
    object AdminLogin : ScreenDestination()
    object AdminDashboard : ScreenDestination()
}

class QuizViewModel(
    private val repository: QuizRepository,
    private val billingRepository: BillingRepository
) : ViewModel() {

    // Destino de tela atual
    private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.Home)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    // Perfil do Jogador
    val userProfile: StateFlow<UserPlayer?> = repository.getUserFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Histórico de partidas
    val matchHistory: StateFlow<List<MatchResult>> = repository.getMatchHistoryFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estado da partida em andamento
    private val _activeSession = MutableStateFlow<GameSession?>(null)
    val activeSession: StateFlow<GameSession?> = _activeSession.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _selectedOptionIndex = MutableStateFlow<Int?>(null)
    val selectedOptionIndex: StateFlow<Int?> = _selectedOptionIndex.asStateFlow()

    private val _validationResult = MutableStateFlow<AnswerValidationResult?>(null)
    val validationResult: StateFlow<AnswerValidationResult?> = _validationResult.asStateFlow()

    private val _remainingTimeSeconds = MutableStateFlow(15)
    val remainingTimeSeconds: StateFlow<Int> = _remainingTimeSeconds.asStateFlow()

    private val _hiddenOptions = MutableStateFlow<Set<Int>>(emptySet())
    val hiddenOptions: StateFlow<Set<Int>> = _hiddenOptions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    private var timerJob: Job? = null
    private var questionStartTimeMs: Long = 0
    private var matchStartTimeMs: Long = 0

    val storeProducts: List<StoreProduct> = billingRepository.availableProducts

    fun navigateTo(destination: ScreenDestination) {
        _currentScreen.value = destination
    }

    /**
     * Inicia uma nova partida segura
     */
    fun startQuizMatch(categoryId: String, mode: GameMode) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.startMatch(categoryId, mode)
            _isLoading.value = false

            result.onSuccess { session ->
                _activeSession.value = session
                _currentQuestionIndex.value = 0
                _selectedOptionIndex.value = null
                _validationResult.value = null
                _hiddenOptions.value = emptySet()
                matchStartTimeMs = System.currentTimeMillis()
                _currentScreen.value = ScreenDestination.Game(categoryId, mode)
                startQuestionTimer()
            }.onFailure { error ->
                _toastMessage.emit(error.message ?: "Falha ao iniciar partida")
            }
        }
    }

    private fun startQuestionTimer() {
        timerJob?.cancel()
        _remainingTimeSeconds.value = 15
        questionStartTimeMs = System.currentTimeMillis()

        timerJob = viewModelScope.launch {
            while (_remainingTimeSeconds.value > 0 && _validationResult.value == null) {
                delay(1000)
                _remainingTimeSeconds.value -= 1
            }

            // Tempo esgotado -> submeter resposta nula (-1)
            if (_remainingTimeSeconds.value <= 0 && _validationResult.value == null) {
                submitUserAnswer(-1)
            }
        }
    }

    /**
     * Envia a resposta selecionada para validação no Backend Seguro
     */
    fun submitUserAnswer(optionIndex: Int) {
        val session = _activeSession.value ?: return
        if (_validationResult.value != null) return // Já respondeu esta pergunta

        timerJob?.cancel()
        _selectedOptionIndex.value = optionIndex

        val timeSpent = System.currentTimeMillis() - questionStartTimeMs
        val currentQuestion = session.questions.getOrNull(_currentQuestionIndex.value) ?: return

        val submission = AnswerSubmission(
            sessionToken = session.sessionToken,
            questionId = currentQuestion.id,
            selectedOptionIndex = optionIndex,
            responseTimeMs = timeSpent
        )

        viewModelScope.launch {
            val result = repository.submitAnswer(submission)
            result.onSuccess { validation ->
                _validationResult.value = validation
            }.onFailure { error ->
                _toastMessage.emit(error.message ?: "Erro ao validar resposta")
            }
        }
    }

    /**
     * Avança para a próxima pergunta ou encerra a partida
     */
    fun nextQuestionOrFinish() {
        val session = _activeSession.value ?: return
        val validation = _validationResult.value

        if (validation?.isGameOver == true || _currentQuestionIndex.value + 1 >= session.totalQuestions) {
            // Finalizar partida
            viewModelScope.launch {
                _isLoading.value = true
                val totalSeconds = ((System.currentTimeMillis() - matchStartTimeMs) / 1000).toInt()
                val summaryResult = repository.finishMatch(session.sessionToken, totalSeconds)
                _isLoading.value = false

                summaryResult.onSuccess { summary ->
                    _currentScreen.value = ScreenDestination.Result(summary)
                    _activeSession.value = null
                }.onFailure { error ->
                    _toastMessage.emit(error.message ?: "Falha ao consolidar partida")
                    _currentScreen.value = ScreenDestination.Home
                }
            }
        } else {
            // Próxima pergunta
            _currentQuestionIndex.value += 1
            _selectedOptionIndex.value = null
            _validationResult.value = null
            _hiddenOptions.value = emptySet()
            startQuestionTimer()
        }
    }

    /**
     * Utiliza Poder 50/50
     */
    fun useFiftyFifty() {
        val session = _activeSession.value ?: return
        if (_hiddenOptions.value.isNotEmpty() || _validationResult.value != null) return

        viewModelScope.launch {
            val consumed = repository.consumePowerUp("FIFTY_FIFTY")
            if (consumed) {
                // Elimina duas alternativas aleatórias (deixando 2 opções na tela)
                val currentQ = session.questions.getOrNull(_currentQuestionIndex.value) ?: return@launch
                val randomIndices = (0..3).shuffled().take(2).toSet()
                _hiddenOptions.value = randomIndices
                _toastMessage.emit("50/50 ativado! Duas alternativas eliminadas.")
            } else {
                _toastMessage.emit("Sem poder 50/50. Adquira na loja!")
            }
        }
    }

    /**
     * Utiliza Poder Pular Pergunta
     */
    fun useSkipQuestion() {
        if (_validationResult.value != null) return
        viewModelScope.launch {
            val consumed = repository.consumePowerUp("SKIP")
            if (consumed) {
                timerJob?.cancel()
                _toastMessage.emit("Pergunta pulada com sucesso!")
                nextQuestionOrFinish()
            } else {
                _toastMessage.emit("Sem poder de Pular. Adquira na loja!")
            }
        }
    }

    /**
     * Utiliza Poder Congelar / Tempo Extra
     */
    fun useFreezeTime() {
        if (_validationResult.value != null) return
        viewModelScope.launch {
            val consumed = repository.consumePowerUp("FREEZE")
            if (consumed) {
                _remainingTimeSeconds.value = minOf(30, _remainingTimeSeconds.value + 15)
                _toastMessage.emit("+15 segundos adicionados!")
            } else {
                _toastMessage.emit("Sem poder de Tempo Extra. Adquira na loja!")
            }
        }
    }

    /**
     * Comprar na Loja com Moedas do Jogo
     */
    fun buyWithCoins(itemKey: String, cost: Int) {
        viewModelScope.launch {
            val result = billingRepository.buyInGameItemWithCoins(itemKey, cost)
            result.onSuccess { msg ->
                _toastMessage.emit(msg)
            }.onFailure { error ->
                _toastMessage.emit(error.message ?: "Falha na compra")
            }
        }
    }

    /**
     * Comprar Pacote VIP / Moedas via Google Play Billing
     */
    fun purchaseProduct(productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = billingRepository.purchaseProduct(productId)
            _isLoading.value = false
            result.onSuccess { msg ->
                _toastMessage.emit(msg)
            }.onFailure { error ->
                _toastMessage.emit(error.message ?: "Erro no processamento da compra")
            }
        }
    }

    /**
     * Ganhar Vidas ou Moedas assistindo anúncio premiado
     */
    fun watchRewardedAd(rewardType: String) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1200) // Simulação de exibição do vídeo premiado
            val result = billingRepository.rewardFromAd(rewardType)
            _isLoading.value = false
            result.onSuccess { msg ->
                _toastMessage.emit(msg)
            }
        }
    }

    class Factory(
        private val repository: QuizRepository,
        private val billingRepository: BillingRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return QuizViewModel(repository, billingRepository) as T
        }
    }
}
