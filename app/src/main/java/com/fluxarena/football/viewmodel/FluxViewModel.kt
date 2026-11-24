package com.fluxarena.football.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.fluxarena.football.model.MatchPoll
import com.fluxarena.football.model.Player
import com.fluxarena.football.model.VoteStatus

class FluxViewModel : ViewModel() {

    var players by mutableStateOf(
        listOf(
            Player(id = "p1", name = "Alex Popescu", totalMatchesPlayed = 14, frequentCancels = 1, lateArrivals = 0),
            Player(id = "p2", name = "Mihai Ionescu", totalMatchesPlayed = 9, frequentCancels = 4, lateArrivals = 3),
            Player(id = "p3", name = "Radu Marin", totalMatchesPlayed = 11, frequentCancels = 0, lateArrivals = 2),
            Player(id = "p4", name = " Andrei Dumitru", totalMatchesPlayed = 6, frequentCancels = 5, lateArrivals = 4, isBlacklisted = true, blacklistReason = "Repeated late cancellations")
        )
    )
        private set

    var currentPlayerId by mutableStateOf("p1")
        private set

    var activePoll by mutableStateOf(
        MatchPoll(
            id = "match_01",
            title = "Flux Arena 6v6",
            dateTime = "Thursday, 20:00",
            pitchLocation = "Flux Arena Pitch 1",
            maxPlayers = 12,
            votes = mapOf("p1" to VoteStatus.IN.name, "p3" to VoteStatus.MAYBE.name),
            voterNames = mapOf("p1" to "Alex Popescu", "p3" to "Radu Marin")
        )
    )
        private set

    val currentPlayer: Player
        get() = players.find { it.id == currentPlayerId } ?: players.first()

    fun selectPlayer(playerId: String) {
        currentPlayerId = playerId
    }

    fun submitVote(newStatus: VoteStatus) {
        val player = currentPlayer
        if (player.isBlacklisted) return

        val previousVote = activePoll.votes[player.id]
        val updatedVotes = activePoll.votes.toMutableMap().apply {
            put(player.id, newStatus.name)
        }
        val updatedNames = activePoll.voterNames.toMutableMap().apply {
            put(player.id, player.name)
        }

        activePoll = activePoll.copy(
            votes = updatedVotes,
            voterNames = updatedNames
        )

        if (previousVote == VoteStatus.IN.name && newStatus == VoteStatus.OUT) {
            logPlayerStat(player.id, "CANCEL")
        }
    }

    fun createPoll(title: String, dateTime: String, maxPlayers: Int) {
        activePoll = MatchPoll(
            id = "match_${System.currentTimeMillis()}",
            title = title.ifBlank { "Flux Arena Match" },
            dateTime = dateTime.ifBlank { "TBD" },
            pitchLocation = "Flux Arena Pitch 1",
            maxPlayers = maxPlayers.coerceAtLeast(2),
            votes = emptyMap(),
            voterNames = emptyMap()
        )
    }

    fun toggleBlacklist(playerId: String, blacklist: Boolean, reason: String) {
        players = players.map { p ->
            if (p.id == playerId) {
                p.copy(
                    isBlacklisted = blacklist,
                    blacklistReason = if (blacklist) reason else ""
                )
            } else {
                p
            }
        }
    }

    fun logPlayerStat(playerId: String, statType: String) {
        players = players.map { p ->
            if (p.id == playerId) {
                when (statType) {
                    "APPEARANCE" -> p.copy(totalMatchesPlayed = p.totalMatchesPlayed + 1)
                    "CANCEL" -> p.copy(frequentCancels = p.frequentCancels + 1)
                    "LATE" -> p.copy(lateArrivals = p.lateArrivals + 1)
                    else -> p
                }
            } else {
                p
            }
        }
    }
}
