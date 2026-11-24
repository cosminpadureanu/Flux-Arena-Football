package com.fluxarena.football.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.fluxarena.football.model.MatchPoll
import com.fluxarena.football.model.VoteStatus

object WhatsAppHelper {

    fun shareDoodleToWhatsApp(context: Context, poll: MatchPoll, appDeepLink: String) {
        val inPlayers = poll.votes.filterValues { it == VoteStatus.IN.name }
            .keys.mapNotNull { poll.voterNames[it] }
        val maybePlayers = poll.votes.filterValues { it == VoteStatus.MAYBE.name }
            .keys.mapNotNull { poll.voterNames[it] }

        val message = buildString {
            appendLine("⚽ *${poll.title.uppercase()}* ⚽")
            appendLine("📅 *When:* ${poll.dateTime}")
            appendLine("📍 *Where:* ${poll.pitchLocation}")
            appendLine("👥 *Spots:* ${inPlayers.size}/${poll.maxPlayers}")
            appendLine()
            appendLine("✅ *IN (${inPlayers.size}):*")
            if (inPlayers.isEmpty()) {
                appendLine("_No confirmations yet_")
            } else {
                inPlayers.forEachIndexed { index, name ->
                    appendLine("${index + 1}. $name")
                }
            }

            if (maybePlayers.isNotEmpty()) {
                appendLine()
                appendLine("❓ *MAYBE (${maybePlayers.size}):*")
                maybePlayers.forEach { name -> appendLine("- $name") }
            }

            appendLine()
            appendLine("👉 *Vote in the Flux Arena App:*")
            appendLine(appDeepLink)
        }

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            setPackage("com.whatsapp")
            putExtra(Intent.EXTRA_TEXT, message)
        }

        try {
            context.startActivity(sendIntent)
        } catch (_: Exception) {
            Toast.makeText(context, "WhatsApp is not installed on this device", Toast.LENGTH_SHORT).show()
        }
    }
}
