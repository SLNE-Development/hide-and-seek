package dev.slne.hideandnseek

import dev.slne.hideandnseek.game.HASGame
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import org.bukkit.entity.Player
import java.util.*

object HASManager {
    val settings get() = plugin.data.settings
    var currentGame: HASGame? = null

    private val bypassingPlayers = mutableObjectSetOf<UUID>()

    fun isBypassing(player: UUID): Boolean {
        return bypassingPlayers.contains(player)
    }

    fun isBypassing(player: Player): Boolean {
        return isBypassing(player.uniqueId)
    }

    fun switchBypass(player: Player): Boolean {
        if (bypassingPlayers.remove(player.uniqueId)) {
            return false
        } else {
            bypassingPlayers.add(player.uniqueId)
            return true
        }
    }
}