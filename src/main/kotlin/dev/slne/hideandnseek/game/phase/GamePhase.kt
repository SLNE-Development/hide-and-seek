package dev.slne.hideandnseek.game.phase

import dev.slne.hideandnseek.game.HASEndReason
import dev.slne.hideandnseek.old.util.TimeUtil
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayerInRegion
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.Sound
import kotlinx.coroutines.coroutineScope
import net.kyori.adventure.sound.Sound
import java.util.concurrent.TimeUnit
import org.bukkit.Sound as BukkitSound

interface GamePhase {
    val canPlayersJoin: Boolean get() = true
    val isPlayerDamageable: Boolean get() = false

    suspend fun prepare() {}
    suspend fun start() {}
    suspend fun end(reason: HASEndReason) {}
    suspend fun reset() {}

    suspend fun sendActionbarTimerAndPlaySound(currentSecond: Long) = coroutineScope {
        forEachPlayerInRegion({ player ->
            if (player.protocolVersion == 770) {
                player.sendActionBar(
                    TimeUtil.formatTimestamp(
                        TimeUnit.SECONDS,
                        currentSecond,
                        Colors.VARIABLE_VALUE
                    )
                )
            }

            player.playSound(Sound {
                type(BukkitSound.BLOCK_NOTE_BLOCK_PLING)
                pitch(2f)
                volume(.5f)
                source(Sound.Source.BLOCK)
            }, Sound.Emitter.self())
        }, concurrent = true)
    }
}