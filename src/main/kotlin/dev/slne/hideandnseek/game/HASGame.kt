package dev.slne.hideandnseek.game

import dev.slne.hideandnseek.HASManager
import dev.slne.hideandnseek.game.area.HASGameArea
import dev.slne.hideandnseek.game.phase.GamePhaseManager
import dev.slne.hideandnseek.game.role.HASSeekerRole
import dev.slne.hideandnseek.game.role.HASUndefinedRole
import dev.slne.hideandnseek.plugin
import dev.slne.hideandnseek.util.HAS
import dev.slne.hideandnseek.util.tp
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayerInRegion
import dev.slne.surf.surfapi.core.api.util.random
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import kotlin.random.asKotlinRandom

class HASGame(val area: HASGameArea) {

    val seekers get() = Bukkit.getOnlinePlayers().map { it.HAS }.filter { it.seeker }
    val hiders get() = Bukkit.getOnlinePlayers().map { it.HAS }.filter { it.hider }
    val phase get() = GamePhaseManager.currentPhase

    val canPlayersJoin get() = phase?.canPlayersJoin == true
    val playersDamageable get() = phase?.isPlayerDamageable == true

    private var prepared = false
    var active = false
        private set

    val rules get() = plugin.data.settings.gameRules
    val settings get() = plugin.data.settings

    suspend fun prepareGame() {
        check(!prepared) { "Game is already prepared" }
        check(HASManager.currentGame == null) { "Another game is already running" }

        HASManager.currentGame = this
        GamePhaseManager.prepareGame(this)
        prepared = true
    }

    suspend fun startGame() {
        check(prepared) { "Game is not prepared" }
        active = true
        GamePhaseManager.startGame()
    }

    suspend fun stopGame(reason: HASEndReason) {
        check(prepared) { "Game is not prepared" }

        active = false
        GamePhaseManager.endGame(reason)
        prepared = false
        HASManager.currentGame = null

        reset()
    }

    suspend fun forceStop() {
        check(prepared) { "Game is not prepared" }

        active = false
        GamePhaseManager.cancelImmediately()
        reset()
        prepared = false
        HASManager.currentGame = null
    }

    suspend fun reset() {
        coroutineScope {
            val roleJob = launch {
                forEachPlayerInRegion(
                    { it.HAS.setRole(HASUndefinedRole, sendMessage = false) },
                    concurrent = true
                )
            }

            val resetJob = launch { GamePhaseManager.resetGame() }

            roleJob.join()
            resetJob.join()
        }
    }

    suspend fun performPlayerCheck() {
        if (phase?.canPlayersJoin == true) return
        if (seekers.isEmpty() && !hiders.isEmpty()) {
            assignNewSeeker()
        }

        if (hiders.isEmpty()) {
            stopGame(HASEndReason.SEEKERS_WIN)
            return
        }

        if (seekers.isEmpty()) {
            stopGame(HASEndReason.HIDERS_WIN)
            return
        }
    }

    private suspend fun assignNewSeeker() {
        val newSeeker = hiders.random(random.asKotlinRandom())

        newSeeker.setRole(HASSeekerRole)
        newSeeker.prepare()
    }

    suspend fun teleportToLobby() {
        forEachPlayerInRegion({ it.tp(settings.lobbyLocation) }, concurrent = true)
    }
}