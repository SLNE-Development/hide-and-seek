package dev.slne.hideandnseek.command.sub

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.LocationType
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.hideandnseek.HASPermissions
import dev.slne.hideandnseek.command.getAreaOrThrow
import org.bukkit.Location

fun CommandTree.setLobbyCommand() = literalArgument("setLobby") {
    withPermission(HASPermissions.SET_LOBBY_COMMAND)

    locationArgument("location", LocationType.BLOCK_POSITION, true) {
        integerArgument("radius", 1) {
            playerExecutor { sender, args ->
                val location: Location by args
                val radius: Int by args
                val area = sender.getAreaOrThrow()

                area.settings.apply {
                    lobbyLocation = location
                    lobbyBorderRadius = radius
                }
            }
        }
    }
}