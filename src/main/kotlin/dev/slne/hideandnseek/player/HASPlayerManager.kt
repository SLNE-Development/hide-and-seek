package dev.slne.hideandnseek.player

import com.github.benmanes.caffeine.cache.Caffeine
import java.util.*

object HASPlayerManager {
    private val cache = Caffeine.newBuilder()
        .build<UUID, HASPlayer> { HASPlayer(it) }

    fun get(uuid: UUID): HASPlayer {
        return cache.get(uuid)
    }
}