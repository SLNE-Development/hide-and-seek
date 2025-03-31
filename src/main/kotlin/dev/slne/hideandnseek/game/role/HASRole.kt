package dev.slne.hideandnseek.game.role

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

}