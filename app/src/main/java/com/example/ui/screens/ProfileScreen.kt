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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.model.MatchResult
import com.example.ui.theme.*
import com.example.ui.viewmodel.QuizViewModel
import com.example.ui.viewmodel.ScreenDestination
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.userProfile.collectAsState()
    val matchHistory by viewModel.matchHistory.collectAsState()

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
                modifier = Modifier.testTag("profile_back_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Perfil do Jogador",
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
            // Card Principal do Avatar e Nível
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    QuizSurfaceVariant,
                                    QuizSurface
                                )
                            )
                        )
                        .border(1.5.dp, if (user?.isVip == true) GoldBorderGlow else CardBorderGlow, RoundedCornerShape(22.dp))
                        .padding(20.dp)
                        .testTag("profile_info_card")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .size(74.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        if (user?.isVip == true) listOf(QuizGold, QuizOrange)
                                        else listOf(QuizPrimary, Color(0xFF6366F1))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(44.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user?.username ?: "Mestre Quiz",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp
                                ),
                                color = TextPrimary
                            )

                            if (user?.isVip == true) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Brush.horizontalGradient(listOf(QuizGold, QuizGoldDark)))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("VIP", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 9.sp)
                                }
                            }
                        }

                        Text(
                            text = user?.email ?: "jogador@quizmania.app",
                            fontSize = 12.sp,
                            color = TextMuted
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Barra de Progresso de XP / Nível
                        val currentXp = user?.currentXp ?: 0
                        val nextLevelXp = user?.xpToNextLevel ?: 500
                        val progress = (currentXp.toFloat() / nextLevelXp.toFloat()).coerceIn(0f, 1f)

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Nível ${user?.level ?: 1}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = QuizPrimaryLight
                                )
                                Text(
                                    text = "$currentXp / $nextLevelXp XP",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = progress,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = QuizPrimary,
                                trackColor = QuizSurfaceLight
                            )
                        }
                    }
                }
            }

            // Grade de Estatísticas Gerais
            item {
                Text(
                    text = "Estatísticas de Carreira",
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Partidas",
                        value = "${user?.totalMatchesPlayed ?: 0}",
                        icon = Icons.Default.EmojiEvents,
                        color = QuizCyan,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Vitórias",
                        value = "${user?.totalWins ?: 0}",
                        icon = Icons.Default.MilitaryTech,
                        color = QuizEmerald,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Melhor Streak",
                        value = "${user?.bestStreak ?: 0}x",
                        icon = Icons.Default.LocalFireDepartment,
                        color = QuizOrange,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Histórico de Partidas Recentes
            item {
                Text(
                    text = "Histórico de Partidas Recentes",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (matchHistory.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(QuizSurface)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.History, contentDescription = null, tint = TextMuted, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Nenhuma partida registrada ainda.", fontSize = 13.sp, color = TextMuted)
                            Text("Jogue agora para começar a pontuar!", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }
            } else {
                items(matchHistory) { match ->
                    MatchHistoryRow(match = match)
                }
            }
        }
    }
}

@Composable
fun MatchHistoryRow(match: MatchResult) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val dateString = dateFormat.format(Date(match.playedAt))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(QuizSurface)
            .border(1.dp, CardBorderGlow, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "${match.modeName} - ${match.categoryId.uppercase()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Text(
                    text = dateString,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (match.accuracy >= 60) QuizEmerald.copy(alpha = 0.2f) else QuizRuby.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${match.accuracy}%",
                        color = if (match.accuracy >= 60) QuizEmeraldLight else QuizRuby,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = "${match.score} pts",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = QuizGoldLight
                )
            }
        }
    }
}
