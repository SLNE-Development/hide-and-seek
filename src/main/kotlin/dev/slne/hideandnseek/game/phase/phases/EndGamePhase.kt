package dev.slne.hideandnseek.game.phase.phases

import dev.slne.hideandnseek.HASPermissions
import dev.slne.hideandnseek.game.HASEndReason
import dev.slne.hideandnseek.game.HASGame
import dev.slne.hideandnseek.game.HASGameRules
import dev.slne.hideandnseek.game.phase.GamePhase
import dev.slne.hideandnseek.old.util.TimeUtil
import dev.slne.hideandnseek.util.HAS
import dev.slne.hideandnseek.util.tp
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayerInRegion
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

class EndGamePhase(val game: HASGame) : GamePhase {

    override suspend fun start() {
        game.stopGame(HASEndReason.TIME_UP)
    }

    override suspend fun end(reason: HASEndReason) {
        for (currentSecond in game.rules.getDuration(HASGameRules.RULE_CELEBRATION_TIME_SECONDS).inWholeSeconds downTo 0) {
            server.sendActionBar(
                TimeUtil.formatTimestamp(
                    TimeUnit.SECONDS,
                    currentSecond,
                    Colors.VARIABLE_VALUE
                )
            )
            delay(1.seconds)
        }

        forEachPlayerInRegion({ player ->
            try {
                player.tp(game.settings.lobbyLocation)
//                HASPlayer[uniqueId].prepare()
                player.HAS.reset()
            } finally {
                if (!player.hasPermission(HASPermissions.BYPASS_END_KICK)) {
                    player.kick(buildText {
                        error("Das Spiel wurde beendet.")
                    })
                }
            }

        }, concurrent = true)
    }
}