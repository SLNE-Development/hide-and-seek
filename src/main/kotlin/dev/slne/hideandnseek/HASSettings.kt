package dev.slne.hideandnseek

import com.mojang.serialization.Dynamic
import com.mojang.serialization.OptionalDynamic
import dev.jorel.commandapi.wrappers.Location2D
import dev.slne.hideandnseek.game.HASGameRules
import dev.slne.hideandnseek.player.HASPlayer
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.querz.nbt.tag.CompoundTag
import net.querz.nbt.tag.ListTag
import net.querz.nbt.tag.StringTag
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import java.util.*
import kotlin.jvm.optionals.getOrNull

class HASSettings(
    var lobbyLocation: Location,
    var spawnLocation: Location,
    var gameRules: HASGameRules,
    var initialSeekers: ObjectSet<HASPlayer>?,
    var world: World,
    var boarderCenter: Location2D,
) {
    companion object {
        fun parse(data: Dynamic<*>): HASSettings {
            val initialSeekers = data.get("InitialSeekers")
                .map { it.asList { UUID.fromString(it.get("uuid").asString().orThrow) } }
                .map { it.map { HASPlayer[it] } }
                .map { it.toObjectSet() }
                .result()
                .getOrNull()

            val worldUuid = data.get("worldUid").asUuid(Bukkit.getWorlds().first().uid)
            val world = Bukkit.getWorld(worldUuid) ?: error("World with UUID $worldUuid not found")

            return HASSettings(
                data.get("LobbyLocation").asLocation(),
                data.get("SpawnLocation").asLocation(),
                HASGameRules(data.get("GameRules")),
                initialSeekers,
                world,
                data.get("BoarderCenter")
                    .map { Location2D(world, it.get("x").asDouble(0.0), it.get("z").asDouble(0.0)) }
                    .result()
                    .getOrNull() ?: Location2D(world, 0.0, 0.0)
            )
        }
    }

    fun writeToTag(tag: CompoundTag) {
        tag.put("LobbyLocation", lobbyLocation.toTag())
        tag.put("SpawnLocation", spawnLocation.toTag())
        tag.put("GameRules", gameRules.createTag())

        val initialSeekers = initialSeekers
        if (initialSeekers != null) {
            val seekerTag = ListTag(StringTag::class.java)
            for (seeker in initialSeekers) {
                seekerTag.addString(seeker.uuid.toString())
            }

            tag.put("InitialSeekers", seekerTag)
        }

        tag.put("worldUid", StringTag(world.uid.toString()))
        tag.put("BoarderCenter", boarderCenter.toTag())
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