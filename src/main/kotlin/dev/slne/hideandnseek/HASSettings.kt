package dev.slne.hideandnseek

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.google.common.base.Supplier
import com.google.common.base.Suppliers
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
import kotlinx.coroutines.withContext
import net.querz.nbt.tag.CompoundTag
import net.querz.nbt.tag.ListTag
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.*
import kotlin.jvm.optionals.getOrNull

class HASSettings(
    var gameRules: HASGameRules,
    var initialSeekers: ObjectSet<HASPlayer>?,
    private val lazyAreaSettings: Supplier<out Object2ObjectMap<String, HASAreaSettings>>,
    val worldsToLoad: ObjectSet<String>,
    var lobbyLocation: Location
) {
    val areaSettings: Object2ObjectMap<String, HASAreaSettings>
        get() = lazyAreaSettings.get()

    suspend fun changeLobbyLocation(location: Location) {
        this.lobbyLocation = location
        refreshLobbyLocation()
    }

    suspend fun refreshLobbyLocation() {
        withContext(plugin.globalRegionDispatcher) {
            lobbyLocation.world.worldBorder.center = lobbyLocation
        }
    }

    companion object {
        fun parse(data: Dynamic<*>): HASSettings {
            println("Parsing HASSettings from data: $data")

            val initialSeekers = data.get("InitialSeekers")
                .map { it.asList { UUID.fromString(it.get("uuid").asString().orThrow) } }
                .map { it.map { HASPlayer[it] } }
                .map { it.toObjectSet() }
                .result()
                .getOrNull()

            val areaSettings = Suppliers.memoize {
                data.get("AreaSettings")
                .asList {
                    val worldName = it.get("worldName").asString().orThrow
                    HASAreaSettings.create(worldName, it.get("settings").get().orThrow)
                }
                .filterNotNull()
                .associateTo(mutableObject2ObjectMapOf()) { it.worldName to it }
            }

            val worldsToLoad = data.get("AreaSettings")
                .asList { it.get("worldName").asString().orThrow }
                .toObjectSet()

            val gameRules = HASGameRules(data.get("GameRules"))
            val lobbyLocation = data.get("lobbyLocation").asLocation()

            return HASSettings(
                gameRules,
                initialSeekers,
                areaSettings,
                worldsToLoad,
                lobbyLocation,
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
    val worldUid = get("worldUid").asUuid(null)
    val x = get("x").asNumber().orThrow.toDouble()
    val y = get("y").asNumber().orThrow.toDouble()
    val z = get("z").asNumber().orThrow.toDouble()
    return Location(Bukkit.getWorld(worldUid), x, y, z)
}

fun OptionalDynamic<*>.asUuid(default: UUID?): UUID {
    val uuid = if (default != null) {
        asString(default.toString())
    } else {
        asString().result().orElseThrow()
    }

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