package dev.slne.hideandnseek.game.role

import dev.slne.hideandnseek.HASManager
import dev.slne.hideandnseek.util.tp
import net.kyori.adventure.text.format.TextColor
import org.bukkit.entity.Player

object HASHiderRole : HASRole("Verstecker", TextColor.color(0x00FF00)) {
    override suspend fun teleportStartPosition(player: Player) {
        player.tp(HASManager.settings.spawnLocation)
    }
}