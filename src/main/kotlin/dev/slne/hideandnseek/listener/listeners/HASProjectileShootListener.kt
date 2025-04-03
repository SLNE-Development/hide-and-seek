package dev.slne.hideandnseek.listener.listeners

import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent

object HASProjectileShootListener : Listener {
    @EventHandler
    fun onEntityShootBow(event: EntityShootBowEvent) {
        if (event.entity is Player) {
            val arrow = event.projectile as? Arrow ?: return
            arrow.lifetimeTicks = 0
        }
    }
}