package dev.slne.hideandnseek.listener.listeners;

import dev.slne.hideandnseek.HideAndSeekGame;
import dev.slne.hideandnseek.HideAndSeekManager;
import dev.slne.hideandnseek.player.HideAndSeekPlayer;
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
      player.giveSeekerInventory();
      event.setRespawnLocation(HideAndSeekManager.INSTANCE.getSpawnLocation());
    }
  }

}
