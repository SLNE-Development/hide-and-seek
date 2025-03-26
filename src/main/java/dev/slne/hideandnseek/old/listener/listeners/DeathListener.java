package dev.slne.hideandnseek.old.listener.listeners;

import dev.slne.hideandnseek.old.HideAndSeekGame;
import dev.slne.hideandnseek.old.HideAndSeekManager;
import dev.slne.hideandnseek.old.Messages;
import dev.slne.hideandnseek.old.player.HideAndSeekPlayer;
import dev.slne.hideandnseek.old.role.Role;
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

    printDeathMessage(event);

    if (runningGame.isHider(player)) {
      if (runningGame.doHidersBecomeSeekers()) {
        player.setRole(Role.SEEKER);
        player.getPlayer().sendMessage(Messages.prefix().append(Component.text("Du bist nun Sucher!").color(NamedTextColor.GREEN)));
      }else{
        player.setRole(Role.SPECTATOR);

        if(!bukkitPlayer.hasPermission("hideandseek.bypass")) {
          bukkitPlayer.kick(Component.text("Du bist ausgeschieden!"));
        }else{
          player.getPlayer().sendMessage(Messages.prefix().append(Component.text("Du bist nun Zuschauer!").color(NamedTextColor.GREEN)));
        }
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
    int hidersRemaining = runningGame != null ? runningGame.getHiders().size() - 1 : 0;

    Component hidersRemainingComponent = Component.empty();

    if (diedPlayer.isHider() && runningGame.getGameState().isIngame()) {
       hidersRemainingComponent = Component.text("Noch ", NamedTextColor.GRAY)
              .append(Component.text(hidersRemaining, NamedTextColor.YELLOW))
              .append(Component.text(" Verstecker verbleibend.", NamedTextColor.GRAY));
    }

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
