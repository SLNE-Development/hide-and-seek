package dev.slne.hideandnseek.listener

import dev.slne.hideandnseek.listener.listeners.*
import dev.slne.surf.surfapi.bukkit.api.event.register

object HASListenerManager {

    fun register() {
        HASConnectionListener.register()
        HASDamageListener.register()
        HASDeathListener.register()
        HASHungerListener.register()
        HASInteractListener.register()
        HASInventoryListener.register()
        HASPlayerMoveListener.register()
        HASPotteryProtectionListener.register()
        HASRegenerationListener.register()
        HASRespawnListener.register()
        HASEntityDestroyListener.register()
        HASProjectileShootListener.register()
    }
}