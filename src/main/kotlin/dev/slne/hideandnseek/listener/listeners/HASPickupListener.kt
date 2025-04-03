package dev.slne.hideandnseek.listener.listeners

import dev.slne.hideandnseek.HASManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent

object HASPickupListener : Listener {
    @EventHandler
    fun onEntityPickupItem(event: EntityPickupItemEvent) {
        val player = event.entity as? Player ?: return
        if (HASManager.isBypassing(player)) {
            return
        }
        event.isCancelled = true
    }
}