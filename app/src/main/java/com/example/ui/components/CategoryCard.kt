package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuizCategory
import com.example.ui.theme.QuizSurface
import com.example.ui.theme.QuizSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CategoryCard(
    category: QuizCategory,
    questionCount: Int = 26,
    onCategoryClick: (QuizCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = Color(category.primaryColor)
    val secondaryColor = Color(category.secondaryColor)

    val iconVector: ImageVector = when (category) {
        QuizCategory.GERAL -> Icons.Default.Public
        QuizCategory.BIBLIA -> Icons.Default.MenuBook
        QuizCategory.HISTORIA -> Icons.Default.AccountBalance
        QuizCategory.GEOGRAFIA -> Icons.Default.Explore
        QuizCategory.CIENCIA -> Icons.Default.Science
        QuizCategory.MATEMATICA -> Icons.Default.Calculate
        QuizCategory.PORTUGUES -> Icons.Default.Spellcheck
        QuizCategory.TECNOLOGIA -> Icons.Default.Memory
        QuizCategory.FUTEBOL -> Icons.Default.SportsSoccer
        QuizCategory.MUSICA -> Icons.Default.MusicNote
        QuizCategory.FILMES_SERIES -> Icons.Default.Movie
        QuizCategory.CURIOSIDADES -> Icons.Default.Psychology
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        QuizSurfaceVariant,
                        QuizSurface
                    )
                )
            )
            .border(1.dp, categoryColor.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
            .clickable { onCategoryClick(category) }
            .padding(14.dp)
            .testTag("category_card_${category.id}")
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
                // Ícone estilizado com gradiente vibrante
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(listOf(categoryColor, secondaryColor))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = category.displayName,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = category.description,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }

            // Indicador de ação e contador
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(categoryColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$questionCount Qs",
                        color = categoryColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Jogar",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
