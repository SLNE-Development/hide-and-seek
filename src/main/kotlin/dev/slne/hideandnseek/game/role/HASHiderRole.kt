package dev.slne.hideandnseek.game.role

import dev.slne.hideandnseek.HASManager
import dev.slne.hideandnseek.game.HASGameRules
import dev.slne.hideandnseek.plugin
import dev.slne.hideandnseek.util.HAS
import dev.slne.hideandnseek.util.tp
import net.kyori.adventure.text.format.TextColor
import org.bukkit.entity.Player

object HASHiderRole : HASRole("Verstecker", TextColor.color(0x3498DB)) {
    override suspend fun teleportStartPosition(player: Player) {
        player.tp(HASManager.settings.spawnLocation)
    }

    override suspend fun applyScale(player: Player) {
        player.HAS.setScale(plugin.data.settings.gameRules.getDouble(HASGameRules.RULE_PLAYER_SCALE))
    }
}