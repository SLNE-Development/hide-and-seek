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
    final Player bukkitPlayer = event.getPlayer();
    final HideAndSeekPlayer player = HideAndSeekPlayer.get(bukkitPlayer);

    event.getDrops().clear();

    final HideAndSeekGame runningGame = HideAndSeekManager.INSTANCE.getRunningGame();

    if (runningGame == null) {
      return;
    }

    if(runningGame.isHider(player) || runningGame.isSeeker(player)) {
      printDeathMessage(event);
    }

    if (runningGame.isHider(player)) {
      runningGame.removeHider(player);

      if (runningGame.doHidersBecomeSeekers()) {
        runningGame.addSeeker(player);
      }
    }

    runningGame.performPlayerCheck();
  }

  /**
   * Print death message.
   *
   * @param event the event
   */
  private void printDeathMessage(PlayerDeathEvent event) {
    final Player died = event.getPlayer();
    final Entity killer = died.getKiller();

    final HideAndSeekPlayer diedPlayer = HideAndSeekPlayer.get(died);
    final Component diedDisplayName = Messages.displayName(diedPlayer);

    event.deathMessage(null);

    HideAndSeekGame runningGame = HideAndSeekManager.INSTANCE.getRunningGame();
    int hidersRemaining = runningGame != null ? runningGame.getHiders().size() : 0;

    Component hidersRemainingComponent = Component.text("Noch ", NamedTextColor.GRAY)
        .append(Component.text(hidersRemaining, NamedTextColor.YELLOW))
        .append(Component.text(" Verstecker verbleibend.", NamedTextColor.GRAY));

    if (killer != null) {
      final HideAndSeekPlayer killerPlayer = HideAndSeekPlayer.get(killer.getUniqueId());
      final Component killerDisplayName = Messages.displayName(killerPlayer);

      Bukkit.broadcast(Messages.prefix()
          .append(diedDisplayName)
          .append(Component.text(" wurde von ", NamedTextColor.GRAY))
          .append(killerDisplayName)
          .append(Component.text(" getötet. ", NamedTextColor.GRAY)).append(hidersRemainingComponent));
    } else {
      Bukkit.broadcast(Messages.prefix()
          .append(diedDisplayName)
          .append(Component.text(" ist gestorben. ", NamedTextColor.GRAY)).append(hidersRemainingComponent));
    }
  }

}
