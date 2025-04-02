package dev.slne.hideandnseek.listener.listeners

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.hideandnseek.HASManager
import dev.slne.hideandnseek.HASPermissions
import dev.slne.hideandnseek.game.HASGameRules
import dev.slne.hideandnseek.game.role.HASSeekerRole
import dev.slne.hideandnseek.game.role.HASSpectatorRole
import dev.slne.hideandnseek.plugin
import dev.slne.hideandnseek.util.HAS
import dev.slne.hideandnseek.util.cancel
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import kotlinx.coroutines.withContext
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

object HASDeathListener : Listener {
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity
        val hasPlayer = player.HAS
        val game = HASManager.currentGame ?: return event.cancel()
        if (game.canPlayersJoin) return event.cancel()

        event.drops.clear()
        event.deathMessage(buildText {
            appendPrefix()

            val killer = player.killer?.HAS
            if (killer != null) {
                append(hasPlayer.displayName())
                info(" wurde von ")
                append(killer.displayName())
                info(" getötet!")
            } else {
                append(hasPlayer.displayName())
                info(" ist gestorben!")
            }

            if (hasPlayer.hider && !game.canPlayersJoin) {
                val hidersRemaining = game.hiders.size - 1
                if (hidersRemaining > 0) {
                    appendSpace()
                    info("Es sind noch ")
                    variableValue(hidersRemaining)
                    info(" Verstecker übrig!")
                }
            }
        })

        plugin.launch {
            try {
                if (hasPlayer.hider) {
                    if (game.rules.getBoolean(HASGameRules.RULE_DO_HIDERS_BECOME_SEEKERS)) {
                        hasPlayer.setRole(HASSeekerRole)
                    } else {
                        try {
                            hasPlayer.setRole(HASSpectatorRole)
                        } finally {
                            withContext(plugin.entityDispatcher(player)) {
                                if (!player.hasPermission(HASPermissions.BYPASS_ELIMINATION_KICK)) {
                                    player.kick(buildText { error("Du bist ausgeschieden!") })
                                }
                            }
                        }
                    }
                }
            } finally {
                game.performPlayerCheck()
            }
        }
    }
}