package dev.slne.hideandnseek.listener.listeners

import dev.slne.hideandnseek.HASManager
import dev.slne.hideandnseek.util.cancel
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.hanging.HangingBreakByEntityEvent

object HASEntityDestroyListener : Listener {
    @EventHandler
    fun onHangingBreakByEntity(event: HangingBreakByEntityEvent) {
        if (HASManager.currentGame != null && (event.remover as? Player)?.let { HASManager.isBypassing(it) } == false) {
            event.cancel()
        }
    }
}