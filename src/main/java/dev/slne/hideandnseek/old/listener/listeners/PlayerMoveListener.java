package dev.slne.hideandnseek.old.listener.listeners;

import dev.slne.hideandnseek.old.HideAndSeekGame;
import dev.slne.hideandnseek.old.HideAndSeekManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * The type Player move listener.
 */
public class PlayerMoveListener implements Listener {

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    final HideAndSeekGame game = HideAndSeekManager.INSTANCE.getRunningGame();

    if (game == null || !game.getGameState().isDamagable()) {
      return;
    }

    Player player = event.getPlayer();
    if (player.getLocation().getBlock().getType().equals(Material.WATER)) {
      player.setVelocity(player.getLocation().getDirection().multiply(-1).setY(0.5));
      player.damage(5);
    }
  }
}
