package dev.slne.hideandnseek.command.sub

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.LocationType
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.locationArgument
import dev.jorel.commandapi.kotlindsl.rotationArgument
import dev.jorel.commandapi.wrappers.Rotation
import dev.slne.hideandnseek.HASManager
import dev.slne.hideandnseek.HASPermissions
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Location
import org.bukkit.command.CommandSender

fun CommandTree.setSpawnCommand() = literalArgument("setspawn"){
    withPermission(HASPermissions.SET_SPAWN_COMMAND)

    locationArgument("location", LocationType.BLOCK_POSITION, true) {
        anyExecutor { sender, args -> setSpawn(sender, args.getUnchecked("location")!!) }
        rotationArgument("rotation") {
            anyExecutor { sender, args ->
                setSpawn(sender, args.getUnchecked("location")!!, args.getUnchecked("rotation")!!)
            }
        }
    }
}

private fun setSpawn(sender: CommandSender, location: Location, rotation: Rotation = Rotation(0f, 0f)) {
    HASManager.settings.spawnLocation = location.apply {
        yaw = rotation.yaw
        pitch = rotation.pitch
    }

    sender.sendText {
        appendPrefix()
        success("Du hast den Spawn gesetzt.")
    }
}