package dev.slne.hideandnseek.game.area

import com.mojang.serialization.Dynamic
import dev.slne.hideandnseek.asLocation
import dev.slne.hideandnseek.asUuid
import dev.slne.hideandnseek.toTag
import net.querz.nbt.tag.CompoundTag
import net.querz.nbt.tag.StringTag
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World

data class HASAreaSettings(
    val worldName: String,
    var spawnLocation: Location,
    var world: World,
    var startRadius: Int = 1000,
    var endRadius: Int = 25,
) {
    fun createTag() = CompoundTag().apply {
        put("spawnLocation", spawnLocation.toTag())
        put("worldUid", StringTag(world.uid.toString()))
        putInt("startRadius", startRadius)
        putInt("endRadius", endRadius)
    }

    companion object {
        fun create(worldName: String, dynamic: Dynamic<*>): HASAreaSettings? {
            val worldUuid = dynamic.get("worldUid").asUuid(null)
            val world = Bukkit.getWorld(worldUuid) ?: return null

            val spawnLocation = dynamic.get("spawnLocation").asLocation()
            val startRadius = dynamic.get("startRadius").asInt(1000)
            val endRadius = dynamic.get("endRadius").asInt(25)

            return HASAreaSettings(
                worldName,
                spawnLocation,
                world,
                startRadius,
                endRadius,
            )
        }
    }
}