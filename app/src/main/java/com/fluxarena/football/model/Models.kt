package com.fluxarena.football.model

enum class VoteStatus {
    IN, OUT, MAYBE
}

data class Player(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val isBlacklisted: Boolean = false,
    val blacklistReason: String = "",
    val totalMatchesPlayed: Int = 0,
    val frequentCancels: Int = 0,
    val lateArrivals: Int = 0
)

data class MatchPoll(
    val id: String = "",
    val title: String = "Flux Arena - Weekly Match",
    val dateTime: String = "",
    val pitchLocation: String = "Flux Arena Pitch 1",
    val maxPlayers: Int = 12,
    val isClosed: Boolean = false,
    val votes: Map<String, String> = emptyMap(),
    val voterNames: Map<String, String> = emptyMap()
)
