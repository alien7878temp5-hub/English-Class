package com.example.ui.home

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CallMode
import com.example.data.model.CefrLevel
import com.example.data.model.TutorGender
import com.example.data.model.TutorPersona
import com.example.ui.MainViewModel
import com.example.ui.theme.BritishNavy
import com.example.ui.theme.BritishNavyDark
import com.example.ui.theme.BritishNavyLight
import com.example.ui.theme.BritishTextLight
import com.example.ui.theme.BritishTextLightMuted
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight
import com.example.ui.theme.SuccessGreen

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToIelts: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedGender by viewModel.selectedGender.collectAsState()
    val cefrLevel by viewModel.cefrLevel.collectAsState()
    val callMode by viewModel.callMode.collectAsState()
    val sessionCount by viewModel.sessionCount.collectAsState()
    val masteredCount by viewModel.masteredIdiomCount.collectAsState()
    val sessions by viewModel.allSessions.collectAsState()

    val latestBand = sessions.firstOrNull()?.estimatedBandScore ?: "Target 8.5"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BritishNavyDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // App Banner / Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "BRITISH TUTOR",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        letterSpacing = 1.sp,
                        color = RoyalGoldLight
                    )
                    Text(
                        text = "Received Pronunciation & IELTS Band 8.5+ Call Simulator",
                        fontSize = 11.sp,
                        color = BritishTextLightMuted
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(RoyalGold.copy(alpha = 0.15f))
                        .border(1.dp, RoyalGold, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🇬🇧 RP Audio",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalGold
                    )
                }
            }
        }

        // Overview Metric Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Calls Completed",
                    value = "$sessionCount",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "British Idioms",
                    value = "$masteredCount Mastered",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "IELTS Level",
                    value = latestBand,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section: Select Your British Tutor (Arthur vs Eleanor)
        item {
            SectionHeader(title = "Select Your Elite British Tutor")
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TutorSelectCard(
                    persona = TutorPersona.ARTHUR,
                    isSelected = selectedGender == TutorGender.MALE,
                    onSelect = { viewModel.selectTutor(TutorGender.MALE) },
                    onHearSample = { viewModel.hearSampleGreeting(TutorPersona.ARTHUR) },
                    testTag = "select_arthur_card",
                    modifier = Modifier.weight(1f)
                )
                TutorSelectCard(
                    persona = TutorPersona.ELEANOR,
                    isSelected = selectedGender == TutorGender.FEMALE,
                    onSelect = { viewModel.selectTutor(TutorGender.FEMALE) },
                    onHearSample = { viewModel.hearSampleGreeting(TutorPersona.ELEANOR) },
                    testTag = "select_eleanor_card",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Big Main CTA: Start Voice Call Now!
        item {
            Button(
                onClick = { viewModel.startCall() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .testTag("start_call_button"),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Start Voice Call",
                    tint = BritishNavyDark,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Call ${if (selectedGender == TutorGender.MALE) "Arthur" else "Eleanor"} (Voice Simulator)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BritishNavyDark
                )
            }
        }

        // Section: Target CEFR Level & IELTS Band
        item {
            SectionHeader(title = "Target CEFR Level & Speaking Pace")
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(CefrLevel.entries) { levelItem ->
                    val isSelected = levelItem == cefrLevel
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) RoyalGold else BritishNavy)
                            .border(
                                1.dp,
                                if (isSelected) RoyalGold else RoyalGold.copy(alpha = 0.2f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.selectCefrLevel(levelItem) }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .testTag("level_chip_${levelItem.code}")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = levelItem.code,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (isSelected) BritishNavyDark else BritishTextLight
                            )
                            Text(
                                text = levelItem.ieltsBand,
                                fontSize = 10.sp,
                                color = if (isSelected) BritishNavyDark.copy(alpha = 0.8f) else BritishTextLightMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BritishNavy),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "${cefrLevel.label} • ${cefrLevel.ieltsBand}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = RoyalGoldLight
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = cefrLevel.description,
                        fontSize = 12.sp,
                        color = BritishTextLight
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Focus: ${cefrLevel.pedagogicalFocus}",
                        fontSize = 11.sp,
                        color = BritishTextLightMuted
                    )
                }
            }
        }

        // Section: Practice Modes
        item {
            SectionHeader(title = "British Coaching Modes")
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CallMode.entries.forEach { modeItem ->
                    val isSelected = modeItem == callMode
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectCallMode(modeItem) }
                            .testTag("mode_card_${modeItem.name}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) BritishNavyLight else BritishNavy
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 1.5.dp else 0.5.dp,
                            color = if (isSelected) RoyalGold else RoyalGold.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val iconVector = when (modeItem) {
                                CallMode.DAILY_CHIT_CHAT -> Icons.Default.Coffee
                                CallMode.BRITISH_IDIOMS -> Icons.Default.AutoAwesome
                                CallMode.IELTS_SIMULATOR -> Icons.Default.School
                                CallMode.RECAST_AND_PRONUNCIATION -> Icons.Default.RecordVoiceOver
                            }
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) RoyalGold else BritishNavyDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = iconVector,
                                    contentDescription = null,
                                    tint = if (isSelected) BritishNavyDark else RoyalGoldLight,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = modeItem.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = BritishTextLight
                                )
                                Text(
                                    text = modeItem.subtitle,
                                    fontSize = 11.sp,
                                    color = BritishTextLightMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = BritishTextLight
    )
}

@Composable
private fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = BritishNavy),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = BritishTextLightMuted
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalGoldLight
            )
        }
    }
}

@Composable
private fun TutorSelectCard(
    persona: TutorPersona,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onHearSample: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onSelect() }
            .testTag(testTag),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) BritishNavyLight else BritishNavy
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) RoyalGold else RoyalGold.copy(alpha = 0.25f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tutor Initial Emblem
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(if (persona.gender == TutorGender.MALE) Color(0xFF1E3558) else Color(0xFF382348))
                    .border(2.dp, if (isSelected) RoyalGold else Color.Transparent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (persona.gender == TutorGender.MALE) "A" else "E",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = RoyalGoldLight
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = persona.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = BritishTextLight
            )
            Text(
                text = if (persona.gender == TutorGender.MALE) "Oxford RP Tutor" else "Cambridge Coach",
                fontSize = 11.sp,
                color = RoyalGold
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Sample Audio Preview Button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(BritishNavyDark)
                    .clickable { onHearSample() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Hear Sample",
                    tint = RoyalGold,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Hear Voice",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BritishTextLight
                )
            }
        }
    }
}
