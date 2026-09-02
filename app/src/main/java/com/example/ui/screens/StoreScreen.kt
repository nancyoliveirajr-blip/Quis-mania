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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Stars
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.StoreProduct
import com.example.ui.theme.*
import com.example.ui.viewmodel.QuizViewModel
import com.example.ui.viewmodel.ScreenDestination

@Composable
fun StoreScreen(
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.userProfile.collectAsState()
    val products = viewModel.storeProducts

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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.navigateTo(ScreenDestination.Home) },
                    modifier = Modifier.testTag("store_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Loja & Planos VIP",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                    color = TextPrimary
                )
            }

            // Moedas do Jogador
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(QuizSurfaceVariant)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = null,
                    tint = QuizGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${user?.coins ?: 0}",
                    color = QuizGoldLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Destaque do Clube VIP
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF78350F),
                                    Color(0xFF451A03),
                                    QuizSurfaceVariant
                                )
                            )
                        )
                        .border(1.5.dp, GoldBorderGlow, RoundedCornerShape(22.dp))
                        .padding(18.dp)
                        .testTag("store_vip_banner")
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(QuizGold, QuizOrange))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Stars,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "PASSAPORTE VIP MANIA",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = QuizGoldLight
                                    )
                                    Text(
                                        text = "Experiência definitiva sem limites",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            if (user?.isVip == true) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(QuizEmerald)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("ATIVO", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 10.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        VipBenefitRow("Vidas ilimitadas: jogue quantas vezes quiser sem esperar")
                        VipBenefitRow("Sem anúncios: navegação 100% fluida e limpa")
                        VipBenefitRow("Multiplicador 2x de moedas e XP em todas as partidas")
                        VipBenefitRow("Emblema Dourado VIP no perfil e nos rankings")
                    }
                }
            }

            // Itens da Google Play Store (Assinaturas & Moedas)
            item {
                Text(
                    text = "Planos e Pacotes Google Play",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = TextPrimary
                )
            }

            items(products) { product ->
                StoreProductCard(
                    product = product,
                    onBuyClick = { viewModel.purchaseProduct(product.productId) }
                )
            }

            // Seção de Itens com Moedas do Jogo
            item {
                Text(
                    text = "Mercado Mania (Moedas In-Game)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                InGameItemRow(
                    title = "Recarregar Vidas ao Máximo",
                    description = "Restaura instantaneamente suas 5 vidas",
                    price = 100,
                    icon = Icons.Default.Favorite,
                    iconTint = QuizRuby,
                    onBuy = { viewModel.buyWithCoins("REFILL_LIVES", 100) },
                    testTag = "buy_lives_coins"
                )
            }

            item {
                InGameItemRow(
                    title = "Pacote 3x Poder 50/50",
                    description = "Elimina 2 alternativas incorretas",
                    price = 80,
                    icon = Icons.Default.Lightbulb,
                    iconTint = QuizGold,
                    onBuy = { viewModel.buyWithCoins("BUY_FIFTY_FIFTY", 80) },
                    testTag = "buy_fifty_fifty_coins"
                )
            }

            item {
                InGameItemRow(
                    title = "Pacote 3x Pular Pergunta",
                    description = "Pule perguntas difíceis sem penalidade",
                    price = 80,
                    icon = Icons.Default.SkipNext,
                    iconTint = QuizPrimaryLight,
                    onBuy = { viewModel.buyWithCoins("BUY_SKIP", 80) },
                    testTag = "buy_skip_coins"
                )
            }

            item {
                InGameItemRow(
                    title = "Pacote 3x Congelar Tempo",
                    description = "+15 segundos no cronômetro",
                    price = 80,
                    icon = Icons.Default.HourglassTop,
                    iconTint = QuizCyan,
                    onBuy = { viewModel.buyWithCoins("BUY_FREEZE", 80) },
                    testTag = "buy_freeze_coins"
                )
            }

            // Vídeos Premiados Grátis (Rewarded Ads)
            item {
                Text(
                    text = "Recompensas Gratuitas",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RewardedAdCard(
                        title = "+2 Vidas Grátis",
                        subtitle = "Assista a um vídeo",
                        icon = Icons.Default.Favorite,
                        iconTint = QuizRuby,
                        onClick = { viewModel.watchRewardedAd("FREE_LIVES") },
                        modifier = Modifier.weight(1f),
                        testTag = "rewarded_lives_button"
                    )

                    RewardedAdCard(
                        title = "+50 Moedas",
                        subtitle = "Assista a um vídeo",
                        icon = Icons.Default.MonetizationOn,
                        iconTint = QuizGold,
                        onClick = { viewModel.watchRewardedAd("FREE_COINS") },
                        modifier = Modifier.weight(1f),
                        testTag = "rewarded_coins_button"
                    )
                }
            }
        }

        BottomNavigationCustom(
            currentScreen = ScreenDestination.Store,
            onNavigate = { dest -> viewModel.navigateTo(dest) }
        )
    }
}

@Composable
fun VipBenefitRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 3.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = QuizGoldLight,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
            color = TextPrimary
        )
    }
}

@Composable
fun StoreProductCard(
    product: StoreProduct,
    onBuyClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(QuizSurfaceVariant)
            .border(1.dp, CardBorderGlow, RoundedCornerShape(18.dp))
            .padding(14.dp)
            .testTag("product_card_${product.productId}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = TextPrimary
                    )

                    product.badge?.let { badgeText ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Brush.horizontalGradient(listOf(QuizGold, QuizOrange)))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badgeText,
                                color = Color.Black,
                                fontWeight = FontWeight.Black,
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = onBuyClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = QuizPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                modifier = Modifier.testTag("buy_btn_${product.productId}")
            ) {
                Text(
                    text = product.priceFormatted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun InGameItemRow(
    title: String,
    description: String,
    price: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    onBuy: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(QuizSurface)
            .border(1.dp, CardBorderGlow, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(QuizSurfaceLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                    Text(text = description, fontSize = 11.sp, color = TextSecondary)
                }
            }

            Button(
                onClick = onBuy,
                colors = ButtonDefaults.buttonColors(
                    containerColor = QuizSurfaceLight,
                    contentColor = QuizGoldLight
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.testTag(testTag)
            ) {
                Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = QuizGold, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "$price", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun RewardedAdCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(QuizSurfaceVariant)
            .border(1.dp, CardBorderGlow, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(12.dp)
            .testTag(testTag)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(QuizSurfaceLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
            Text(text = subtitle, fontSize = 11.sp, color = TextMuted)

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.OndemandVideo, contentDescription = null, tint = QuizPrimaryLight, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Assistir Vídeo", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = QuizPrimaryLight)
            }
        }
    }
}
