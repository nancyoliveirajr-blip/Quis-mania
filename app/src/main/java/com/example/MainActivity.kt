package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.data.backend.QuizBackendEngine
import com.example.data.local.QuizDatabase
import com.example.data.repository.BillingRepository
import com.example.data.repository.QuizRepository
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.DailyChallengeScreen
import com.example.ui.screens.GameScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ResultScreen
import com.example.ui.screens.StoreScreen
import com.example.ui.theme.QuizManiaTheme
import com.example.ui.theme.QuizPrimary
import com.example.ui.viewmodel.AdminViewModel
import com.example.ui.viewmodel.QuizViewModel
import com.example.ui.viewmodel.ScreenDestination
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val quizDatabase by lazy { QuizDatabase.getDatabase(this) }
    private val billingRepository by lazy { BillingRepository(quizDatabase.userDao()) }
    private val backendEngine by lazy {
        QuizBackendEngine(
            quizDao = quizDatabase.quizDao(),
            userDao = quizDatabase.userDao(),
            securityLogDao = quizDatabase.securityLogDao()
        )
    }
    private val quizRepository by lazy {
        QuizRepository(
            backendEngine = backendEngine,
            userDao = quizDatabase.userDao(),
            quizDao = quizDatabase.quizDao(),
            securityLogDao = quizDatabase.securityLogDao(),
            billingRepository = billingRepository
        )
    }

    private val quizViewModel: QuizViewModel by viewModels {
        QuizViewModel.Factory(quizRepository, billingRepository)
    }

    private val adminViewModel: AdminViewModel by viewModels {
        AdminViewModel.Factory(quizRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            QuizManiaTheme {
                val currentScreen by quizViewModel.currentScreen.collectAsState()
                val isLoading by quizViewModel.isLoading.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                // Toast/Snackbar notifications observer
                LaunchedEffect(Unit) {
                    launch {
                        quizViewModel.toastMessage.collectLatest { msg ->
                            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                    launch {
                        adminViewModel.toastMessage.collectLatest { msg ->
                            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // Tratar botão Voltar do Android de acordo com a tela
                BackHandler(enabled = currentScreen !is ScreenDestination.Home) {
                    when (currentScreen) {
                        is ScreenDestination.Game -> {
                            quizViewModel.navigateTo(ScreenDestination.Home)
                        }
                        is ScreenDestination.Result -> {
                            quizViewModel.navigateTo(ScreenDestination.Home)
                        }
                        else -> {
                            quizViewModel.navigateTo(ScreenDestination.Home)
                        }
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "screen_transition"
                        ) { screen ->
                            when (screen) {
                                is ScreenDestination.Home -> HomeScreen(viewModel = quizViewModel)
                                is ScreenDestination.Game -> GameScreen(viewModel = quizViewModel)
                                is ScreenDestination.Result -> ResultScreen(summary = screen.summary, viewModel = quizViewModel)
                                is ScreenDestination.DailyChallenge -> DailyChallengeScreen(viewModel = quizViewModel)
                                is ScreenDestination.Store -> StoreScreen(viewModel = quizViewModel)
                                is ScreenDestination.Leaderboard -> LeaderboardScreen(viewModel = quizViewModel)
                                is ScreenDestination.Profile -> ProfileScreen(viewModel = quizViewModel)
                                is ScreenDestination.AdminLogin, is ScreenDestination.AdminDashboard -> {
                                    AdminScreen(adminViewModel = adminViewModel, quizViewModel = quizViewModel)
                                }
                            }
                        }

                        if (isLoading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = QuizPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}
