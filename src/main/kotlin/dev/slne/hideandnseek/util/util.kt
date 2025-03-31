package dev.slne.hideandnseek.util

import dev.slne.hideandnseek.player.HASPlayer
import dev.slne.surf.surfapi.core.api.messages.adventure.playSound
import kotlinx.coroutines.future.await
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import java.nio.file.Path
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.moveTo
import org.bukkit.Sound as BukkitSound

suspend fun Player.tp(location: Location, playSound: Boolean = true) {
    teleportAsync(location).await()
    if (playSound) {
        playSound {
            type(BukkitSound.ENTITY_ENDERMAN_TELEPORT)
            volume(.5f)
            source(Sound.Source.HOSTILE)
        }
    }
}

val Player.HAS get() = HASPlayer[this.uniqueId]

fun Cancellable.cancel() {
    isCancelled = true
}

fun safeReplaceOrMoveFile(
    current: Path,
    newPath: Path,
    backup: Path,
    noRestoreOnFail: Boolean
): Boolean {
    if (current.exists()) {
        if (!runWithRetries(10, "create backup $backup",
                { backup.deleteIfExists(); true },
                { current.moveTo(backup, overwrite = true); true },
                { backup.exists() })
        ) return false
    }

    if (!runWithRetries(10, "remove old $current",
            { current.deleteIfExists(); true },
            { !current.exists() })
    ) return false

    if (!runWithRetries(10, "replace $current with $newPath",
            { newPath.moveTo(current, overwrite = true); true },
            { current.exists() })
        && !noRestoreOnFail
    ) {
        runWithRetries(10, "restore $current from $backup",
            { backup.moveTo(current, overwrite = true); true },
            { current.exists() })
        return false
    }

    return true
}

private val retryLogger = ComponentLogger.logger("FileOperation")
fun runWithRetries(retries: Int, taskName: String, vararg steps: () -> Boolean): Boolean {
    repeat(retries) { attempt ->
        if (steps.all { runCatching(it).getOrElse { false } }) return true
        retryLogger.error("Failed to {}, retrying {}/{}", taskName, attempt + 1, retries)
    }
    retryLogger.error("Failed to {}, aborting, progress might be lost", taskName)
    return false
}
