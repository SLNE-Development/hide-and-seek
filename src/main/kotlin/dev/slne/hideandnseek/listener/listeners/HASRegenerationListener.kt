package dev.slne.hideandnseek.listener.listeners

import dev.slne.hideandnseek.util.cancel
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason

object HASRegenerationListener : Listener {
    @EventHandler
    fun onEntityRegainHealth(event: EntityRegainHealthEvent) {
        event.entity as? Player ?: return
        when (event.regainReason) {
            RegainReason.MAGIC, RegainReason.MAGIC_REGEN -> {}
            else -> event.cancel()
        }
    }
}