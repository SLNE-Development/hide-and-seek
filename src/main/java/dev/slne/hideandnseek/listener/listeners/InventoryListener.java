package dev.slne.hideandnseek.listener.listeners;

import dev.slne.hideandnseek.HideAndSeekManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

/**
 * The type Inventory listener.
 */
public class InventoryListener implements Listener {

  /**
   * On drop.
   *
   * @param event the event
   */
  @EventHandler
  public void onDrop(PlayerDropItemEvent event) {
    if (!HideAndSeekManager.INSTANCE.isBypassing(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onInventoryMove(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player player)) {
      return;
    }

    if (!HideAndSeekManager.INSTANCE.isBypassing(player)) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onDrag(InventoryDragEvent event) {
    if (!(event.getWhoClicked() instanceof Player player)) {
      return;
    }

    if (!HideAndSeekManager.INSTANCE.isBypassing(player)) {
      event.setCancelled(true);
    }
  }

}
