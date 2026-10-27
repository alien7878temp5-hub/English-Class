package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TutorGender
import com.example.data.model.TutorPersona
import com.example.ui.theme.BritishNavy
import com.example.ui.theme.BritishNavyDark
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight

@Composable
fun PulsingWaveform(
    persona: TutorPersona,
    isSpeaking: Boolean,
    amplitude: Float,
    size: Dp = 220.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")

    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isSpeaking) 1.25f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isSpeaking) 900 else 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse1"
    )

    val pulse2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isSpeaking) 1.45f else 1.10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isSpeaking) 1200 else 3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse2"
    )

    val dynamicAmp = if (isSpeaking) (amplitude * 0.25f) else 0f

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulsing acoustic rings
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerOffset = center
            val baseRadius = size.toPx() / 2f * 0.65f

            // Ring 2 (outer faint ring)
            val r2 = baseRadius * (pulse2 + dynamicAmp)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        RoyalGold.copy(alpha = if (isSpeaking) 0.35f else 0.12f),
                        Color.Transparent
                    ),
                    center = centerOffset,
                    radius = r2
                ),
                radius = r2,
                center = centerOffset
            )
            drawCircle(
                color = RoyalGold.copy(alpha = if (isSpeaking) 0.5f else 0.2f),
                radius = r2,
                center = centerOffset,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Ring 1 (inner energetic ring)
            val r1 = baseRadius * (pulse1 + dynamicAmp * 0.5f)
            drawCircle(
                color = RoyalGoldLight.copy(alpha = if (isSpeaking) 0.7f else 0.3f),
                radius = r1,
                center = centerOffset,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Central Avatar Sphere with British Academic Monogram
        val avatarBg = if (persona.gender == TutorGender.MALE) {
            Brush.linearGradient(listOf(Color(0xFF1E3558), Color(0xFF0D1B36)))
        } else {
            Brush.linearGradient(listOf(Color(0xFF382348), Color(0xFF161C38)))
        }

        Box(
            modifier = Modifier
                .size(size * 0.65f)
                .clip(CircleShape)
                .background(avatarBg)
                .border(3.dp, RoyalGold, CircleShape)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(BritishNavyDark.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                // Classic initial letter with British styling
                Text(
                    text = if (persona.gender == TutorGender.MALE) "A" else "E",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = (size.value * 0.26f).sp,
                    color = RoyalGoldLight
                )
            }
        }
    }
}
