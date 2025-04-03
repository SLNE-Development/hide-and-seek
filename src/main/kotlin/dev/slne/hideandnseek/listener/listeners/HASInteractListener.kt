package dev.slne.hideandnseek.listener.listeners

import dev.slne.hideandnseek.HASManager
import dev.slne.hideandnseek.util.cancel
import org.bukkit.Material
import org.bukkit.block.data.type.DecoratedPot
import org.bukkit.block.data.type.Door
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

object HASInteractListener : Listener {
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val block = event.clickedBlock ?: return
        val player = event.player

        if (block.blockData is Door) return

        if (player.inventory.itemInMainHand.type == Material.BOW) {
            if (block.blockData is DecoratedPot && !HASManager.isBypassing(player)) {
                event.cancel()
            }

            return
        }

        if (!HASManager.isBypassing(player)) {
            event.cancel()
        }
    }
}