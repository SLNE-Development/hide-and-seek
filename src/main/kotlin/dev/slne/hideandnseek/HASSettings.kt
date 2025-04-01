package dev.slne.hideandnseek

import com.mojang.serialization.Dynamic
import com.mojang.serialization.OptionalDynamic
import dev.jorel.commandapi.wrappers.Location2D
import dev.slne.hideandnseek.game.HASGameRules
import dev.slne.hideandnseek.game.area.HASAreaSettings
import dev.slne.hideandnseek.player.HASPlayer
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.querz.nbt.tag.CompoundTag
import net.querz.nbt.tag.ListTag
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.*
import kotlin.jvm.optionals.getOrNull

class HASSettings(
    var gameRules: HASGameRules,
    var initialSeekers: ObjectSet<HASPlayer>?,
    val areaSettings: Object2ObjectMap<String, HASAreaSettings>,
    var lobbyLocation: Location
) {
    companion object {
        fun parse(data: Dynamic<*>): HASSettings {
            val initialSeekers = data.get("InitialSeekers")
                .map { it.asList { UUID.fromString(it.get("uuid").asString().orThrow) } }
                .map { it.map { HASPlayer[it] } }
                .map { it.toObjectSet() }
                .result()
                .getOrNull()

            val areaSettings = data.get("AreaSettings")
                .asList {
                    val worldName = it.get("worldName").asString().orThrow
                    HASAreaSettings.create(worldName, it.get("settings").get().orThrow)
                }
                .associateTo(mutableObject2ObjectMapOf()) { it.worldName to it }


            return HASSettings(
                HASGameRules(data.get("GameRules")),
                initialSeekers,
                areaSettings,
                data.get("lobbyLocation").asLocation(),
            )
        }
    }

    fun writeToTag(tag: CompoundTag) {
        tag.put("GameRules", gameRules.createTag())

        val initialSeekers = initialSeekers
        if (initialSeekers != null) {
            val seekerTag = ListTag(CompoundTag::class.java)
            for (seeker in initialSeekers) {
                seekerTag.add(CompoundTag().apply {
                    putString("uuid", seeker.uuid.toString())
                })
            }

            tag.put("InitialSeekers", seekerTag)
        }

        val areaTag = ListTag(CompoundTag::class.java)
        for (area in areaSettings.values) {
            areaTag.add(CompoundTag().apply {
                putString("worldName", area.worldName)
                put("settings", area.createTag())
            })
        }
        tag.put("AreaSettings", areaTag)
        tag.put("lobbyLocation", lobbyLocation.toTag())
    }
}

fun OptionalDynamic<*>.asLocation(): Location {
    val worldUid = get("worldUid").asUuid(Bukkit.getWorlds().first().uid)
    val x = get("x").asDouble(0.0)
    val y = get("y").asDouble(0.0)
    val z = get("z").asDouble(0.0)
    return Location(Bukkit.getWorld(worldUid), x, y, z)
}

fun OptionalDynamic<*>.asUuid(default: UUID): UUID {
    val uuid = this.get("uuid").asString(default.toString())
    return UUID.fromString(uuid)
}

fun Location.toTag(): CompoundTag {
    val tag = CompoundTag()
    tag.putString("worldUid", world.uid.toString())
    tag.putDouble("x", x)
    tag.putDouble("y", y)
    tag.putDouble("z", z)
    return tag
}

fun Location2D.toTag(): CompoundTag {
    val tag = CompoundTag()
    tag.putString("worldUid", world.uid.toString())
    tag.putDouble("x", x)
    tag.putDouble("z", z)
    return tag
}