package dev.slne.hideandnseek.papi.placeholder

import dev.slne.hideandnseek.old.util.TimeUtil
import dev.slne.surf.surfapi.bukkit.api.hook.papi.expansion.PapiPlaceholder
import org.bukkit.OfflinePlayer
import java.util.concurrent.TimeUnit

class HASCountdownPlaceholder: PapiPlaceholder("countdown") {
    override fun parse(
        player: OfflinePlayer,
        args: List<String>
    ): String? {
        return TimeUtil.formatTimestampString(TimeUnit.SECONDS, currentCountdownSeconds ?: return null)
    }

    companion object {
        var currentCountdownSeconds: Long? = null
    }
}