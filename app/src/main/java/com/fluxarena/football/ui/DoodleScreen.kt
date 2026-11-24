package com.fluxarena.football.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fluxarena.football.model.MatchPoll
import com.fluxarena.football.model.Player
import com.fluxarena.football.model.VoteStatus
import com.fluxarena.football.util.WhatsAppHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoodleScreen(
    players: List<Player>,
    currentPlayer: Player,
    activePoll: MatchPoll,
    onSelectPlayer: (String) -> Unit,
    onVote: (VoteStatus) -> Unit,
    onNavigateToAdmin: () -> Unit,
    isAdmin: Boolean
) {
    val context = LocalContext.current
    val currentVote = activePoll.votes[currentPlayer.id]
    var playerMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flux Arena Football") },
                actions = {
                    if (isAdmin) {
                        TextButton(onClick = onNavigateToAdmin) {
                            Text("ADMIN PANEL", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    WhatsAppHelper.shareDoodleToWhatsApp(
                        context = context,
                        poll = activePoll,
                        appDeepLink = "https://fluxarena.app/poll/${activePoll.id}"
                    )
                },
                icon = { Icon(Icons.Default.Share, contentDescription = "Share") },
                text = { Text("Send to WhatsApp") },
                containerColor = Color(0xFF25D366),
                contentColor = Color.White
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Quick player switcher for testing on a single device
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Voting as:", style = MaterialTheme.typography.labelLarge)
                Box {
                    OutlinedButton(onClick = { playerMenuExpanded = true }) {
                        Text(currentPlayer.name)
                    }
                    DropdownMenu(
                        expanded = playerMenuExpanded,
                        onDismissRequest = { playerMenuExpanded = false }
                    ) {
                        players.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p.name) },
                                onClick = {
                                    onSelectPlayer(p.id)
                                    playerMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            if (currentPlayer.isBlacklisted) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = "Account Suspended: ${currentPlayer.blacklistReason}. Contact Admin.",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
                return@Column
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        activePoll.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("📅 ${activePoll.dateTime}")
                    Text("📍 ${activePoll.pitchLocation}")
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("Your Availability:", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VoteStatus.entries.forEach { status ->
                    val isSelected = currentVote == status.name
                    Button(
                        onClick = { onVote(status) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(status.name)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            val inList = activePoll.votes.filterValues { it == VoteStatus.IN.name }.keys.toList()
            Text(
                "Confirmed Squad (${inList.size}/${activePoll.maxPlayers})",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(inList) { playerId ->
                    val name = activePoll.voterNames[playerId] ?: "Unknown Player"
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("✅ $name", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}
