package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.AdSystemConfig
import com.example.data.model.DailyChallengeConfig
import com.example.data.model.Question
import com.example.data.model.SecurityLog
import com.example.data.model.SystemAnalytics
import com.example.data.model.UserPlayer
import com.example.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminViewModel(
    private val repository: QuizRepository
) : ViewModel() {

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _analytics = MutableStateFlow<SystemAnalytics?>(null)
    val analytics: StateFlow<SystemAnalytics?> = _analytics.asStateFlow()

    val securityLogs: StateFlow<List<SecurityLog>> = repository.getSecurityLogsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val questionsList: StateFlow<List<Question>> = repository.getAllQuestionsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedTab = MutableStateFlow(0) // 0: Visão Geral, 1: Perguntas, 2: Jogadores, 3: Segurança, 4: Configurações
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _dailyConfig = MutableStateFlow(repository.getDailyChallengeConfig())
    val dailyConfig: StateFlow<DailyChallengeConfig> = _dailyConfig.asStateFlow()

    private val _adConfig = MutableStateFlow(repository.getAdConfig())
    val adConfig: StateFlow<AdSystemConfig> = _adConfig.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    fun loginWithPin(pin: String): Boolean {
        val success = repository.verifyAdminAuth(pin)
        _isAuthenticated.value = success
        if (success) {
            loadAnalytics()
        }
        return success
    }

    fun logout() {
        _isAuthenticated.value = false
    }

    fun selectTab(index: Int) {
        _selectedTab.value = index
        if (index == 0) {
            loadAnalytics()
        }
    }

    fun loadAnalytics() {
        viewModelScope.launch {
            _analytics.value = repository.getSystemAnalytics()
        }
    }

    fun addQuestion(
        categoryId: String,
        questionText: String,
        opt0: String,
        opt1: String,
        opt2: String,
        opt3: String,
        correctIndex: Int,
        explanation: String,
        difficulty: Int
    ) {
        viewModelScope.launch {
            val newQ = Question(
                id = "admin_q_${System.currentTimeMillis()}",
                categoryId = categoryId,
                questionText = questionText,
                optionA = opt0,
                optionB = opt1,
                optionC = opt2,
                optionD = opt3,
                correctOptionIndex = correctIndex,
                explanation = explanation,
                difficulty = difficulty,
                isCustomAdmin = true
            )
            repository.addQuestion(newQ)
            _toastMessage.emit("Pergunta adicionada com sucesso ao banco seguro!")
            loadAnalytics()
        }
    }

    fun deleteQuestion(questionId: String) {
        viewModelScope.launch {
            repository.deleteQuestion(questionId)
            _toastMessage.emit("Pergunta removida!")
            loadAnalytics()
        }
    }

    fun updateDailyChallenge(title: String, desc: String, categoryId: String, multiplier: Float, coins: Int) {
        val config = _dailyConfig.value.copy(
            title = title,
            description = desc,
            categoryId = categoryId,
            xpMultiplier = multiplier,
            coinReward = coins
        )
        _dailyConfig.value = config
        repository.updateDailyChallenge(config)
        viewModelScope.launch {
            _toastMessage.emit("Desafio Diário atualizado no servidor!")
        }
    }

    fun updateAdSettings(bannerEnabled: Boolean, interstitialEveryMatches: Int, rewardedCoins: Int) {
        val config = _adConfig.value.copy(
            bannerAdsEnabled = bannerEnabled,
            interstitialIntervalMatches = interstitialEveryMatches,
            rewardedVideoCoins = rewardedCoins
        )
        _adConfig.value = config
        repository.updateAdConfig(config)
        viewModelScope.launch {
            _toastMessage.emit("Configurações de anúncios salvas!")
        }
    }

    class Factory(
        private val repository: QuizRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AdminViewModel(repository) as T
        }
    }
}
