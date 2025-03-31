package dev.slne.hideandnseek.listener.listeners

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.ticks
import dev.slne.hideandnseek.HASManager
import dev.slne.hideandnseek.game.role.HASRole
import dev.slne.hideandnseek.plugin
import dev.slne.hideandnseek.util.HAS
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object HASConnectionListener: Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val hasPlayer = player.HAS

        event.joinMessage(buildText {
            append(hasPlayer.displayName())
            spacer(" hat das Spiel betreten.")
        })

        plugin.launch {
            hasPlayer.reset()
            val runningGame = HASManager.currentGame
            if (runningGame != null && !runningGame.canPlayersJoin) {
                player.gameMode = GameMode.SPECTATOR
                player.sendText {
                    appendPrefix()
                    append {
                        error("Du hast das Spiel betreten, während es bereits läuft.")
                        error(" Du bist nun ein Zuschauer.")
                        error(" Bitte versetze dich NICHT manuell in einen anderen Spielmodus.")
                        decorate(TextDecoration.BOLD)
                    }
                }
            }

            delay(3.ticks)
            withContext(plugin.entityDispatcher(player)) {
                player.inventory.clear()
            }
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        val hasPlayer = player.HAS

        event.quitMessage(buildText {
            append(hasPlayer.displayName())
            spacer(" hat das Spiel verlassen.")
        })

        val runningGame = HASManager.currentGame ?: return

        plugin.launch {
            try {
                withContext(plugin.entityDispatcher(player)) {
                    with(player) {
                        isFlying = false
                        allowFlight = false
                    }
                }

                hasPlayer.setRole(HASRole.Undefined)
            } finally {
                runningGame.performPlayerCheck()
            }
        }
    }
}