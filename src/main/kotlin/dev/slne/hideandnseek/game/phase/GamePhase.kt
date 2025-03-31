package dev.slne.hideandnseek.game.phase

import dev.slne.hideandnseek.game.HASEndReason

interface GamePhase {
    val canPlayersJoin: Boolean get() = true
    val isPlayerDamageable: Boolean get() = false

    suspend fun prepare() {}
    suspend fun start() {}
    suspend fun end(reason: HASEndReason) {}
    suspend fun reset() {}
}