package dev.slne.hideandnseek.listener.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * The type Hunger listener.
 */
public class HungerListener implements Listener {

  /**
   * On hunger change.
   *
   * @param event the event
   */
  @EventHandler
  public void onHungerChange(FoodLevelChangeEvent event) {
    event.setCancelled(true);
  }

}
