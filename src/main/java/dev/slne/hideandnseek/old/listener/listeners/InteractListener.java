package dev.slne.hideandnseek.old.listener.listeners;

import dev.slne.hideandnseek.old.HideAndSeekManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * The type Interact listener.
 */

@SuppressWarnings("deprecation")
public class InteractListener implements Listener {

  /**
   * On interact.
   *
   * @param event the event
   */
  @EventHandler
  public void onInteract(PlayerInteractEvent event) {
    Block clickedBlock = event.getClickedBlock();
    Player player = event.getPlayer();

    if(clickedBlock == null){
      return;
    }

    if(this.isDoor(clickedBlock.getType())){
      return;
    }

    if(!player.getItemInHand().isEmpty() && player.getItemInHand().getType().equals(Material.BOW)){
      return;
    }

    if (!HideAndSeekManager.INSTANCE.isBypassing(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  private boolean isDoor(Material material) {
    return material == Material.OAK_DOOR ||
        material == Material.SPRUCE_DOOR ||
        material == Material.BIRCH_DOOR ||
        material == Material.JUNGLE_DOOR ||
        material == Material.ACACIA_DOOR ||
        material == Material.DARK_OAK_DOOR ||
        material == Material.CRIMSON_DOOR ||
        material == Material.WARPED_DOOR;
  }
}
