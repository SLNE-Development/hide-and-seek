package dev.slne.hideandnseek.listener.listeners

import dev.slne.hideandnseek.HASManager
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

object HASPlayerMoveListener : Listener {

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val game = HASManager.currentGame ?: return
        if (!game.playersDamageable) return

        val to = event.to
        if (to.block.type != Material.WATER) return

        val player = event.player
        player.velocity = player.location.direction.multiply(-1).setY(0.5)
        player.damage(5.0)
    }
}