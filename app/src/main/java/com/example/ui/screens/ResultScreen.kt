package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameSessionSummary
import com.example.data.model.QuestionReviewItem
import com.example.ui.components.BannerAdPlaceholder
import com.example.ui.theme.*
import com.example.ui.viewmodel.QuizViewModel
import com.example.ui.viewmodel.ScreenDestination

@Composable
fun ResultScreen(
    summary: GameSessionSummary,
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.userProfile.collectAsState()
    val isWin = summary.accuracyPercentage >= 60

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(QuizBgDark),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Card Principal de Vitória / Desempenho
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            if (isWin) listOf(QuizSurfaceVariant, QuizEmeraldDark.copy(alpha = 0.3f))
                            else listOf(QuizSurfaceVariant, QuizRubyDark.copy(alpha = 0.3f))
                        )
                    )
                    .border(
                        1.5.dp,
                        if (isWin) QuizEmerald else QuizRuby,
                        RoundedCornerShape(24.dp)
                    )
                    .padding(20.dp)
                    .testTag("result_main_card")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Ícone Troféu / Medalha
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    if (isWin) listOf(QuizGold, QuizGoldLight)
                                    else listOf(QuizRuby, Color(0xFFF87171))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isWin) Icons.Default.EmojiEvents else Icons.Default.Close,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isWin) "EXCELENTE PARTIDA!" else "FIM DE JOGO!",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp
                        ),
                        color = if (isWin) QuizGoldLight else TextPrimary
                    )

                    Text(
                        text = if (isWin) "Você dominou as perguntas desta rodada!" else "Não desista! Tente novamente para melhorar sua pontuação.",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pontuação Destaque
                    Text(
                        text = "${summary.finalScore}",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 42.sp
                        ),
                        color = TextPrimary
                    )
                    Text(
                        text = "PONTOS CONQUISTADOS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuizGold
                    )
                }
            }
        }

        // Grade de Estatísticas da Partida
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Precisão",
                    value = "${summary.accuracyPercentage}%",
                    icon = Icons.Default.CheckCircle,
                    color = QuizEmerald,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Acertos",
                    value = "${summary.correctCount}/${summary.totalQuestions}",
                    icon = Icons.Default.Stars,
                    color = QuizPrimary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Combo Max",
                    value = "${summary.maxStreak}x",
                    icon = Icons.Default.LocalFireDepartment,
                    color = QuizGold,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Recompensas Recebidas (XP e Moedas)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(QuizSurface)
                    .border(1.dp, CardBorderGlow, RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(QuizPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+XP", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "+${summary.xpEarned} XP", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                            Text(text = "Experiência", fontSize = 11.sp, color = TextMuted)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(QuizGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "+${summary.coinsEarned} Moedas", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = QuizGoldLight)
                            Text(text = "Recompensa", fontSize = 11.sp, color = TextMuted)
                        }
                    }
                }
            }
        }

        // Botões de Ação
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { viewModel.startQuizMatch(summary.categoryId, summary.mode) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("result_replay_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = QuizPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Jogar Novamente", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                OutlinedButton(
                    onClick = { viewModel.navigateTo(ScreenDestination.Home) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("result_home_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Voltar ao Início", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                }
            }
        }

        // Banner Patrocinador se não for VIP
        item {
            BannerAdPlaceholder(
                isVip = user?.isVip == true,
                onUpgradeVipClick = { viewModel.navigateTo(ScreenDestination.Store) }
            )
        }

        // Seção de Revisão das Respostas
        item {
            Text(
                text = "Gabarito e Explicações Detalhadas",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = TextPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(summary.answeredReviewList) { item ->
            ReviewItemCard(item = item)
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(QuizSurfaceVariant)
            .border(1.dp, CardBorderGlow, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = TextPrimary
            )
            Text(
                text = title,
                fontSize = 11.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
fun ReviewItemCard(item: QuestionReviewItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(QuizSurface)
            .border(
                1.dp,
                if (item.isCorrect) QuizEmerald.copy(alpha = 0.4f) else QuizRuby.copy(alpha = 0.4f),
                RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = item.questionText,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ),
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (item.isCorrect) QuizEmerald else QuizRuby)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = if (item.isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (!item.isCorrect) {
                Text(
                    text = "Sua resposta: ${item.userSelectedOption}",
                    fontSize = 12.sp,
                    color = QuizRubyLight
                )
            }

            Text(
                text = "Resposta correta: ${item.correctOption}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = QuizEmeraldLight
            )

            if (item.explanation.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Por que: ${item.explanation}",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }
    }
}
