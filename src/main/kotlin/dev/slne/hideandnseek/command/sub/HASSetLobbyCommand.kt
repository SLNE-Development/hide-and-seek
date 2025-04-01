package dev.slne.hideandnseek.command.sub

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.LocationType
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.locationArgument
import dev.slne.hideandnseek.HASPermissions
import dev.slne.hideandnseek.plugin
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Location

fun CommandTree.setLobbyCommand() = literalArgument("setLobby") {
    withPermission(HASPermissions.SET_LOBBY_COMMAND)

    locationArgument("location", LocationType.BLOCK_POSITION, true) {
        anyExecutor { sender, args ->
            val location: Location by args

            plugin.data.settings.lobbyLocation = location
            sender.sendText {
                appendPrefix()
                success("Die Lobby-Position wurde auf ")
                variableValue(location.toString())
                success(" gesetzt.")
            }
        }
    }
}