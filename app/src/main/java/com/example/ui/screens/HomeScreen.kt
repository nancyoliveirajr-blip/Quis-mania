package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.GameMode
import com.example.data.model.QuizCategory
import com.example.ui.components.BannerAdPlaceholder
import com.example.ui.components.CategoryCard
import com.example.ui.components.QuizTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.QuizViewModel
import com.example.ui.viewmodel.ScreenDestination

@Composable
fun HomeScreen(
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.userProfile.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(QuizBgDark)
    ) {
        // TopBar com Moedas, Vidas e Perfil
        QuizTopBar(
            user = user,
            onStoreClick = { viewModel.navigateTo(ScreenDestination.Store) },
            onProfileClick = { viewModel.navigateTo(ScreenDestination.Profile) }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Hero Banner Proprietary
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .height(140.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(QuizPrimaryDark, Color(0xFF3B0764))
                            )
                        )
                        .border(1.dp, CardBorderGlow, RoundedCornerShape(20.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.quiz_hero_banner),
                        contentDescription = "Quiz Mania Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlay gradiente para legibilidade perfeita
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xEE090514),
                                        Color(0x99090514),
                                        Color(0x22090514)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = QuizGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "QUIZ MANIA OFICIAL",
                                color = QuizGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Desafie seu Conhecimento!",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 19.sp
                            ),
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "300+ perguntas em 12 categorias exclusivas.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            color = TextSecondary
                        )
                    }
                }
            }

            // Desafio Diário Especial Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF78350F),
                                    Color(0xFF451A03),
                                    QuizSurfaceVariant
                                )
                            )
                        )
                        .border(1.dp, GoldBorderGlow, RoundedCornerShape(18.dp))
                        .clickable { viewModel.navigateTo(ScreenDestination.DailyChallenge) }
                        .padding(14.dp)
                        .testTag("home_daily_challenge_card")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Brush.linearGradient(listOf(QuizGold, QuizOrange))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FlashOn,
                                    contentDescription = "Desafio Diário",
                                    tint = Color.Black,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "DESAFIO DIÁRIO",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        color = QuizGold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(QuizGoldDark)
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "2X XP",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }

                                Text(
                                    text = "Desafio dos Sábios",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    ),
                                    color = TextPrimary
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.startQuizMatch("ciencia", GameMode.DAILY_CHALLENGE) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = QuizGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("play_daily_challenge_button")
                        ) {
                            Text("Jogar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Seletor de Modos de Jogo
            item {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "Modos de Jogo",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp),
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            GameModePill(
                                title = "Partida Rápida",
                                subtitle = "Perguntas Mistas",
                                icon = Icons.Default.PlayArrow,
                                gradient = listOf(QuizPrimary, QuizPrimaryDark),
                                onClick = { viewModel.startQuizMatch("mixed", GameMode.CLASSIC) },
                                testTag = "mode_quick_match"
                            )
                        }
                        item {
                            GameModePill(
                                title = "Sobrevivência",
                                subtitle = "Até a 1ª derrota",
                                icon = Icons.Default.Shield,
                                gradient = listOf(QuizRuby, Color(0xFF991B1B)),
                                onClick = { viewModel.startQuizMatch("mixed", GameMode.SURVIVAL) },
                                testTag = "mode_survival"
                            )
                        }
                        item {
                            GameModePill(
                                title = "Modo Ranqueado",
                                subtitle = "Vale Pontos na Liga",
                                icon = Icons.Default.MilitaryTech,
                                gradient = listOf(QuizGold, QuizOrange),
                                onClick = { viewModel.startQuizMatch("mixed", GameMode.RANKED) },
                                testTag = "mode_ranked"
                            )
                        }
                    }
                }
            }

            // Anúncio Patrocinado (se não for VIP)
            item {
                BannerAdPlaceholder(
                    isVip = user?.isVip == true,
                    onUpgradeVipClick = { viewModel.navigateTo(ScreenDestination.Store) }
                )
            }

            // Lista de Categorias
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Categorias Temáticas (12)",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp),
                        color = TextPrimary
                    )

                    Text(
                        text = "Selecione para jogar",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                        color = TextMuted
                    )
                }
            }

            items(QuizCategory.values()) { category ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    CategoryCard(
                        category = category,
                        questionCount = 26,
                        onCategoryClick = { selectedCat ->
                            viewModel.startQuizMatch(selectedCat.id, GameMode.CLASSIC)
                        }
                    )
                }
            }

            // Acesso Rápido ao Painel Administrativo Seguro
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(QuizSurface)
                        .clickable { viewModel.navigateTo(ScreenDestination.AdminLogin) }
                        .padding(12.dp)
                        .testTag("home_admin_portal_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Painel Admin",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Painel de Administração e Gestão",
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Barra de Navegação Inferior
        BottomNavigationCustom(
            currentScreen = ScreenDestination.Home,
            onNavigate = { dest -> viewModel.navigateTo(dest) }
        )
    }
}

@Composable
fun GameModePill(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradient: List<Color>,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .width(150.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(gradient))
            .clickable { onClick() }
            .padding(12.dp)
            .testTag(testTag)
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = subtitle,
                color = Color(0xCCFFFFFF),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun BottomNavigationCustom(
    currentScreen: ScreenDestination,
    onNavigate: (ScreenDestination) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(QuizSurface)
            .border(1.dp, CardBorderGlow, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(
            icon = Icons.Default.PlayArrow,
            label = "Jogar",
            isSelected = currentScreen is ScreenDestination.Home,
            onClick = { onNavigate(ScreenDestination.Home) },
            testTag = "nav_home"
        )
        BottomNavItem(
            icon = Icons.Default.EmojiEvents,
            label = "Ranking",
            isSelected = currentScreen is ScreenDestination.Leaderboard,
            onClick = { onNavigate(ScreenDestination.Leaderboard) },
            testTag = "nav_ranking"
        )
        BottomNavItem(
            icon = Icons.Default.FlashOn,
            label = "Diário",
            isSelected = currentScreen is ScreenDestination.DailyChallenge,
            onClick = { onNavigate(ScreenDestination.DailyChallenge) },
            testTag = "nav_daily"
        )
        BottomNavItem(
            icon = Icons.Default.ShoppingBag,
            label = "Loja VIP",
            isSelected = currentScreen is ScreenDestination.Store,
            onClick = { onNavigate(ScreenDestination.Store) },
            testTag = "nav_store"
        )
    }
}

@Composable
fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) QuizPrimaryLight else TextMuted,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isSelected) QuizPrimaryLight else TextMuted,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
