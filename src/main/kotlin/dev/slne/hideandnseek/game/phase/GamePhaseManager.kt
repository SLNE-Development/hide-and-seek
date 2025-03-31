package dev.slne.hideandnseek.game.phase

import dev.slne.hideandnseek.game.HASEndReason
import dev.slne.hideandnseek.game.HASGame
import dev.slne.hideandnseek.game.phase.phases.EndGamePhase
import dev.slne.hideandnseek.game.phase.phases.IngamePhase
import dev.slne.hideandnseek.game.phase.phases.LobbyPhase
import dev.slne.hideandnseek.game.phase.phases.PreparationPhase
import dev.slne.surf.surfapi.core.api.util.logger
import dev.slne.surf.surfapi.core.api.util.objectListOf
import kotlinx.coroutines.*

object GamePhaseManager {
    private val log = logger()

    private var phases = objectListOf<GamePhase>()
    private var phaseJob: Job? = null
    var currentPhase: GamePhase? = null
        private set

    var isRunning: Boolean = false
        private set

    suspend fun prepareGame(game: HASGame) {
        check(!isRunning) { "Game is already running" }
        isRunning = true

        phases = objectListOf(
            LobbyPhase(game),
            PreparationPhase(game),
            IngamePhase(game),
            EndGamePhase(game)
        )

        runPhasesSequentially { phase ->
            phase.prepare()
        }
    }

    suspend fun startGame() {
        check(isRunning) { "Game is not running" }

        runPhasesSequentially { phase ->
            phase.start()
        }
    }

    suspend fun endGame(reason: HASEndReason) {
        check(isRunning) { "Game is not running" }

        runPhasesSequentially { phase ->
            phase.end(reason)
        }

        isRunning = false
    }

    suspend fun resetGame() {
        runPhasesSequentially { phase ->
            phase.reset()
        }
    }

    suspend fun cancelImmediately() {
        phaseJob?.cancelAndJoin()
        phaseJob = null
        currentPhase = null

        resetGame()
        isRunning = false
    }

    private suspend fun runPhasesSequentially(action: suspend (GamePhase) -> Unit) {
        val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

        phaseJob = scope.launch {
            for (phase in phases) {
                try {
                    currentPhase = phase
                    action(phase)
                } catch (e: CancellationException) {
                    currentPhase = null
                    throw e
                }

                yield()
            }
        }.also {
            it.join()
        }
    }
}