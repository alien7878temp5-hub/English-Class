package com.example.ui.call

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.example.ui.MainViewModel
import com.example.ui.components.PulsingWaveform
import com.example.ui.theme.BritishCrimson
import com.example.ui.theme.BritishNavy
import com.example.ui.theme.BritishNavyDark
import com.example.ui.theme.BritishTextLight
import com.example.ui.theme.BritishTextLightMuted
import com.example.ui.theme.IdiomBadgeColor
import com.example.ui.theme.RecastBadgeColor
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight
import com.example.ui.theme.SuccessGreen

@Composable
fun CallScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val persona by viewModel.currentPersona.collectAsState()
    val level by viewModel.cefrLevel.collectAsState()
    val callMode by viewModel.callMode.collectAsState()
    val durationSeconds by viewModel.callDurationSeconds.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val isThinking by viewModel.isThinking.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()
    val isSpeakerOn by viewModel.isSpeakerOn.collectAsState()

    val isTtsSpeaking by viewModel.ttsEngine.isSpeaking.collectAsState()
    val ttsAmplitude by viewModel.ttsEngine.voiceAmplitude.collectAsState()
    val isListening by viewModel.speechEngine.isListening.collectAsState()
    val speechAmplitude by viewModel.speechEngine.inputAmplitude.collectAsState()
    val liveSpokenText by viewModel.speechEngine.partialText.collectAsState()

    val listState = rememberLazyListState()
    var showKeyboardDrawer by remember { mutableStateOf(false) }
    var typedInputText by remember { mutableStateOf("") }
    var hasRecordAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasRecordAudioPermission = isGranted
        if (isGranted) {
            viewModel.toggleMicrophone()
        }
    }

    // Auto-scroll transcript when new message arrives
    LaunchedEffect(messages.size, liveSpokenText) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Intercept back button to end call gracefully
    BackHandler {
        viewModel.endCall()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BritishNavyDark)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("call_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Caller Identity & Call Duration
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = persona.name,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = RoyalGoldLight
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(RoyalGold.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "RP Accent",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = RoyalGold
                            )
                        }
                    }
                    Text(
                        text = "${level.code} • ${callMode.title}",
                        fontSize = 12.sp,
                        color = BritishTextLightMuted
                    )
                }

                // Call Duration Digital Clock
                val minutes = durationSeconds / 60
                val seconds = durationSeconds % 60
                val formattedTime = String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(BritishNavy)
                        .border(1.dp, RoyalGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = formattedTime,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = BritishTextLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Central Pulsing Avatar Soundwave
            val currentAmplitude = if (isTtsSpeaking) ttsAmplitude else if (isListening) speechAmplitude else 0f
            val isAudioActive = isTtsSpeaking || isListening

            PulsingWaveform(
                persona = persona,
                isSpeaking = isAudioActive,
                amplitude = currentAmplitude,
                size = 190.dp
            )

            // Audio Status Pill
            Spacer(modifier = Modifier.height(8.dp))
            val statusLabel = when {
                isTtsSpeaking -> "${persona.name} is speaking..."
                isThinking -> "${persona.name} is pondering..."
                isListening -> "Listening to your British voice..."
                else -> "Tap microphone or type to speak"
            }
            Text(
                text = statusLabel,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (isListening) SuccessGreen else if (isTtsSpeaking) RoyalGoldLight else BritishTextLightMuted
            )

            // Live Transcript & Dialogue History
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BritishNavy.copy(alpha = 0.85f))
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages) { msg ->
                        MessageBubble(msg = msg, tutorName = persona.name)
                    }

                    // Live Speech Transcription Bubble while user is talking
                    if (isListening && liveSpokenText.isNotBlank()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A5F))
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.GraphicEq,
                                        contentDescription = null,
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = liveSpokenText,
                                        fontSize = 13.sp,
                                        color = BritishTextLight
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Optional Keyboard Input Drawer
            AnimatedVisibility(
                visible = showKeyboardDrawer,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = typedInputText,
                        onValueChange = { typedInputText = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_text_input"),
                        placeholder = { Text("Type in English...", fontSize = 13.sp, color = BritishTextLightMuted) },
                        maxLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalGold,
                            unfocusedBorderColor = RoyalGold.copy(alpha = 0.4f),
                            focusedTextColor = BritishTextLight,
                            unfocusedTextColor = BritishTextLight,
                            focusedContainerColor = BritishNavy,
                            unfocusedContainerColor = BritishNavy
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (typedInputText.isNotBlank()) {
                                viewModel.sendMessage(typedInputText.trim())
                                typedInputText = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(RoyalGold)
                            .testTag("send_text_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send Message",
                            tint = BritishNavyDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom In-Call Phone Control Panel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mute Mic Toggle
                CallActionButton(
                    icon = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    label = if (isMuted) "Unmute" else "Mute",
                    isActive = isMuted,
                    activeColor = Color(0xFFD32F2F),
                    testTag = "mute_button",
                    onClick = { viewModel.toggleMute() }
                )

                // Keyboard / Text Input Toggle
                CallActionButton(
                    icon = Icons.Default.Keyboard,
                    label = if (showKeyboardDrawer) "Hide Key" else "Type",
                    isActive = showKeyboardDrawer,
                    activeColor = RoyalGold,
                    testTag = "keyboard_toggle_button",
                    onClick = { showKeyboardDrawer = !showKeyboardDrawer }
                )

                // Push-to-Talk / Tap-to-Speak (Main Action)
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(
                            if (isListening) SuccessGreen else RoyalGold
                        )
                        .clickable {
                            if (!hasRecordAudioPermission) {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            } else {
                                viewModel.toggleMicrophone()
                            }
                        }
                        .testTag("mic_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                        contentDescription = "Speak to British Tutor",
                        tint = BritishNavyDark,
                        modifier = Modifier.size(34.dp)
                    )
                }

                // Speaker Toggle
                CallActionButton(
                    icon = if (isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                    label = if (isSpeakerOn) "Speaker" else "Earpiece",
                    isActive = isSpeakerOn,
                    activeColor = RoyalGold,
                    testTag = "speaker_button",
                    onClick = { viewModel.toggleSpeaker() }
                )

                // End Call Button
                CallActionButton(
                    icon = Icons.Default.CallEnd,
                    label = "End",
                    isActive = true,
                    activeColor = BritishCrimson,
                    testTag = "end_call_button",
                    onClick = { viewModel.endCall() }
                )
            }
        }
    }
}

@Composable
private fun MessageBubble(msg: ChatMessage, tutorName: String) {
    val isUser = msg.sender == MessageSender.USER
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Speaker Label
        Text(
            text = if (isUser) "You" else "$tutorName (RP Tutor)",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isUser) RoyalGoldLight else RoyalGold
        )
        Spacer(modifier = Modifier.height(2.dp))

        // Message Box
        Card(
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (isUser) 14.dp else 2.dp,
                bottomEnd = if (isUser) 2.dp else 14.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) Color(0xFF1E3A5F) else BritishNavyDark
            ),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = msg.text,
                    fontSize = 14.sp,
                    color = BritishTextLight,
                    lineHeight = 20.sp
                )

                // Gentle Recasting Badge
                msg.recast?.let { recast ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(RecastBadgeColor.copy(alpha = 0.2f))
                            .border(1.dp, RecastBadgeColor, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                text = "🇬🇧 Gentle Recast: \"${recast.correctedBritishPhrase}\"",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = RoyalGoldLight
                            )
                            Text(
                                text = recast.tutorNote,
                                fontSize = 11.sp,
                                color = BritishTextLightMuted
                            )
                        }
                    }
                }

                // British Idiom Card Badge
                msg.idiomIntroduced?.let { idiom ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(IdiomBadgeColor.copy(alpha = 0.25f))
                            .border(1.dp, IdiomBadgeColor, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "🇬🇧 British Slang: ${idiom.idiom}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = RoyalGoldLight
                                )
                                Text(
                                    text = idiom.phoneticRP,
                                    fontSize = 10.sp,
                                    color = BritishTextLightMuted
                                )
                            }
                            Text(
                                text = idiom.meaning,
                                fontSize = 11.sp,
                                color = BritishTextLight
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CallActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    activeColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (isActive) activeColor else BritishNavy)
                .border(1.dp, RoyalGold.copy(alpha = 0.3f), CircleShape)
                .testTag(testTag),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive && activeColor == RoyalGold) BritishNavyDark else BritishTextLight,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = BritishTextLightMuted
        )
    }
}
