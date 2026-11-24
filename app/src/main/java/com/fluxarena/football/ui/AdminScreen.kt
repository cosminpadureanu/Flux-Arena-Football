package com.fluxarena.football.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fluxarena.football.model.Player

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    players: List<Player>,
    onCreatePoll: (title: String, dateTime: String, maxPlayers: Int) -> Unit,
    onToggleBlacklist: (playerId: String, blacklist: Boolean, reason: String) -> Unit,
    onLogStat: (playerId: String, statType: String) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("New Doodle", "Reports & Stats", "Blacklist")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flux Arena Admin") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> CreateDoodleTab(onCreatePoll)
                1 -> PlayerReportsTab(players, onLogStat)
                2 -> BlacklistTab(players, onToggleBlacklist)
            }
        }
    }
}

@Composable
fun CreateDoodleTab(onCreatePoll: (String, String, Int) -> Unit) {
    var title by remember { mutableStateOf("Flux Arena 6v6") }
    var dateTime by remember { mutableStateOf("Thursday, 20:00") }
    var maxPlayers by remember { mutableStateOf("12") }
    var createdNotice by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it; createdNotice = false },
            label = { Text("Match Title") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = dateTime,
            onValueChange = { dateTime = it; createdNotice = false },
            label = { Text("Date & Time") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = maxPlayers,
            onValueChange = { maxPlayers = it; createdNotice = false },
            label = { Text("Max Players") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                onCreatePoll(title, dateTime, maxPlayers.toIntOrNull() ?: 12)
                createdNotice = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Publish New Doodle")
        }

        if (createdNotice) {
            Text(
                "✅ New Doodle published! Tap Back to view or send to WhatsApp.",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PlayerReportsTab(players: List<Player>, onLogStat: (String, String) -> Unit) {
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(players) { player ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        player.name,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("⚽ Played: ${player.totalMatchesPlayed}")
                        Text("❌ Cancels: ${player.frequentCancels}", color = Color.Red)
                        Text("⏰ Late: ${player.lateArrivals}", color = Color(0xFFE65100))
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { onLogStat(player.id, "APPEARANCE") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+Played", style = MaterialTheme.typography.labelSmall)
                        }
                        OutlinedButton(
                            onClick = { onLogStat(player.id, "CANCEL") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+Cancel", style = MaterialTheme.typography.labelSmall)
                        }
                        OutlinedButton(
                            onClick = { onLogStat(player.id, "LATE") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+Late", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BlacklistTab(
    players: List<Player>,
    onToggleBlacklist: (playerId: String, blacklist: Boolean, reason: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(players) { player ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(player.name, fontWeight = FontWeight.Bold)
                        if (player.isBlacklisted) {
                            Text(
                                "Reason: ${player.blacklistReason}",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Button(
                        onClick = {
                            onToggleBlacklist(
                                player.id,
                                !player.isBlacklisted,
                                if (!player.isBlacklisted) "Repeated late cancels / no-show" else ""
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (player.isBlacklisted) Color.Gray else MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(if (player.isBlacklisted) "Unban" else "Blacklist")
                    }
                }
            }
        }
    }
}
