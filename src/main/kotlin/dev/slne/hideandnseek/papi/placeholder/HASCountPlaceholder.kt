package dev.slne.hideandnseek.papi.placeholder

import dev.slne.hideandnseek.HASManager
import dev.slne.surf.surfapi.bukkit.api.hook.papi.expansion.PapiPlaceholder
import org.bukkit.OfflinePlayer

class HASCountPlaceholder: PapiPlaceholder("count") {
    enum class Type(val value: String) {
        HIDER("hider") {
            override fun parse(player: OfflinePlayer): String? {
                return HASManager.currentGame?.hiders?.count()?.toString() ?: "0"
            }
        },
        SEEKER("seeker") {
            override fun parse(player: OfflinePlayer): String? {
                return HASManager.currentGame?.seekers?.count()?.toString() ?: "0"
            }
        };

        abstract fun parse(player: OfflinePlayer): String?
    }

    override fun parse(
        player: OfflinePlayer,
        args: List<String>
    ): String? {
        if (args.size != 1) return null
        val type = Type.entries.find { it.value == args[0] } ?: return null
        return type.parse(player)
    }
}