package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.WhatsAppShareDialog
import com.example.ui.screens.AgentsScreen
import com.example.ui.screens.CounterScreen
import com.example.ui.screens.DailyHisabScreen
import com.example.ui.screens.ShopSettingsScreen

enum class AppTab(val labelHindi: String, val labelEnglish: String) {
    COUNTER("नोट काउंटर", "Counter"),
    DAILY_HISAB("दैनिक हिसाब", "Daily Ledger"),
    AGENTS("एजेंट्स", "Agents"),
    SHOP_PROFILE("दुकान", "Shop Profile")
}

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(AppTab.COUNTER) }

    val activeShareMessage by viewModel.activeShareMessage.collectAsState()
    val activeSharePhone by viewModel.activeSharePhone.collectAsState()

    // Handle back button on secondary screens
    if (selectedTab != AppTab.COUNTER) {
        BackHandler {
            selectedTab = AppTab.COUNTER
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                tonalElevation = 6.dp,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = selectedTab == AppTab.COUNTER,
                    onClick = { selectedTab = AppTab.COUNTER },
                    icon = { Icon(imageVector = Icons.Default.Calculate, contentDescription = "Counter") },
                    label = { Text("नोट काउंटर", fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("nav_counter")
                )

                NavigationBarItem(
                    selected = selectedTab == AppTab.DAILY_HISAB,
                    onClick = { selectedTab = AppTab.DAILY_HISAB },
                    icon = { Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "Daily Hisab") },
                    label = { Text("दैनिक हिसाब", fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("nav_daily_hisab")
                )

                NavigationBarItem(
                    selected = selectedTab == AppTab.AGENTS,
                    onClick = { selectedTab = AppTab.AGENTS },
                    icon = { Icon(imageVector = Icons.Default.People, contentDescription = "Agents") },
                    label = { Text("एजेंट्स", fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("nav_agents")
                )

                NavigationBarItem(
                    selected = selectedTab == AppTab.SHOP_PROFILE,
                    onClick = { selectedTab = AppTab.SHOP_PROFILE },
                    icon = { Icon(imageVector = Icons.Default.Store, contentDescription = "Shop Profile") },
                    label = { Text("दुकान", fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("nav_shop")
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        when (selectedTab) {
            AppTab.COUNTER -> {
                CounterScreen(
                    viewModel = viewModel,
                    onNavigateToShopSettings = { selectedTab = AppTab.SHOP_PROFILE },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            AppTab.DAILY_HISAB -> {
                DailyHisabScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            AppTab.AGENTS -> {
                AgentsScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            AppTab.SHOP_PROFILE -> {
                ShopSettingsScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }

    // WhatsApp Share Dialog
    activeShareMessage?.let { message ->
        WhatsAppShareDialog(
            messageText = message,
            initialPhoneNumber = activeSharePhone,
            onDismiss = { viewModel.dismissShareDialog() }
        )
    }
}
