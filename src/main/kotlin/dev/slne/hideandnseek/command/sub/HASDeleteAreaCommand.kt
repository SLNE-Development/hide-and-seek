package dev.slne.hideandnseek.command.sub

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.stringArgument
import dev.slne.hideandnseek.HASManager
import dev.slne.hideandnseek.HASPermissions
import dev.slne.hideandnseek.plugin
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandTree.deleteAreaCommand() = literalArgument("deleteArea") {
    withPermission(HASPermissions.DELETE_AREA_COMMAND)

    stringArgument("worldName") {
        anyExecutor { sender, args ->
            val worldName: String by args

            plugin.launch {
                val area = HASManager.getArea(worldName)

                if (area == null) {
                    throw CommandAPI.failWithString("Area with name $worldName not found.")
                }

                HASManager.deleteArea(area)
                area.unload()

                sender.sendText {
                    appendPrefix()
                    success("Die Arena ")
                    variableValue(worldName)
                    success(" wurde erfolgreich gelöscht.")
                }
            }
        }
    }
}