package dev.slne.hideandnseek.command.sub

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.hideandnseek.HASManager
import dev.slne.hideandnseek.HASPermissions
import dev.slne.hideandnseek.plugin
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandTree.forceStopCommand() = literalArgument("forcestop") {
    withPermission(HASPermissions.FORCESTOP_COMMAND)

    anyExecutor { sender, args ->
        val runningGame = HASManager.currentGame

        if (runningGame == null) {
            throw CommandAPI.failWithString("Es wurde noch kein Spiel gestartet.")
        }

        plugin.launch {
            runningGame.forceStop()
            sender.sendText {
                appendPrefix()
                success("Das Spiel wurde erfolgreich gestoppt.")
            }
        }
    }
}