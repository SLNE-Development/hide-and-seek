package dev.slne.hideandnseek.old.listener.listeners;

import dev.slne.hideandnseek.old.HideAndSeek;
import dev.slne.hideandnseek.old.HideAndSeekGame;
import dev.slne.hideandnseek.old.HideAndSeekManager;
import dev.slne.hideandnseek.old.Messages;
import dev.slne.hideandnseek.old.player.HideAndSeekPlayer;
import dev.slne.hideandnseek.old.role.Role;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * The type Connection listener.
 */
public class ConnectionListener implements Listener {

  /**
   * On join.
   *
   * @param event the event
   */
  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    final Player player = event.getPlayer();
    HideAndSeekPlayer hideAndSeekPlayer = HideAndSeekPlayer.get(player);
    final HideAndSeekGame runningGame = HideAndSeekManager.INSTANCE.getRunningGame();

    event.joinMessage(Messages.prefix()
        .append(Messages.displayName(HideAndSeekPlayer.get(player)))
        .append(Component.text(" hat das Spiel betreten.", NamedTextColor.GRAY)));

    player.teleportAsync(
        HideAndSeekManager.INSTANCE.getGameSettings().getWorld().getSpawnLocation());
    player.setVisibleByDefault(true);

    hideAndSeekPlayer.prepareForGame();

    if (runningGame != null && runningGame.getGameState().isIngame()) {
      player.setGameMode(GameMode.SPECTATOR);

      player.sendMessage(Messages.prefix()
          .append(Component.text(
              "Du hast das Spiel betreten, während es bereits läuft. Du bist nun ein Zuschauer. Bitte versetz dich NICHT manuell in einen anderen Spielmodus.",
              NamedTextColor.RED, TextDecoration.BOLD)));
    }

    Bukkit.getScheduler()
        .runTaskLater(HideAndSeek.getInstance(), () -> player.getInventory().clear(), 5L);
  }

  /**
   * On quit.
   *
   * @param event the event
   */
  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    final HideAndSeekGame runningGame = HideAndSeekManager.INSTANCE.getRunningGame();
    final Player player = event.getPlayer();
    final HideAndSeekPlayer hideAndSeekPlayer = HideAndSeekPlayer.get(player);

    event.quitMessage(Messages.prefix()
        .append(Messages.displayName(hideAndSeekPlayer))
        .append(Component.text(" hat das Spiel verlassen.", NamedTextColor.GRAY)));

    if (runningGame == null) {
      return;
    }

    player.setFlying(false);
    player.setAllowFlight(false);

    hideAndSeekPlayer.setRole(Role.UNDEFINED);
    runningGame.performPlayerCheck();
  }

}
