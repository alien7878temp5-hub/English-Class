package com.example.ui.notebook

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CallSessionEntity
import com.example.data.local.RecastEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.BritishNavy
import com.example.ui.theme.BritishNavyDark
import com.example.ui.theme.BritishNavyLight
import com.example.ui.theme.BritishTextLight
import com.example.ui.theme.BritishTextLightMuted
import com.example.ui.theme.RecastBadgeColor
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight
import com.example.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotebookScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val sessions by viewModel.allSessions.collectAsState()
    val recasts by viewModel.allRecasts.collectAsState()
    val idioms by viewModel.allIdioms.collectAsState()
    val persona by viewModel.currentPersona.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BritishNavyDark)
            .padding(horizontal = 16.dp)
            .testTag("notebook_screen")
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Title Header
        Text(
            text = "Learning Notebook & Memory",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = RoyalGoldLight
        )
        Text(
            text = "Track recasts, session debriefs, and spaced recall mastery",
            fontSize = 12.sp,
            color = BritishTextLightMuted
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Notebook Subtabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = BritishNavy,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = RoyalGold
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Recast Notes (${recasts.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (selectedTab == 0) RoyalGold else BritishTextLightMuted) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Call History (${sessions.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (selectedTab == 1) RoyalGold else BritishTextLightMuted) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Spaced Recall Quiz", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (selectedTab == 2) RoyalGold else BritishTextLightMuted) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (selectedTab) {
            0 -> RecastsListTab(recasts = recasts, onMarkMastered = { viewModel.markRecastMastered(it) })
            1 -> CallHistoryTab(sessions = sessions)
            2 -> SpacedRecallQuizTab(
                recasts = recasts,
                idioms = idioms,
                onPronounce = { viewModel.speakText(it) }
            )
        }
    }
}

@Composable
private fun RecastsListTab(
    recasts: List<RecastEntity>,
    onMarkMastered: (RecastEntity) -> Unit
) {
    if (recasts.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.RecordVoiceOver,
                    contentDescription = null,
                    tint = RoyalGold.copy(alpha = 0.5f),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No Recast Corrections Logged Yet",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = BritishTextLight
                )
                Text(
                    text = "Arthur and Eleanor gently recast errors during live voice calls.",
                    fontSize = 12.sp,
                    color = BritishTextLightMuted
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(recasts) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = BritishNavy),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (item.mastered) SuccessGreen.copy(alpha = 0.5f) else RecastBadgeColor.copy(alpha = 0.4f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Learner said: \"${item.originalUserInput}\"",
                                fontSize = 12.sp,
                                color = Color(0xFFFF8A80)
                            )
                            IconButton(
                                onClick = { onMarkMastered(item) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Mark as Mastered",
                                    tint = if (item.mastered) SuccessGreen else BritishTextLightMuted
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "British Recast: \"${item.correctedBritishPhrase}\"",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                        if (item.tutorExplanation.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• ${item.tutorExplanation}",
                                fontSize = 11.sp,
                                color = BritishTextLightMuted
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun CallHistoryTab(sessions: List<CallSessionEntity>) {
    if (sessions.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = RoyalGold.copy(alpha = 0.5f),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No Previous Calls",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = BritishTextLight
                )
                Text(
                    text = "Start a voice call simulator from the Home tab to log your history.",
                    fontSize = 12.sp,
                    color = BritishTextLightMuted
                )
            }
        }
    } else {
        val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.UK) }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(sessions) { s ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = BritishNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RoyalGold.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tutor: ${s.tutorName} (${s.cefrLevel})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = RoyalGoldLight
                            )
                            Text(
                                text = s.estimatedBandScore,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                color = RoyalGold
                            )
                        }
                        Text(
                            text = dateFormat.format(Date(s.timestamp)),
                            fontSize = 10.sp,
                            color = BritishTextLightMuted
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Mode: ${s.callMode} • Duration: ${s.durationSeconds / 60}m ${s.durationSeconds % 60}s • ${s.totalTurns} Spoken Turns",
                            fontSize = 11.sp,
                            color = BritishTextLight
                        )
                        if (s.summaryFeedback.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = s.summaryFeedback,
                                fontSize = 11.sp,
                                color = BritishTextLightMuted
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun SpacedRecallQuizTab(
    recasts: List<RecastEntity>,
    idioms: List<com.example.data.local.IdiomEntity>,
    onPronounce: (String) -> Unit
) {
    var quizIndex by remember { mutableIntStateOf(0) }
    var isAnswerRevealed by remember { mutableStateOf(false) }

    // Combined quiz pool of recasts and idioms
    val quizItems = remember(recasts, idioms) {
        val list = mutableListOf<QuizItem>()
        recasts.forEach {
            list.add(QuizItem(
                question = "How would an elite British speaker recast: \"${it.originalUserInput}\"?",
                answer = it.correctedBritishPhrase,
                explanation = it.tutorExplanation
            ))
        }
        idioms.take(8).forEach {
            list.add(QuizItem(
                question = "What does the British idiom '${it.idiom}' mean?",
                answer = it.meaning,
                explanation = "RP Pronunciation: ${it.phoneticRP} | Example: \"${it.britishExample}\""
            ))
        }
        list
    }

    if (quizItems.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Complete your first call to generate personalized recall questions!",
                color = BritishTextLightMuted,
                fontSize = 13.sp
            )
        }
        return
    }

    val currentItem = quizItems[quizIndex % quizItems.size]

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = BritishNavy),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, RoyalGold)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(RoyalGold.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = RoyalGold,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Question ${(quizIndex % quizItems.size) + 1} of ${quizItems.size}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BritishTextLightMuted
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currentItem.question,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = BritishTextLight
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isAnswerRevealed) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BritishNavyDark),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🇬🇧 British Answer:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalGold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentItem.answer,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SuccessGreen
                            )
                            if (currentItem.explanation.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = currentItem.explanation,
                                    fontSize = 11.sp,
                                    color = BritishTextLightMuted
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            IconButton(
                                onClick = { onPronounce(currentItem.answer) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Pronounce",
                                    tint = RoyalGold
                                )
                            }
                        }
                    }
                } else {
                    Button(
                        onClick = { isAnswerRevealed = true },
                        colors = ButtonDefaults.buttonColors(containerColor = BritishNavyLight),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = RoyalGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Reveal British Form",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BritishTextLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            isAnswerRevealed = false
                            quizIndex = (quizIndex + 1) % quizItems.size
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Next Question", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BritishNavyDark)
                    }
                }
            }
        }
    }
}

private data class QuizItem(
    val question: String,
    val answer: String,
    val explanation: String
)
