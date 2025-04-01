package dev.slne.hideandnseek.game.phase.phases

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import dev.slne.hideandnseek.game.HASGame
import dev.slne.hideandnseek.game.HASGameRules
import dev.slne.hideandnseek.game.phase.GamePhase
import dev.slne.hideandnseek.old.util.TimeUtil
import dev.slne.hideandnseek.plugin
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.longSetOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.bukkit.GameRule
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

class LobbyPhase(val game: HASGame) : GamePhase {

    override suspend fun prepare() {
        game.teleportToLobby()

        withContext(plugin.globalRegionDispatcher) {
            val areaSettings = game.area.settings
            val world = areaSettings.world

            world.worldBorder.apply {
                this.center = areaSettings.spawnLocation
                this.size = areaSettings.startRadius * 2.0
                this.damageAmount = game.rules.getDouble(HASGameRules.RULE_BORDER_DAMAGE)
                this.damageBuffer = game.rules.getDouble(HASGameRules.RULE_BORDER_BUFFER)
                this.warningDistance = 0
                this.warningTime = 5
            }

            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true)
        }
    }

    override suspend fun start() {
        for (time in game.rules.getDuration(HASGameRules.RULE_LOBBY_TIME).inWholeSeconds downTo 1) {
            if (!game.active) break
            server.sendActionBar(
                TimeUtil.formatTimestamp(
                    TimeUnit.SECONDS,
                    time,
                    Colors.VARIABLE_VALUE
                )
            )

            if (time in announcementTimes) {
                server.sendText {
                    appendPrefix()
                    info("Das Spiel beginnt in ")
                    append(
                        TimeUtil.formatLongTimestamp(
                            TimeUnit.SECONDS,
                            time,
                            Colors.VARIABLE_VALUE
                        )
                    )
                    info("...")
                }
            }

            delay(1.seconds)
        }
    }

    companion object {
        private val announcementTimes = longSetOf(60, 30, 15, 10, 5, 4, 3, 2, 1)
    }
}