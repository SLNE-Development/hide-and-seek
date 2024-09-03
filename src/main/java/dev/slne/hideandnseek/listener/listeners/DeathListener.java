package dev.slne.hideandnseek.listener.listeners;

import dev.slne.hideandnseek.HideAndSeekGame;
import dev.slne.hideandnseek.HideAndSeekManager;
import dev.slne.hideandnseek.Messages;
import dev.slne.hideandnseek.player.HideAndSeekPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * The type Death listener.
 */
public class DeathListener implements Listener {

  /**
   * On player death.
   *
   * @param event the event
   */
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    HideAndSeekPlayer player = HideAndSeekPlayer.get(event.getPlayer());

    event.getDrops().clear();

    HideAndSeekGame runningGame = HideAndSeekManager.INSTANCE.getRunningGame();
    printDeathMessage(event);

    if (runningGame == null) {
      return;
    }

    if (runningGame.isHider(player)) {
      runningGame.removeHider(player);

      if (runningGame.doHidersBecomeSeekers()) {
        runningGame.addSeeker(player);
      }
    }

    runningGame.performPlayerCheck();
    
    player.getPlayer().spigot().respawn();
  }

  /**
   * Print death message.
   *
   * @param event the event
   */
  private void printDeathMessage(PlayerDeathEvent event) {
    Player died = event.getPlayer();
    Entity killer = died.getKiller();

    HideAndSeekPlayer diedPlayer = HideAndSeekPlayer.get(died);
    Component diedDisplayName = Messages.displayName(diedPlayer);

    if (killer != null) {
      HideAndSeekPlayer killerPlayer = HideAndSeekPlayer.get(killer.getUniqueId());
      Component killerDisplayName = Messages.displayName(killerPlayer);

      Bukkit.broadcast(
          Messages.prefix().append(diedDisplayName).append(Component.text(" wurde von ",
                  NamedTextColor.GRAY)).append(killerDisplayName)
              .append(Component.text(" getötet.", NamedTextColor.GRAY)));
    } else {
      Bukkit.broadcast(Messages.prefix().append(diedDisplayName)
          .append(Component.text(" ist gestorben.", NamedTextColor.GRAY)));
    }
  }

}
