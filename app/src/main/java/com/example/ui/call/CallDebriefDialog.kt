package com.example.ui.call

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.CallSessionEntity
import com.example.data.local.IdiomEntity
import com.example.data.local.RecastEntity
import com.example.ui.theme.BritishNavy
import com.example.ui.theme.BritishNavyDark
import com.example.ui.theme.BritishTextLight
import com.example.ui.theme.BritishTextLightMuted
import com.example.ui.theme.RecastBadgeColor
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight
import com.example.ui.theme.SuccessGreen

@Composable
fun CallDebriefDialog(
    session: CallSessionEntity?,
    recasts: List<RecastEntity>,
    idioms: List<IdiomEntity>,
    onDismiss: () -> Unit
) {
    if (session == null) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, RoyalGold.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .testTag("call_debrief_dialog"),
            color = BritishNavyDark
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    // Header with British Trophy / Crest
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(RoyalGold.copy(alpha = 0.2f))
                            .border(1.5.dp, RoyalGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Session Evaluation",
                            tint = RoyalGoldLight,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Call Debrief with ${session.tutorName}",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = RoyalGoldLight
                    )
                    Text(
                        text = "Duration: ${session.durationSeconds / 60}m ${session.durationSeconds % 60}s • ${session.totalTurns} Spoken Turns",
                        fontSize = 12.sp,
                        color = BritishTextLightMuted
                    )
                }

                // IELTS Band Score Estimate Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BritishNavy),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "ESTIMATED IELTS SPEAKING BAND",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = RoyalGold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = session.estimatedBandScore,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Serif,
                                color = BritishTextLight
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            ScoreCriterionRow(title = "Fluency & Coherence", score = session.fluencynScore)
                            Spacer(modifier = Modifier.height(8.dp))
                            ScoreCriterionRow(title = "Lexical Resource (RP & Idioms)", score = session.vocabularyScore)
                            Spacer(modifier = Modifier.height(8.dp))
                            ScoreCriterionRow(title = "Grammatical Range & Accuracy", score = session.grammarScore)
                            Spacer(modifier = Modifier.height(8.dp))
                            ScoreCriterionRow(title = "Pronunciation & Intonation", score = session.pronunciationScore)
                        }
                    }
                }

                // Gentle Recasting Section
                if (recasts.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.RecordVoiceOver,
                                    contentDescription = null,
                                    tint = RecastBadgeColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Gentle Recast Notes (${recasts.size})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = BritishTextLight
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            recasts.forEach { r ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1E2A)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "Learner: \"${r.originalUserInput}\"",
                                            fontSize = 12.sp,
                                            color = Color(0xFFFF8A80)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "British Recast: \"${r.correctedBritishPhrase}\"",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SuccessGreen
                                        )
                                        if (r.tutorExplanation.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "• ${r.tutorExplanation}",
                                                fontSize = 11.sp,
                                                color = BritishTextLightMuted
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // British Idioms Practiced Section
                if (idioms.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = RoyalGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "British Idioms Added to Notebook (${idioms.size})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = BritishTextLight
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            idioms.forEach { idm ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = BritishNavy),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = idm.idiom,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = RoyalGoldLight
                                            )
                                            Text(
                                                text = idm.phoneticRP,
                                                fontSize = 11.sp,
                                                color = BritishTextLightMuted
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = idm.meaning,
                                            fontSize = 12.sp,
                                            color = BritishTextLight
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Close Button
                item {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("dismiss_debrief_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = BritishNavyDark
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Splendid! Save & Return Home",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = BritishNavyDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreCriterionRow(title: String, score: Float) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, fontSize = 12.sp, color = BritishTextLight)
            Text(
                text = "${String.format(java.util.Locale.US, "%.1f", score)} / 9.0",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalGoldLight
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (score / 9.0f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = RoyalGold,
            trackColor = BritishNavyDark
        )
    }
}
