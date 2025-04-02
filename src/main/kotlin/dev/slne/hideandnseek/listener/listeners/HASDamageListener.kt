package dev.slne.hideandnseek.listener.listeners

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.hideandnseek.HASManager
import dev.slne.hideandnseek.game.HASGameRules
import dev.slne.hideandnseek.game.phase.phases.PreparationPhase
import dev.slne.hideandnseek.plugin
import dev.slne.hideandnseek.util.HAS
import dev.slne.hideandnseek.util.cancel
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.ProjectileHitEvent

object HASDamageListener : Listener {
    @EventHandler
    fun onProjectileHit(event: ProjectileHitEvent) {
        val shooter = event.entity.shooter as? Player ?: return
        val target = event.hitEntity as? Player ?: return
        val game = HASManager.currentGame ?: return event.cancel()

        if (game.canPlayersJoin) return event.cancel()

        val hasShooter = shooter.HAS
        val hasTarget = target.HAS

        if (hasShooter.role == hasTarget.role) return event.cancel()

        if (hasShooter.seeker && game.rules.getBoolean(HASGameRules.RULE_IS_ONE_HIT_KNOCK_OUT)) {
            plugin.launch(plugin.entityDispatcher(target)) {
                target.damage(Float.MAX_VALUE.toDouble(), shooter)
            }
        }
    }

    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        val target = event.entity as? Player ?: return
        val game = HASManager.currentGame ?: return event.cancel()

        if (!game.playersDamageable) return event.cancel()

        val damager = event.damager as? Player ?: return
        val hasDamager = damager.HAS
        val hasTarget = target.HAS

        if (hasDamager.hider || hasDamager.role == hasTarget.role) return event.cancel()

        if (hasDamager.seeker && game.rules.getBoolean(HASGameRules.RULE_IS_ONE_HIT_KNOCK_OUT)) {
            plugin.launch(plugin.entityDispatcher(target)) {
                target.damage(Float.MAX_VALUE.toDouble(), damager)
            }
        }
    }

    @EventHandler
    fun onPrePlayerAttackEntity(event: PrePlayerAttackEntityEvent) {
        val game = HASManager.currentGame ?: return event.cancel()
        if (game.canPlayersJoin) return event.cancel()

        val hasPlayer = event.player.HAS
        if (!hasPlayer.seeker && !hasPlayer.hider) return event.cancel()
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        event.entity as? Player ?: return
        val game = HASManager.currentGame ?: return event.cancel()

        if (game.phase is PreparationPhase) {
            event.cancel()
        }
    }
}