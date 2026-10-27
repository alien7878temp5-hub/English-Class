package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.call.CallDebriefDialog
import com.example.ui.call.CallScreen
import com.example.ui.home.HomeScreen
import com.example.ui.idioms.IdiomsScreen
import com.example.ui.ielts.IeltsScreen
import com.example.ui.notebook.NotebookScreen
import com.example.ui.theme.BritishNavy
import com.example.ui.theme.BritishNavyDark
import com.example.ui.theme.BritishTextLight
import com.example.ui.theme.BritishTextLightMuted
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val isCallActive by viewModel.isCallActive.collectAsState()
    val showDebrief by viewModel.showDebriefDialog.collectAsState()
    val lastSession by viewModel.lastCompletedSession.collectAsState()
    val activeRecasts by viewModel.activeSessionRecasts.collectAsState()
    val activeIdioms by viewModel.activeSessionIdioms.collectAsState()

    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    // If an active voice call is in progress, show the immersive call simulator
    if (isCallActive) {
        CallScreen(viewModel = viewModel)
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    containerColor = BritishNavy,
                    contentColor = BritishTextLight,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call"
                            )
                        },
                        label = { Text("Call Tutor", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BritishNavyDark,
                            selectedTextColor = RoyalGold,
                            indicatorColor = RoyalGold,
                            unselectedIconColor = BritishTextLightMuted,
                            unselectedTextColor = BritishTextLightMuted
                        ),
                        modifier = Modifier.testTag("tab_home")
                    )

                    NavigationBarItem(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "IELTS"
                            )
                        },
                        label = { Text("IELTS Prep", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BritishNavyDark,
                            selectedTextColor = RoyalGold,
                            indicatorColor = RoyalGold,
                            unselectedIconColor = BritishTextLightMuted,
                            unselectedTextColor = BritishTextLightMuted
                        ),
                        modifier = Modifier.testTag("tab_ielts")
                    )

                    NavigationBarItem(
                        selected = selectedTabIndex == 2,
                        onClick = { selectedTabIndex = 2 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Slang & Idioms"
                            )
                        },
                        label = { Text("Slang Vault", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BritishNavyDark,
                            selectedTextColor = RoyalGold,
                            indicatorColor = RoyalGold,
                            unselectedIconColor = BritishTextLightMuted,
                            unselectedTextColor = BritishTextLightMuted
                        ),
                        modifier = Modifier.testTag("tab_idioms")
                    )

                    NavigationBarItem(
                        selected = selectedTabIndex == 3,
                        onClick = { selectedTabIndex = 3 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = "Notebook"
                            )
                        },
                        label = { Text("Notebook", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BritishNavyDark,
                            selectedTextColor = RoyalGold,
                            indicatorColor = RoyalGold,
                            unselectedIconColor = BritishTextLightMuted,
                            unselectedTextColor = BritishTextLightMuted
                        ),
                        modifier = Modifier.testTag("tab_notebook")
                    )
                }
            }
        ) { innerPadding ->
            BackHandler(enabled = selectedTabIndex != 0) {
                selectedTabIndex = 0
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BritishNavyDark)
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = selectedTabIndex,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tab_navigation"
                ) { targetTab ->
                    when (targetTab) {
                        0 -> HomeScreen(
                            viewModel = viewModel,
                            onNavigateToIelts = { selectedTabIndex = 1 }
                        )
                        1 -> IeltsScreen(viewModel = viewModel)
                        2 -> IdiomsScreen(viewModel = viewModel)
                        3 -> NotebookScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

    // Post-Call Debrief Dialog
    if (showDebrief && lastSession != null) {
        CallDebriefDialog(
            session = lastSession,
            recasts = activeRecasts,
            idioms = activeIdioms,
            onDismiss = { viewModel.dismissDebriefDialog() }
        )
    }
}
