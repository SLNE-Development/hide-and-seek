package dev.slne.hideandnseek.game.area

import com.github.shynixn.mccoroutine.folia.asyncDispatcher
import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import dev.slne.hideandnseek.HASManager
import dev.slne.hideandnseek.plugin
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator

class HASGameArea(
    val world: World,
    val settings: HASAreaSettings
) {
    suspend fun unload() {
        withContext(plugin.globalRegionDispatcher) {
            Bukkit.unloadWorld(world, true)
        }
    }

    companion object {
        suspend fun load(name: String, loadAsync: Boolean = false, checkForLoadedSettings: Boolean = true): HASGameArea {
            val existingWorld = server.getWorld(name)
            val world = existingWorld ?: withContext(
                if (loadAsync) plugin.asyncDispatcher else plugin.globalRegionDispatcher
            ) {
                server.createWorld(WorldCreator(name))
                    ?: error("World $name could not be loaded")
            }

            val spawn = world.spawnLocation
            val existingSettings = if (checkForLoadedSettings) plugin.data.settings.areaSettings[name] else null
            val areaSettings = existingSettings ?: HASAreaSettings(name, spawn, world).also {
                if (checkForLoadedSettings) {
                    plugin.data.settings.areaSettings[name] = it
                }
            }

            return HASGameArea(world, areaSettings)
        }
    }
}

fun World.getArea(): HASGameArea? {
    return HASManager.getArea(name)
}