package dev.slne.hideandnseek.listener.listeners;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import dev.slne.hideandnseek.HideAndSeekGame;
import dev.slne.hideandnseek.HideAndSeekManager;
import dev.slne.hideandnseek.player.HideAndSeekPlayer;
import dev.slne.hideandnseek.role.Role;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * The type Respawn listener.
 */
public class RespawnListener implements Listener {

  /**
   * On respawn.
   *
   * @param event the event
   */
  @EventHandler
  public void onRespawn(PlayerRespawnEvent event) {
    final HideAndSeekGame runningGame = HideAndSeekManager.INSTANCE.getRunningGame();
    final HideAndSeekPlayer player = HideAndSeekPlayer.get(event.getPlayer());

    event.setRespawnLocation(HideAndSeekManager.INSTANCE.getLobbyLocation());

    if (runningGame != null && runningGame.getGameState().isIngame()) {
      if (player.isSeeker()) {
        player.giveSeekerInventory();
      }

      event.setRespawnLocation(HideAndSeekManager.INSTANCE.getSpawnLocation());
    }
  }

  @EventHandler
  public void onPlayerPostRespawn(PlayerPostRespawnEvent event) {
    final Player bukkitPlayer = event.getPlayer();
    final HideAndSeekPlayer player = HideAndSeekPlayer.get(bukkitPlayer);

    if (player.isSeeker()) {
      return;
    }

    final HideAndSeekGame runningGame = HideAndSeekManager.INSTANCE.getRunningGame();
    if (runningGame != null && runningGame.getGameState().isIngame()) {
      bukkitPlayer.setAllowFlight(true);
      bukkitPlayer.setFlying(true);

      player.setRole(Role.SPECTATOR);

      //bukkitPlayer.setVisibleByDefault(false);
    }
  }
}
