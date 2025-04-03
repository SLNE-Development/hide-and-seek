package dev.slne.hideandnseek.listener.listeners

import dev.slne.hideandnseek.util.cancel
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent

object HASHungerListener : Listener {
    @EventHandler
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        if (event.foodLevel < 20) {
            event.cancel()
            event.entity.foodLevel = 20
        }
    }
}