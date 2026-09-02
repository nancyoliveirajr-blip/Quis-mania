package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserPlayer
import com.example.ui.theme.CardBorderGlow
import com.example.ui.theme.QuizEmerald
import com.example.ui.theme.QuizGold
import com.example.ui.theme.QuizGoldDark
import com.example.ui.theme.QuizGoldLight
import com.example.ui.theme.QuizPrimary
import com.example.ui.theme.QuizRuby
import com.example.ui.theme.QuizSurface
import com.example.ui.theme.QuizSurfaceVariant
import com.example.ui.theme.TextPrimary

@Composable
fun QuizTopBar(
    user: UserPlayer?,
    onStoreClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Perfil / Nível
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(QuizSurfaceVariant)
                .border(1.dp, CardBorderGlow, RoundedCornerShape(24.dp))
                .clickable { onProfileClick() }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("topbar_profile_button"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            if (user?.isVip == true) listOf(QuizGold, QuizGoldLight)
                            else listOf(QuizPrimary, Color(0xFF6366F1))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${user?.level ?: 1}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = user?.username ?: "Jogador",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
                color = TextPrimary,
                maxLines = 1
            )

            if (user?.isVip == true) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Brush.horizontalGradient(listOf(QuizGold, QuizGoldDark)))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "VIP",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 9.sp
                    )
                }
            }
        }

        // Recursos (Vidas e Moedas)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Vidas
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(QuizSurfaceVariant)
                    .border(1.dp, CardBorderGlow, RoundedCornerShape(20.dp))
                    .clickable { onStoreClick() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("topbar_lives_indicator"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Vidas",
                    tint = QuizRuby,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                if (user?.isVip == true) {
                    Icon(
                        imageVector = Icons.Default.AllInclusive,
                        contentDescription = "Vidas Infinitas",
                        tint = QuizRuby,
                        modifier = Modifier.size(14.dp)
                    )
                } else {
                    Text(
                        text = "${user?.lives ?: 5}/${user?.maxLives ?: 5}",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            // Moedas
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(QuizSurfaceVariant)
                    .border(1.dp, CardBorderGlow, RoundedCornerShape(20.dp))
                    .clickable { onStoreClick() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("topbar_coins_indicator"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = "Moedas",
                    tint = QuizGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${user?.coins ?: 0}",
                    color = QuizGoldLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
