package dev.slne.hideandnseek.command.sub

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.hideandnseek.HASManager
import dev.slne.hideandnseek.HASPermissions
import dev.slne.hideandnseek.command.getAreaOrThrow
import dev.slne.hideandnseek.game.HASGame
import dev.slne.hideandnseek.game.area.HASGameArea
import dev.slne.hideandnseek.plugin
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.command.CommandSender

fun CommandTree.startCommand() = literalArgument("start") {
    withPermission(HASPermissions.START_COMMAND)

    stringArgument("area") {
        replaceSuggestions(ArgumentSuggestions.stringCollection { HASManager.getAreaKeys() })
        anyExecutor { sender, args ->
            val area: String by args
            startGame(sender,
                HASManager.getArea(area)
                    ?: throw CommandAPI.failWithString("No se encontró el área $area.")
            )
        }
    }

    playerExecutor { sender, args ->
        startGame(sender, sender.getAreaOrThrow())
    }
}

private fun startGame(sender: CommandSender, area: HASGameArea) {
    if (HASManager.currentGame != null) {
        throw CommandAPI.failWithString("Es la hora de jugar, pero no hay juego activo.")
    }

    val game = HASGame(area)
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