package dev.slne.hideandnseek.listener.listeners;

import dev.slne.hideandnseek.HideAndSeekGame;
import dev.slne.hideandnseek.HideAndSeekManager;
import dev.slne.hideandnseek.Messages;
import dev.slne.hideandnseek.player.HideAndSeekPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnlineListener implements Listener {

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    final Player player = event.getPlayer();
    final HideAndSeekGame runningGame = HideAndSeekManager.INSTANCE.getRunningGame();

    event.joinMessage(Messages.prefix()
        .append(Messages.displayName(HideAndSeekPlayer.get(player)))
        .append(Component.text(" hat das Spiel betreten.", NamedTextColor.GRAY)));

    if (runningGame != null && runningGame.getGameState().isIngame()) {
      player.setGameMode(GameMode.SPECTATOR);

      player.sendMessage(Messages.prefix()
          .append(Component.text(
              "Du hast das Spiel betreten, während es bereits läuft. Du bist nun ein Zuschauer. Bitte versetz dich NICHT manuell in einen anderen Spielmodus.",
              NamedTextColor.RED, TextDecoration.BOLD)));
    }
  }

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

    runningGame.removeSeeker(hideAndSeekPlayer);
    runningGame.removeHider(hideAndSeekPlayer);
    runningGame.performPlayerCheck();
  }

}
