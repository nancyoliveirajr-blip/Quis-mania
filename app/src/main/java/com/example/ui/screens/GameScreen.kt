package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuizCategory
import com.example.ui.components.OptionButton
import com.example.ui.components.OptionButtonState
import com.example.ui.components.QuizTimerBar
import com.example.ui.components.StreakIndicator
import com.example.ui.theme.CardBorderGlow
import com.example.ui.theme.GoldBorderGlow
import com.example.ui.theme.QuizBgDark
import com.example.ui.theme.QuizCyan
import com.example.ui.theme.QuizEmerald
import com.example.ui.theme.QuizEmeraldDark
import com.example.ui.theme.QuizGold
import com.example.ui.theme.QuizGoldLight
import com.example.ui.theme.QuizPrimary
import com.example.ui.theme.QuizPrimaryDark
import com.example.ui.theme.QuizRuby
import com.example.ui.theme.QuizRubyDark
import com.example.ui.theme.QuizSurface
import com.example.ui.theme.QuizSurfaceLight
import com.example.ui.theme.QuizSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.QuizViewModel
import com.example.ui.viewmodel.ScreenDestination

@Composable
fun GameScreen(
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    val session by viewModel.activeSession.collectAsState()
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val selectedOption by viewModel.selectedOptionIndex.collectAsState()
    val validation by viewModel.validationResult.collectAsState()
    val remainingTime by viewModel.remainingTimeSeconds.collectAsState()
    val hiddenOptions by viewModel.hiddenOptions.collectAsState()
    val user by viewModel.userProfile.collectAsState()

    val currentQuestion = session?.questions?.getOrNull(currentIndex)
    val totalQuestions = session?.totalQuestions ?: 10
    val category = QuizCategory.fromId(session?.categoryId ?: "geral")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(QuizBgDark)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Cabeçalho da Partida (Progresso, Categoria, Vidas e Pontos)
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botão de desistir / Sair
                IconButton(
                    onClick = { viewModel.navigateTo(ScreenDestination.Home) },
                    modifier = Modifier.size(36.dp).testTag("game_exit_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Sair da Partida",
                        tint = TextMuted
                    )
                }

                // Categoria Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(category.primaryColor).copy(alpha = 0.25f))
                        .border(1.dp, Color(category.primaryColor).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = category.displayName.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(category.primaryColor)
                    )
                }

                // Vidas Restantes
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(QuizSurfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = QuizRuby,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    if (user?.isVip == true) {
                        Icon(
                            imageVector = Icons.Default.AllInclusive,
                            contentDescription = null,
                            tint = QuizRuby,
                            modifier = Modifier.size(14.dp)
                        )
                    } else {
                        Text(
                            text = "${validation?.remainingLives ?: session?.livesRemaining ?: 5}",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Barra de Progresso e Pergunta Atual
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pergunta ${currentIndex + 1} de $totalQuestions",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                    color = TextSecondary
                )

                validation?.currentStreak?.let { streak ->
                    StreakIndicator(streak = streak)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Timer Bar
            QuizTimerBar(
                remainingSeconds = remainingTime,
                maxSeconds = 15
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Card com Enunciado da Pergunta
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(QuizSurface)
                .border(1.dp, CardBorderGlow, RoundedCornerShape(20.dp))
                .padding(20.dp)
                .testTag("question_text_container")
        ) {
            Text(
                text = currentQuestion?.questionText ?: "Carregando pergunta...",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 24.sp
                ),
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4 Alternativas de Resposta
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            currentQuestion?.options?.forEachIndexed { index, optionText ->
                if (!hiddenOptions.contains(index)) {
                    val buttonState = when {
                        validation != null -> {
                            when {
                                index == validation?.correctOptionIndex -> OptionButtonState.CORRECT
                                index == selectedOption && !validation!!.isCorrect -> OptionButtonState.WRONG
                                else -> OptionButtonState.DISABLED
                            }
                        }
                        selectedOption == index -> OptionButtonState.SELECTED
                        else -> OptionButtonState.DEFAULT
                    }

                    OptionButton(
                        index = index,
                        text = optionText,
                        state = buttonState,
                        enabled = validation == null,
                        onClick = { viewModel.submitUserAnswer(index) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Barra de Poderes (Ajudas)
        if (validation == null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(QuizSurfaceVariant)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 50/50
                PowerUpButton(
                    title = "50/50",
                    count = user?.powerUpFiftyFifty ?: 0,
                    icon = Icons.Default.Lightbulb,
                    onClick = { viewModel.useFiftyFifty() },
                    testTag = "powerup_fifty_fifty"
                )

                // Pular Pergunta
                PowerUpButton(
                    title = "Pular",
                    count = user?.powerUpSkip ?: 0,
                    icon = Icons.Default.SkipNext,
                    onClick = { viewModel.useSkipQuestion() },
                    testTag = "powerup_skip"
                )

                // Tempo Extra
                PowerUpButton(
                    title = "+15s",
                    count = user?.powerUpTimeFreeze ?: 0,
                    icon = Icons.Default.HourglassTop,
                    onClick = { viewModel.useFreezeTime() },
                    testTag = "powerup_freeze"
                )
            }
        }

        // Explicação e Botão de Avanço após responder
        AnimatedVisibility(
            visible = validation != null,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically()
        ) {
            val valid = validation ?: return@AnimatedVisibility
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                // Banner de Resultado do Backend
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (valid.isCorrect) QuizEmeraldDark.copy(alpha = 0.25f) else QuizRubyDark.copy(alpha = 0.25f))
                        .border(
                            1.dp,
                            if (valid.isCorrect) QuizEmerald else QuizRuby,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (valid.isCorrect) "RESPOSTA CORRETA!" else "RESPOSTA INCORRETA",
                                color = if (valid.isCorrect) QuizEmerald else QuizRuby,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp
                            )

                            if (valid.isCorrect) {
                                Text(
                                    text = "+${valid.totalScoreEarned} pts",
                                    color = QuizGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        if (valid.explanation.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = valid.explanation,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                color = TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botão de Avançar
                Button(
                    onClick = { viewModel.nextQuestionOrFinish() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("game_next_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (valid.isGameOver) QuizRuby else QuizPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (valid.isGameOver) "Ver Resultado da Partida"
                            else if (currentIndex + 1 >= totalQuestions) "Finalizar Partida"
                            else "Próxima Pergunta",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PowerUpButton(
    title: String,
    count: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(QuizSurfaceLight)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = QuizGold,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(QuizPrimary)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "$count",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }
    }
}
