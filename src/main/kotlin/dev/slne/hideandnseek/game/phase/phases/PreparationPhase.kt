package dev.slne.hideandnseek.game.phase.phases

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import dev.slne.hideandnseek.game.HASGame
import dev.slne.hideandnseek.game.HASGameRules
import dev.slne.hideandnseek.game.phase.GamePhase
import dev.slne.hideandnseek.game.role.HASHiderRole
import dev.slne.hideandnseek.game.role.HASSeekerRole
import dev.slne.hideandnseek.game.role.HASUndefinedRole
import dev.slne.hideandnseek.old.util.TimeUtil
import dev.slne.hideandnseek.player.HASPlayer
import dev.slne.hideandnseek.plugin
import dev.slne.hideandnseek.util.HAS
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayerInRegion
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.Sound
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.longListOf
import dev.slne.surf.surfapi.core.api.util.random
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.*
import net.kyori.adventure.sound.Sound
import org.bukkit.Bukkit
import java.util.concurrent.TimeUnit
import kotlin.random.asKotlinRandom
import kotlin.time.Duration.Companion.seconds
import org.bukkit.Sound as BukkitSound

class PreparationPhase(val game: HASGame) : GamePhase {

    private var previousMaxPlayers = -1
    private var previousWorldBorderSize = -1.0
    override val isPlayerDamageable = true

    override suspend fun start() = coroutineScope {
        withContext(plugin.globalRegionDispatcher) {
            previousMaxPlayers = server.maxPlayers
            server.maxPlayers = 0

            // TODO: 31.03.2025 16:46 - change difficulty to peaceful

            val worldBorder = game.area.settings.world.worldBorder
            previousWorldBorderSize = worldBorder.size
            worldBorder.size = game.area.settings.startRadius * 2.0
        }

        printStartMessage()
        playStartSound()

        val seekers = chooseSeekers(game.rules.getInteger(HASGameRules.RULE_SEEKER_AMOUNT))
        seekers.map { async { it.setRole(HASSeekerRole) } }.awaitAll()

        forEachPlayerInRegion({
            val player = HASPlayer[it.uniqueId]

            if (!player.seeker) {
                player.setRole(HASHiderRole)
            }

            player.prepare()
        }, concurrent = true)

        for (currentSecond in game.rules.getDuration(HASGameRules.RULE_PREPARATION_TIME).inWholeSeconds downTo 1) {
            if (!game.active) break
            if (currentSecond in announcements) {
                server.sendText {
                    appendPrefix()
                    info("Die Versteckenden haben noch ")
                    append(
                        TimeUtil.formatLongTimestamp(
                            TimeUnit.SECONDS,
                            currentSecond,
                            Colors.VARIABLE_VALUE
                        )
                    )
                    info(" bis zum Spielstart!")
                }
            }
            server.sendActionBar(
                TimeUtil.formatTimestamp(
                    TimeUnit.SECONDS,
                    currentSecond,
                    Colors.VARIABLE_VALUE,
                )
            )

            delay(1.seconds)
        }
    }

    private suspend fun chooseSeekers(seekerAmount: Int): ObjectSet<HASPlayer> {
        val predefinedSeekers = game.settings.initialSeekers?.filter { it.online }
        if (!predefinedSeekers.isNullOrEmpty()) return predefinedSeekers.toObjectSet()

        val players = withContext(plugin.globalRegionDispatcher) { Bukkit.getOnlinePlayers() }
        return players.asSequence()
            .map { it.HAS }
            .filter { it.role == HASUndefinedRole }
            .shuffled(random.asKotlinRandom())
            .take(seekerAmount)
            .toObjectSet()
    }

    private fun printStartMessage() {
        server.sendText {
            appendPrefix()
            appendNewPrefixedLine()
            spacer("-".repeat(20))
            appendNewPrefixedLine()
            appendNewPrefixedLine()
            info("Das Spiel beginnt!")
            appendNewPrefixedLine()
            appendNewPrefixedLine()
            spacer("-".repeat(20))
            appendNewPrefixedLine()
        }
    }

    private suspend fun playStartSound() {
        forEachPlayerInRegion({
            it.playSound(Sound {
                type(BukkitSound.ENTITY_ENDER_DRAGON_GROWL)
                volume(0.5f)
                pitch(0.75f)
                source(Sound.Source.HOSTILE)
            }, Sound.Emitter.self())
        }, concurrent = true)
    }

    override suspend fun reset() {
        withContext(plugin.globalRegionDispatcher) {
            if (previousMaxPlayers != -1) {
                server.maxPlayers = previousMaxPlayers
            }

            if (previousWorldBorderSize != -1.0) {
                game.area.settings.world.worldBorder.size = previousWorldBorderSize
            }
        }
    }

    companion object {
        private val announcements = longListOf(60, 30, 15, 10, 5, 4, 3, 2, 1)
    }
}