package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.QuizViewModel
import com.example.ui.viewmodel.ScreenDestination

data class LeaderboardPlayer(
    val rank: Int,
    val name: String,
    val score: Int,
    val level: Int,
    val isVip: Boolean = false,
    val isCurrentUser: Boolean = false
)

@Composable
fun LeaderboardScreen(
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.userProfile.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Global", "Semanal", "Liga Ouro")

    val mockLeaderboard = remember(user) {
        listOf(
            LeaderboardPlayer(1, "Professor Silva", 28540, 42, isVip = true),
            LeaderboardPlayer(2, "Mente Brilhante", 24320, 38, isVip = true),
            LeaderboardPlayer(3, "Sabedoria Pura", 21890, 35, isVip = false),
            LeaderboardPlayer(4, user?.username ?: "Mestre Quiz", user?.totalScore ?: 4800, user?.level ?: 1, isVip = user?.isVip ?: false, isCurrentUser = true),
            LeaderboardPlayer(5, "Quiz Master BR", 18200, 29, isVip = true),
            LeaderboardPlayer(6, "Capitão Curioso", 15400, 24, isVip = false),
            LeaderboardPlayer(7, "Genius 2026", 13150, 21, isVip = false),
            LeaderboardPlayer(8, "Explorador da Ciência", 11200, 19, isVip = true),
            LeaderboardPlayer(9, "Historiador Nato", 9800, 16, isVip = false),
            LeaderboardPlayer(10, "Desafiante", 7500, 12, isVip = false)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(QuizBgDark)
    ) {
        // TopBar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(ScreenDestination.Home) },
                modifier = Modifier.testTag("leaderboard_back_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Hall da Fama & Rankings",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                color = TextPrimary
            )
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = QuizSurface,
            contentColor = QuizPrimaryLight,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = QuizPrimary
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Pódio Top 3
            item {
                PodiumSection(top3 = mockLeaderboard.take(3))
            }

            item {
                Text(
                    text = "Classificação Geral",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            itemsIndexed(mockLeaderboard) { index, player ->
                LeaderboardPlayerRow(player = player)
            }
        }

        BottomNavigationCustom(
            currentScreen = ScreenDestination.Leaderboard,
            onNavigate = { dest -> viewModel.navigateTo(dest) }
        )
    }
}

@Composable
fun PodiumSection(top3: List<LeaderboardPlayer>) {
    if (top3.size < 3) return
    val first = top3[0]
    val second = top3[1]
    val third = top3[2]

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        // 2º Lugar
        PodiumCol(player = second, rank = 2, height = 90.dp, color = Color(0xFF94A3B8))

        // 1º Lugar (Maior destaque)
        PodiumCol(player = first, rank = 1, height = 115.dp, color = QuizGold)

        // 3º Lugar
        PodiumCol(player = third, rank = 3, height = 75.dp, color = Color(0xFFB45309))
    }
}

@Composable
fun PodiumCol(player: LeaderboardPlayer, rank: Int, height: androidx.compose.ui.unit.Dp, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(color, color.copy(alpha = 0.6f)))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = player.name,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = TextPrimary,
            maxLines = 1
        )

        Text(
            text = "${player.score} pts",
            fontSize = 11.sp,
            color = QuizGoldLight,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(QuizSurfaceVariant)
                .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${rank}º",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = color
            )
        }
    }
}

@Composable
fun LeaderboardPlayerRow(player: LeaderboardPlayer) {
    val isCurrentUser = player.isCurrentUser
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isCurrentUser) QuizPrimaryDark.copy(alpha = 0.3f) else QuizSurface)
            .border(
                1.dp,
                if (isCurrentUser) QuizPrimaryLight else CardBorderGlow,
                RoundedCornerShape(14.dp)
            )
            .padding(12.dp)
            .testTag("leaderboard_row_${player.rank}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Posição
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(QuizSurfaceLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${player.rank}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = when (player.rank) {
                            1 -> QuizGold
                            2 -> Color.LightGray
                            3 -> Color(0xFFCD7F32)
                            else -> TextPrimary
                        }
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isCurrentUser) "${player.name} (Você)" else player.name,
                            fontWeight = if (isCurrentUser) FontWeight.Black else FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isCurrentUser) QuizGoldLight else TextPrimary
                        )

                        if (player.isVip) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(QuizGoldDark)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("VIP", color = Color.White, fontWeight = FontWeight.Black, fontSize = 8.sp)
                            }
                        }
                    }

                    Text(
                        text = "Nível ${player.level}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            Text(
                text = "${player.score} pts",
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                color = TextPrimary
            )
        }
    }
}
