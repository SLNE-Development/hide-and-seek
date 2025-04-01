package dev.slne.hideandnseek.game.area

import com.mojang.serialization.Dynamic
import dev.jorel.commandapi.wrappers.Location2D
import dev.slne.hideandnseek.asLocation
import dev.slne.hideandnseek.asUuid
import dev.slne.hideandnseek.toTag
import net.querz.nbt.tag.CompoundTag
import net.querz.nbt.tag.StringTag
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import kotlin.jvm.optionals.getOrElse

data class HASAreaSettings(
    var worldName: String,
    var lobbyLocation: Location,
    var spawnLocation: Location,
    var world: World,
    var boarderCenter: Location2D = Location2D(world, 0.0, 0.0),
    var startRadius: Int = 1000,
    var endRadius: Int = 25,
    var lobbyBorderRadius: Int = 15,
) {
    fun createTag() = CompoundTag().apply {
        put("lobbyLocation", lobbyLocation.toTag())
        put("spawnLocation", spawnLocation.toTag())
        put("worldUid", StringTag(world.uid.toString()))
        put("BoarderCenter", boarderCenter.toTag())
        putInt("startRadius", startRadius)
        putInt("endRadius", endRadius)
        putInt("lobbyBorderRadius", lobbyBorderRadius)
    }

    companion object {
        fun create(dynamic: Dynamic<*>): HASAreaSettings {
            val worldUuid = dynamic.get("worldUid").asUuid(Bukkit.getWorlds().first().uid)
            val world = Bukkit.getWorld(worldUuid) ?: error("World with UUID $worldUuid not found")

            val lobbyLocation = dynamic.get("lobbyLocation").asLocation()
            val spawnLocation = dynamic.get("spawnLocation").asLocation()
            val boarderCenter = dynamic.get("BoarderCenter")
                .map { Location2D(world, it.get("x").asDouble(0.0), it.get("z").asDouble(0.0)) }
                .result()
                .getOrElse { Location2D(world, 0.0, 0.0) }
            val startRadius = dynamic.get("startRadius").asInt(1000)
            val endRadius = dynamic.get("endRadius").asInt(25)
            val lobbyBorderRadius = dynamic.get("lobbyBorderRadius").asInt(15)

            return HASAreaSettings(
                "",
                lobbyLocation,
                spawnLocation,
                world,
                boarderCenter,
                startRadius,
                endRadius,
                lobbyBorderRadius
            )
        }
    }
}