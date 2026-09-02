package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.CardBorderGlow
import com.example.ui.theme.QuizEmerald
import com.example.ui.theme.QuizEmeraldDark
import com.example.ui.theme.QuizPrimary
import com.example.ui.theme.QuizPrimaryLight
import com.example.ui.theme.QuizRuby
import com.example.ui.theme.QuizRubyDark
import com.example.ui.theme.QuizSurface
import com.example.ui.theme.QuizSurfaceLight
import com.example.ui.theme.QuizSurfaceVariant
import com.example.ui.theme.TextPrimary

enum class OptionButtonState {
    DEFAULT,
    SELECTED,
    CORRECT,
    WRONG,
    DISABLED
}

@Composable
fun OptionButton(
    index: Int,
    text: String,
    state: OptionButtonState,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val letter = when (index) {
        0 -> "A"
        1 -> "B"
        2 -> "C"
        3 -> "D"
        else -> "${index + 1}"
    }

    val backgroundColor by animateColorAsState(
        targetValue = when (state) {
            OptionButtonState.DEFAULT -> QuizSurfaceVariant
            OptionButtonState.SELECTED -> QuizPrimary.copy(alpha = 0.25f)
            OptionButtonState.CORRECT -> QuizEmeraldDark.copy(alpha = 0.35f)
            OptionButtonState.WRONG -> QuizRubyDark.copy(alpha = 0.35f)
            OptionButtonState.DISABLED -> QuizSurface.copy(alpha = 0.4f)
        },
        animationSpec = tween(250),
        label = "bg_color"
    )

    val borderColor by animateColorAsState(
        targetValue = when (state) {
            OptionButtonState.DEFAULT -> CardBorderGlow
            OptionButtonState.SELECTED -> QuizPrimaryLight
            OptionButtonState.CORRECT -> QuizEmerald
            OptionButtonState.WRONG -> QuizRuby
            OptionButtonState.DISABLED -> Color.Transparent
        },
        animationSpec = tween(250),
        label = "border_color"
    )

    val badgeBg = when (state) {
        OptionButtonState.DEFAULT -> Brush.linearGradient(listOf(QuizSurfaceLight, QuizSurfaceVariant))
        OptionButtonState.SELECTED -> Brush.linearGradient(listOf(QuizPrimary, QuizPrimaryLight))
        OptionButtonState.CORRECT -> Brush.linearGradient(listOf(QuizEmerald, Color(0xFF34D399)))
        OptionButtonState.WRONG -> Brush.linearGradient(listOf(QuizRuby, Color(0xFFF87171)))
        OptionButtonState.DISABLED -> Brush.linearGradient(listOf(QuizSurface, QuizSurface))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .testTag("option_button_$index")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Letra de Opção (A, B, C, D)
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(badgeBg),
                contentAlignment = Alignment.Center
            ) {
                when (state) {
                    OptionButtonState.CORRECT -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Correto",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    OptionButtonState.WRONG -> {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Incorreto",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    else -> {
                        Text(
                            text = letter,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Texto da alternativa
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (state == OptionButtonState.SELECTED || state == OptionButtonState.CORRECT) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 15.sp
                ),
                color = if (state == OptionButtonState.DISABLED) Color.Gray else TextPrimary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
