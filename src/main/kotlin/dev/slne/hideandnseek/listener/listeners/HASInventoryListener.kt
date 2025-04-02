package dev.slne.hideandnseek.listener.listeners

import dev.slne.hideandnseek.HASManager
import dev.slne.hideandnseek.util.cancel
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent

object HASInventoryListener : Listener {

    @EventHandler
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        if (!HASManager.isBypassing(event.player)) {
            event.cancel()
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (!HASManager.isBypassing(event.whoClicked as? Player ?: return)) {
            event.cancel()
        }
    }

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        if (!HASManager.isBypassing(event.whoClicked as? Player ?: return)) {
            event.cancel()
        }
    }

    @EventHandler
    fun onPlayerSwapHandItems(event: PlayerSwapHandItemsEvent) {
        if (!HASManager.isBypassing(event.player)) {
            event.cancel()
        }
    }
}