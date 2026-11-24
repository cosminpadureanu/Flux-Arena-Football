package com.fluxarena.football

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fluxarena.football.ui.AdminScreen
import com.fluxarena.football.ui.DoodleScreen
import com.fluxarena.football.viewmodel.FluxViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val vm: FluxViewModel = viewModel()
                var showAdmin by remember { mutableStateOf(false) }

                if (showAdmin) {
                    AdminScreen(
                        players = vm.players,
                        onCreatePoll = { title, dateTime, maxPlayers ->
                            vm.createPoll(title, dateTime, maxPlayers)
                        },
                        onToggleBlacklist = { id, blacklist, reason ->
                            vm.toggleBlacklist(id, blacklist, reason)
                        },
                        onLogStat = { id, statType ->
                            vm.logPlayerStat(id, statType)
                        },
                        onBack = { showAdmin = false }
                    )
                } else {
                    DoodleScreen(
                        players = vm.players,
                        currentPlayer = vm.currentPlayer,
                        activePoll = vm.activePoll,
                        onSelectPlayer = { vm.selectPlayer(it) },
                        onVote = { vm.submitVote(it) },
                        onNavigateToAdmin = { showAdmin = true },
                        isAdmin = true
                    )
                }
            }
        }
    }
}
