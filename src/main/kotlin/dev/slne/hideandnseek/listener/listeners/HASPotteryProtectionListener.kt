package dev.slne.hideandnseek.listener.listeners

import dev.slne.hideandnseek.util.cancel
import org.bukkit.block.DecoratedPot
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent

object HASPotteryProtectionListener : Listener {
    @EventHandler
    fun onProjectileHit(event: ProjectileHitEvent) {
        if (event.hitBlock?.blockData is DecoratedPot) {
            event.cancel()
        }
    }
}