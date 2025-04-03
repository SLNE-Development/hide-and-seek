package dev.slne.hideandnseek.command.sub

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.hideandnseek.HASManager
import dev.slne.hideandnseek.HASPermissions
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandTree.bypassCommand() = literalArgument("bypass") {
    withPermission(HASPermissions.BYPASS_COMMAND)
    playerExecutor { player, _ ->
        val switchBypass = HASManager.switchBypass(player)
        player.sendText {
            appendPrefix()
            success("Du bist jetzt ")
            if (switchBypass) {
                success("im Bypass-Modus")
            } else {
                success("nicht mehr im Bypass-Modus")
            }
            success(".")
        }
    }
}
