package dev.slne.hideandnseek

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object HASPermissions: PermissionRegistry() {
    val BYPASS_END_KICK = create("hideandseek.kick.end.bypass")
    val BYPASS_ELIMINATION_KICK = create("hideandseek.kick.elimination.bypass")

    val ROOT_COMMAND = create("hideandseek.command")
    val GAMERULE_COMMAND = create("hideandseek.command.gamerule")
    val BYPASS_COMMAND = create("hideandseek.command.bypass")
    val FORCESTOP_COMMAND = create("hideandseek.command.forcestop")
    val SET_LOBBY_COMMAND = create("hideandseek.command.setlobby")
    val SET_SPAWN_COMMAND = create("hideandseek.command.setspawn")
    val START_COMMAND = create("hideandseek.command.start")
    val CREATE_AREA_COMMAND = create("hideandseek.command.createarea")
    val DELETE_AREA_COMMAND = create("hideandseek.command.deletearea")
}