package dev.slne.hideandnseek.listener.listeners

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.hideandnseek.HASManager
import dev.slne.hideandnseek.game.role.HASSpectatorRole
import dev.slne.hideandnseek.plugin
import dev.slne.hideandnseek.util.HAS
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent

object HASRespawnListener : Listener {

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        val game = HASManager.currentGame
        if (game != null && !game.canPlayersJoin) {
            event.respawnLocation = game.area.settings.spawnLocation
        } else {
            event.respawnLocation = plugin.data.settings.lobbyLocation
        }
    }

    @EventHandler
    fun onPlayerPostRespawn(event: PlayerPostRespawnEvent) {
        val player = event.player
        val hasPlayer = player.HAS

        plugin.launch {
            hasPlayer.role.giveInventory(player)

            if (hasPlayer.seeker) return@launch
            val game = HASManager.currentGame ?: return@launch

            if (!game.canPlayersJoin) {
                hasPlayer.setRole(HASSpectatorRole)
            }
        }
    }
}