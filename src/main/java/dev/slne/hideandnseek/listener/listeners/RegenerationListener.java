package dev.slne.hideandnseek.listener.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

/**
 * The type Regeneration listener.
 */
public class RegenerationListener implements Listener {

  /**
   * On regenerate.
   *
   * @param event the event
   */
  @EventHandler
  public void onRegenerate(EntityRegainHealthEvent event) {
    if (event.getEntity() instanceof Player) {
      if (!isAllowed(event.getRegainReason())) {
        event.setCancelled(true);
      }
    }
  }

  /**
   * Is allowed boolean.
   *
   * @param reason the reason
   * @return the boolean
   */
  private boolean isAllowed(RegainReason reason) {
    return switch (reason) {
      case MAGIC, MAGIC_REGEN -> true;
      default -> false;
    };
  }

}
