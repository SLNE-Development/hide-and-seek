package dev.slne.hideandnseek.command.sub

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.LocationType
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.hideandnseek.HASManager
import dev.slne.hideandnseek.HASPermissions
import dev.slne.hideandnseek.game.HASGameRules
import dev.slne.hideandnseek.plugin
import org.bukkit.Location

fun CommandTree.setLobbyCommand() = literalArgument("setLobby") {
    withPermission(HASPermissions.SET_LOBBY_COMMAND)

    locationArgument("location", LocationType.BLOCK_POSITION, true) {
        integerArgument("radius", 1) {
            anyExecutor { sender, args ->
                val location: Location by args

                HASManager.settings.lobbyLocation = location
                plugin.data.settings.gameRules.getRule(HASGameRules.RULE_LOBBY_BORDER_RADIUS)
                    .setFromArgument(args, "radius", HASGameRules.RULE_LOBBY_BORDER_RADIUS)
            }
        }
    }
}