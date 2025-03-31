package dev.slne.hideandnseek.game.role

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import dev.slne.hideandnseek.HASManager
import dev.slne.hideandnseek.old.Items
import dev.slne.hideandnseek.plugin
import dev.slne.hideandnseek.util.tp
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.format.TextColor
import org.bukkit.entity.Player

object HASSeekerRole : HASRole("Sucher", TextColor.color(0xFF0000)) {
    override suspend fun giveInventory(player: Player) =
        withContext(plugin.entityDispatcher(player)) {
            Items.prepareSeekerInventory(player)
        }

    override suspend fun teleportStartPosition(player: Player) {
        player.tp(HASManager.settings.lobbyLocation)
    }

    override fun canDamage(role: HASRole) = role != this

}