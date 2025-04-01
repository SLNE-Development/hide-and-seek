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
import dev.slne.hideandnseek.game.area.HASGameArea
import dev.slne.hideandnseek.plugin
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandTree.createAreaCommand() = literalArgument("createArea") {
    withPermission(HASPermissions.CREATE_AREA_COMMAND)

    stringArgument("worldName") {
        anyExecutor { sender, args ->
            val worldName: String by args
            val existing = HASManager.getArea(worldName)

            if (existing != null) {
                throw CommandAPI.failWithString("Area with name $worldName already loaded.")
            }

            plugin.launch {
                val loaded = HASGameArea.load(worldName)
                HASManager.addArea(loaded)

                sender.sendText {
                    appendPrefix()
                    success("Die Arena ")
                    variableValue(worldName)
                    success(" wurde erfolgreich erstellt.")
                }
            }
        }
    }
}