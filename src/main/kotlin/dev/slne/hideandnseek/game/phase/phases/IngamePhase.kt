package dev.slne.hideandnseek.game.phase.phases

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import dev.slne.hideandnseek.game.HASEndReason
import dev.slne.hideandnseek.game.HASGame
import dev.slne.hideandnseek.game.HASGameRules
import dev.slne.hideandnseek.game.phase.GamePhase
import dev.slne.hideandnseek.old.util.TimeUtil
import dev.slne.hideandnseek.plugin
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayerInRegion
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.Sound
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.longSetOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.kyori.adventure.sound.Sound
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds
import org.bukkit.Sound as BukkitSound

class IngamePhase(val game: HASGame) : GamePhase {

    private var previousWorldBoarderSize: Double = -1.0
    override val canPlayersJoin = false
    override val isPlayerDamageable = true

    override suspend fun start() {
        withContext(plugin.globalRegionDispatcher) {
            server.sendText {
                appendPrefix()
                appendNewPrefixedLine()
                spacer("-".repeat(20))
                appendNewPrefixedLine()
                appendNewPrefixedLine()
                info("Die Vorbereitungszeit ist abgelaufen!")
                appendNewPrefixedLine()
                appendNewPrefixedLine()
                spacer("-".repeat(20))
                appendNewPrefixedLine()
            }

            val worldBorder = game.settings.world.worldBorder
            previousWorldBoarderSize = worldBorder.size
            worldBorder.setSize(
                game.rules.getInteger(HASGameRules.RULE_GAME_END_RADIUS) * 2.0,
                game.rules.getDuration(HASGameRules.RULE_GAME_TIME).inWholeSeconds
            )
        }

        forEachPlayerInRegion({
            it.playSound(Sound {
                type(BukkitSound.ENTITY_ENDER_DRAGON_GROWL)
                volume(.75f)
                pitch(.75f)
                source(Sound.Source.HOSTILE)
            }, Sound.Emitter.self())
        }, concurrent = true)

        game.seekers.forEach { it.teleportToSpawn() }

        for (currentSeconds in game.rules.getDuration(HASGameRules.RULE_GAME_TIME).inWholeSeconds downTo 1) {
            if (currentSeconds in remainingTimeAnnouncements) {
                server.sendText {
                    appendPrefix()
                    info("Das Spiel endet in ")
                    append(
                        TimeUtil.formatLongTimestamp(
                            TimeUnit.SECONDS,
                            currentSeconds,
                            Colors.VARIABLE_VALUE
                        )
                    )
                    info(" Sekunden.")
                }
            }

            server.sendActionBar(
                TimeUtil.formatTimestamp(
                    TimeUnit.SECONDS,
                    currentSeconds,
                    Colors.VARIABLE_VALUE
                )
            )

            delay(1.seconds)
        }
    }

    override suspend fun end(reason: HASEndReason) {
        withContext(plugin.globalRegionDispatcher) {
            server.sendText {
                appendNewPrefixedLine()
                appendNewPrefixedLine()
                spacer("-".repeat(20))
                appendNewPrefixedLine()
                appendNewPrefixedLine()
                info("Das Spiel ist vorbei!")
                appendNewPrefixedLine()
                appendNewPrefixedLine()

                when (reason) {
                    HASEndReason.TIME_UP, HASEndReason.HIDERS_WIN -> {
                        variableValue("Die Verstecker haben gewonnen.")
                    }

                    HASEndReason.SEEKERS_WIN -> {
                        variableValue("Die Sucher haben gewonnen.")
                    }
                }

                appendNewPrefixedLine()
                appendNewPrefixedLine()
                spacer("-".repeat(20))
                appendNewPrefixedLine()
            }
        }

        forEachPlayerInRegion({
            it.playSound(Sound {
                type(BukkitSound.ENTITY_ENDER_DRAGON_DEATH)
                volume(0.75f)
                pitch(0.75f)
                source(Sound.Source.HOSTILE)
            }, Sound.Emitter.self())
        }, concurrent = true)

        withContext(plugin.globalRegionDispatcher) {
            val worldBorder = game.settings.world.worldBorder
            worldBorder.size = worldBorder.size
        }
    }

    override suspend fun reset() {
        withContext(plugin.globalRegionDispatcher) {
            val worldBorder = game.settings.world.worldBorder
            worldBorder.size = previousWorldBoarderSize
        }
    }

    companion object {
        private var remainingTimeAnnouncements = longSetOf(
            3600, 1800, 900, 600, 300, 240,
            180, 120, 60, 30, 15, 10, 5, 4, 3, 2, 1
        )
    }
}