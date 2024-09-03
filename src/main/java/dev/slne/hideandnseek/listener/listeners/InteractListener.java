package dev.slne.hideandnseek.listener.listeners;

import dev.slne.hideandnseek.HideAndSeekManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * The type Interact listener.
 */
public class InteractListener implements Listener {

  /**
   * On interact.
   *
   * @param event the event
   */
  @EventHandler
  public void onInteract(PlayerInteractEvent event) {
    if (!HideAndSeekManager.INSTANCE.isByPassing(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

}
