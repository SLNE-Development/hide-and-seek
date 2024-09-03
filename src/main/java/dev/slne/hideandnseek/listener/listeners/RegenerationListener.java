package dev.slne.hideandnseek.listener.listeners;

import java.util.Arrays;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

/**
 * The type Regeneration listener.
 */
public class RegenerationListener implements Listener {

  private final RegainReason[] ALLOWED_REASONS = new RegainReason[]{
      RegainReason.MAGIC, RegainReason.MAGIC_REGEN
  };

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
    return Arrays.stream(ALLOWED_REASONS).anyMatch(allowed -> allowed == reason);
  }

}
