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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameMode
import com.example.ui.theme.*
import com.example.ui.viewmodel.QuizViewModel
import com.example.ui.viewmodel.ScreenDestination

@Composable
fun DailyChallengeScreen(
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.userProfile.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(QuizBgDark)
    ) {
        // Cabeçalho
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(ScreenDestination.Home) },
                modifier = Modifier.testTag("daily_back_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Desafio Diário dos Mestres",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                color = TextPrimary
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card Principal do Desafio
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF78350F),
                                    Color(0xFF451A03),
                                    QuizSurfaceVariant
                                )
                            )
                        )
                        .border(1.5.dp, GoldBorderGlow, RoundedCornerShape(24.dp))
                        .padding(20.dp)
                        .testTag("daily_challenge_hero_card")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(QuizGold, QuizOrange))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "DESAFIO DO DIA",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = QuizGold,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = "A Trilha do Conhecimento",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp
                            ),
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Responda 10 perguntas selecionadas pelo sistema para conquistar recompensas em dobro!",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            // Bônus e Recompensas
            item {
                Text(
                    text = "Recompensas do Desafio",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = TextPrimary
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 2x XP
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(QuizSurfaceVariant)
                            .border(1.dp, CardBorderGlow, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = QuizCyan, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "2.0x XP", fontWeight = FontWeight.Black, fontSize = 16.sp, color = TextPrimary)
                            Text(text = "Multiplicador", fontSize = 11.sp, color = TextMuted)
                        }
                    }

                    // 150 Moedas
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(QuizSurfaceVariant)
                            .border(1.dp, CardBorderGlow, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = QuizGold, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "+150", fontWeight = FontWeight.Black, fontSize = 16.sp, color = QuizGoldLight)
                            Text(text = "Moedas Mania", fontSize = 11.sp, color = TextMuted)
                        }
                    }
                }
            }

            // Regras do Desafio
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(QuizSurface)
                        .border(1.dp, CardBorderGlow, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Regras do Desafio Diário",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
                            color = TextPrimary
                        )
                        Text(text = "• 10 perguntas de múltipla escolha com tempo de 15 segundos cada.", fontSize = 12.sp, color = TextSecondary)
                        Text(text = "• Não consome suas vidas regulares.", fontSize = 12.sp, color = TextSecondary)
                        Text(text = "• Novo desafio liberado todos os dias à meia-noite.", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }

            // Botão Iniciar
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.startQuizMatch("mixed", GameMode.DAILY_CHALLENGE) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("start_daily_challenge_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = QuizGold,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Começar Desafio Agora", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }

        BottomNavigationCustom(
            currentScreen = ScreenDestination.DailyChallenge,
            onNavigate = { dest -> viewModel.navigateTo(dest) }
        )
    }
}
