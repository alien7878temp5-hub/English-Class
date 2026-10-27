package com.example.ui.ielts

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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.local.PreloadedData
import com.example.data.model.CallMode
import com.example.data.model.IeltsPart
import com.example.data.model.IeltsTopic
import com.example.ui.MainViewModel
import com.example.ui.theme.BritishNavy
import com.example.ui.theme.BritishNavyDark
import com.example.ui.theme.BritishNavyLight
import com.example.ui.theme.BritishTextLight
import com.example.ui.theme.BritishTextLightMuted
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight
import kotlinx.coroutines.delay

@Composable
fun IeltsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val persona by viewModel.currentPersona.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val topics = PreloadedData.IELTS_TOPICS

    val currentPart = when (selectedTab) {
        0 -> IeltsPart.PART_1
        1 -> IeltsPart.PART_2
        else -> IeltsPart.PART_3
    }
    val filteredTopics = topics.filter { it.part == currentPart }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BritishNavyDark)
            .padding(horizontal = 16.dp)
            .testTag("ielts_screen")
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Title Header
        Text(
            text = "IELTS Speaking Simulator",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = RoyalGoldLight
        )
        Text(
            text = "Official Cambridge criteria targeting Band 8.5+ with ${persona.name}",
            fontSize = 12.sp,
            color = BritishTextLightMuted
        )

        Spacer(modifier = Modifier.height(14.dp))

        // IELTS Part Tabs
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
                text = { Text("Part 1: Warm-up", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selectedTab == 0) RoyalGold else BritishTextLightMuted) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Part 2: Cue Card", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selectedTab == 1) RoyalGold else BritishTextLightMuted) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Part 3: Discussion", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selectedTab == 2) RoyalGold else BritishTextLightMuted) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filteredTopics) { topic ->
                IeltsTopicCard(
                    topic = topic,
                    tutorName = persona.name,
                    onStartPracticeCall = {
                        viewModel.startCall(
                            initialMode = CallMode.IELTS_SIMULATOR,
                            topic = topic
                        )
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun IeltsTopicCard(
    topic: IeltsTopic,
    tutorName: String,
    onStartPracticeCall: () -> Unit
) {
    var prepTimerSeconds by remember { mutableIntStateOf(60) }
    var isTimerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (prepTimerSeconds > 0) {
                delay(1000)
                prepTimerSeconds -= 1
            }
            isTimerRunning = false
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BritishNavy),
        border = androidx.compose.foundation.BorderStroke(1.dp, RoyalGold.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Part Badge & Target Band
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(RoyalGold.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = topic.part.name.replace("_", " "),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalGold
                    )
                }

                Text(
                    text = topic.targetBandLevel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = RoyalGoldLight
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = topic.topicTitle,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = BritishTextLight
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = topic.promptDescription,
                fontSize = 12.sp,
                color = BritishTextLightMuted
            )

            // Bullet points for candidate prompts
            if (topic.bulletPoints.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BritishNavyDark),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "You should discuss:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = RoyalGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        topic.bulletPoints.forEach { point ->
                            Text(
                                text = "• $point",
                                fontSize = 12.sp,
                                color = BritishTextLight,
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }
                    }
                }
            }

            // High-Band British Collocations
            if (topic.suggestedBritishPhrases.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "High-scoring British collocations:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = RoyalGoldLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                topic.suggestedBritishPhrases.forEach { phrase ->
                    Text(
                        text = "✓ \"$phrase\"",
                        fontSize = 12.sp,
                        color = Color(0xFFA5D6A7),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Part 2 Prep Timer
            if (topic.part == IeltsPart.PART_2) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(BritishNavyLight)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = RoyalGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "1-Min Prep: ${prepTimerSeconds}s",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BritishTextLight
                        )
                    }

                    Row {
                        IconButton(
                            onClick = { isTimerRunning = !isTimerRunning },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isTimerRunning) Icons.Default.Timer else Icons.Default.PlayArrow,
                                contentDescription = "Toggle Timer",
                                tint = RoyalGoldLight
                            )
                        }
                        IconButton(
                            onClick = {
                                isTimerRunning = false
                                prepTimerSeconds = 60
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset Timer",
                                tint = BritishTextLightMuted
                            )
                        }
                    }
                }
            }

            // Launch Practice Call CTA
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onStartPracticeCall,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = null,
                    tint = BritishNavyDark,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Practice this topic with $tutorName",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BritishNavyDark
                )
            }
        }
    }
}
