package com.example.ui.idioms

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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.local.IdiomEntity
import com.example.data.model.CallMode
import com.example.ui.MainViewModel
import com.example.ui.theme.BritishNavy
import com.example.ui.theme.BritishNavyDark
import com.example.ui.theme.BritishNavyLight
import com.example.ui.theme.BritishTextLight
import com.example.ui.theme.BritishTextLightMuted
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight

@Composable
fun IdiomsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val allIdioms by viewModel.allIdioms.collectAsState()
    val persona by viewModel.currentPersona.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Emotions & Pride", "Emotions & Disappointment", "Colloquial Intensifiers", "Iconic British Sayings", "Preferences", "In-Call Practice")

    val filteredIdioms = allIdioms.filter { idiom ->
        val matchesSearch = idiom.idiom.contains(searchQuery, ignoreCase = true) ||
                idiom.meaning.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || idiom.category.equals(selectedCategory, ignoreCase = true)
        matchesSearch && matchesCategory
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BritishNavyDark)
            .padding(horizontal = 16.dp)
            .testTag("idioms_screen")
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Title Header
        Text(
            text = "British Slang & Idioms Vault",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = RoyalGoldLight
        )
        Text(
            text = "Authentic UK expressions & RP pronunciation drills with ${persona.name}",
            fontSize = 12.sp,
            color = BritishTextLightMuted
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_idioms_field"),
            placeholder = { Text("Search slang or meaning (e.g., 'chuffed', 'gutted')...", fontSize = 12.sp, color = BritishTextLightMuted) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = RoyalGold
                )
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = RoyalGold,
                unfocusedBorderColor = RoyalGold.copy(alpha = 0.3f),
                focusedTextColor = BritishTextLight,
                unfocusedTextColor = BritishTextLight,
                focusedContainerColor = BritishNavy,
                unfocusedContainerColor = BritishNavy
            ),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { cat ->
                val isSelected = cat == selectedCategory
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) RoyalGold else BritishNavy)
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = cat,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) BritishNavyDark else BritishTextLight
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Idioms List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredIdioms) { idiom ->
                IdiomCard(
                    idiom = idiom,
                    onPronounce = { viewModel.speakText("${idiom.idiom}. ${idiom.britishExample}") },
                    onToggleFavorite = { viewModel.toggleIdiomFavorite(idiom) },
                    onPracticeInCall = {
                        viewModel.startCall(initialMode = CallMode.BRITISH_IDIOMS)
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
private fun IdiomCard(
    idiom: IdiomEntity,
    onPronounce: () -> Unit,
    onToggleFavorite: () -> Unit,
    onPracticeInCall: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BritishNavy),
        border = androidx.compose.foundation.BorderStroke(1.dp, RoyalGold.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Idiom name, Audio listen button & Star Favorite
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = idiom.idiom,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = RoyalGoldLight
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onPronounce,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Hear British Pronunciation",
                            tint = RoyalGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (idiom.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Toggle Favorite",
                        tint = if (idiom.isFavorite) RoyalGold else BritishTextLightMuted
                    )
                }
            }

            // RP Phonetic & Category Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "RP: ${idiom.phoneticRP}",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF81D4FA)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(BritishNavyDark)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = idiom.category,
                        fontSize = 10.sp,
                        color = BritishTextLightMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            // Meaning
            Text(
                text = idiom.meaning,
                fontSize = 13.sp,
                color = BritishTextLight
            )

            Spacer(modifier = Modifier.height(6.dp))
            // British Example Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BritishNavyDark),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "🇬🇧 \"${idiom.britishExample}\"",
                    fontSize = 12.sp,
                    color = BritishTextLightMuted,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            // Action button to practice
            Button(
                onClick = onPracticeInCall,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BritishNavyLight),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = null,
                    tint = RoyalGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Practice in Live Call",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BritishTextLight
                )
            }
        }
    }
}
