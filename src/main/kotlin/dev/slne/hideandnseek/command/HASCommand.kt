package dev.slne.hideandnseek.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.slne.hideandnseek.HASPermissions
import dev.slne.hideandnseek.command.sub.*

fun hasCommand() = commandTree("hideandseek") {
    withAliases("has")
    withPermission(HASPermissions.ROOT_COMMAND)

    settingsCommand()
    bypassCommand()
    forceStopCommand()
    setLobbyCommand()
    setSpawnCommand()
    startCommand()
}