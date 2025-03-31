package dev.slne.hideandnseek

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import com.mojang.serialization.Dynamic
import dev.jorel.commandapi.wrappers.Location2D
import dev.slne.hideandnseek.command.hasCommand
import dev.slne.hideandnseek.game.HASGameRules
import dev.slne.hideandnseek.listener.HASListenerManager
import dev.slne.hideandnseek.storage.HASData
import dev.slne.hideandnseek.storage.HASDataFixers
import dev.slne.hideandnseek.storage.HASStorageSource
import dev.slne.surf.surfapi.bukkit.api.event.listen
import kotlinx.io.IOException
import org.bukkit.Bukkit
import org.bukkit.event.world.WorldSaveEvent
import org.bukkit.plugin.java.JavaPlugin

class PaperMain : SuspendingJavaPlugin() {
    lateinit var data: HASData

    override suspend fun onLoadAsync() {
    }

    override suspend fun onEnableAsync() {
        val session = HASStorageSource(dataPath, HASDataFixers.getDataFixer())

        var dynamic: Dynamic<*>? = null
        if (session.hasHASData()) {
            try {
                dynamic = session.getDataTag()
                data = HASStorageSource.getHASData(dynamic)
            } catch (e: IOException) {
                componentLogger.warn("Failed to load HAS data from ${session.dataFile}", e)
                componentLogger.info("Attempting to use fallback data")

                try {
                    dynamic = session.getOldDataTag()
                    data = HASStorageSource.getHASData(dynamic)
                } catch (e: IOException) {
                    componentLogger.error("Failed to load HAS data from ${session.oldDataFile}", e)
                    componentLogger.error(
                        "Failed to load HAS data from {} and {}. HAS data may be corrupted. Disabling plugin.",
                        session.dataFile,
                        session.oldDataFile
                    )
                    return
                }

                session.restoreDataFromOld()
            }
        }


        if (!this::data.isInitialized) {
            componentLogger.info("No HAS data found. Creating new data file.")
            val spawn = Bukkit.getWorlds().first().spawnLocation

            data = HASData(HASSettings(spawn, spawn, HASGameRules(), null, spawn.world, Location2D(spawn.world, spawn.x, spawn.z)))
            session.saveDataTag(data)
        }

        listen<WorldSaveEvent> {
            if (world == Bukkit.getWorlds().first()) {
                try {
                    session.saveDataTag(data)
                } catch (e: IOException) {
                    componentLogger.error("Failed to save HAS data to ${session.dataFile}", e)
                }
            }
        }

        HASListenerManager.register()
        hasCommand()
    }
}


val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)