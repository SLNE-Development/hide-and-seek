package dev.slne.hideandnseek.command.sub

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.hideandnseek.HASManager
import dev.slne.hideandnseek.HASPermissions
import dev.slne.hideandnseek.game.HASGame
import dev.slne.hideandnseek.plugin
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandTree.startCommand() = literalArgument("start") {
    withPermission(HASPermissions.START_COMMAND)

    anyExecutor { sender, args ->
        if (HASManager.currentGame != null) {
            throw CommandAPI.failWithString("Es la hora de jugar, pero no hay juego activo.")
        }

        val game = HASGame()
        sender.sendText {
            appendPrefix()
            success("Iniciando juego...")
        }
        plugin.launch {
            game.prepareGame()
            sender.sendText {
                appendPrefix()
                success("Juego preparado. Iniciando...")
            }
            game.startGame()
        }
    }
}