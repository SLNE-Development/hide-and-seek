package dev.slne.hideandnseek.command

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.slne.hideandnseek.HASPermissions
import dev.slne.hideandnseek.command.sub.*
import dev.slne.hideandnseek.game.area.getArea
import org.bukkit.entity.Player

fun hasCommand() = commandTree("hideandseek") {
    withAliases("has")
    withPermission(HASPermissions.ROOT_COMMAND)

    settingsCommand()
    bypassCommand()
    forceStopCommand()
    setLobbyCommand()
    setSpawnCommand()
    startCommand()
    createAreaCommand()
    deleteAreaCommand()
}

fun Player.getAreaOrThrow() = world.getArea() ?: throw CommandAPI.failWithString("Du befindest dich nicht in einer Arena.")