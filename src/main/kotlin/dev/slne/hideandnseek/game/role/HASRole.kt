package dev.slne.hideandnseek.game.role

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import dev.slne.hideandnseek.HASManager
import dev.slne.hideandnseek.old.Items
import dev.slne.hideandnseek.plugin
import dev.slne.hideandnseek.util.tp
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.format.TextColor
import org.bukkit.GameMode
import org.bukkit.entity.Player

abstract class HASRole(
    val displayName: String,
    val color: TextColor,
    val gameMode: GameMode = GameMode.ADVENTURE
) {
    open suspend fun giveInventory(player: Player) {}
    open suspend fun teleportStartPosition(player: Player) {}
    open fun canDamage(role: HASRole): Boolean = true

    object Seeker : HASRole("Sucher", TextColor.color(0xFF0000)) {
        override suspend fun giveInventory(player: Player) =
            withContext(plugin.entityDispatcher(player)) {
                Items.prepareSeekerInventory(player)
            }

        override suspend fun teleportStartPosition(player: Player) {
            player.tp(HASManager.settings.lobbyLocation)
        }

        override fun canDamage(role: HASRole) = role != this

    }

    object Hider : HASRole("Verstecker", TextColor.color(0x00FF00)) {
        override suspend fun teleportStartPosition(player: Player) {
            player.tp(HASManager.settings.spawnLocation)
        }
    }

    object Undefined : HASRole("Unbekannt", TextColor.color(0xFFFFFF)) {
        override fun canDamage(role: HASRole) = false
    }

    object Spectator : HASRole("Zuschauer", TextColor.color(0x0000FF), GameMode.SPECTATOR) {
    }
}