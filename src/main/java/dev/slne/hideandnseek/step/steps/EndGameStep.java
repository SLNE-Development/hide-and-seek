package dev.slne.hideandnseek.step.steps;

import dev.slne.hideandnseek.GameSettings;
import dev.slne.hideandnseek.HideAndSeekEndReason;
import dev.slne.hideandnseek.HideAndSeekGame;
import dev.slne.hideandnseek.HideAndSeekGameState;
import dev.slne.hideandnseek.HideAndSeekManager;
import dev.slne.hideandnseek.player.HideAndSeekPlayer;
import dev.slne.hideandnseek.step.GameStep;
import dev.slne.hideandnseek.timer.EndCountdown;
import dev.slne.hideandnseek.util.Continuation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EndGameStep extends GameStep {

  private final HideAndSeekGame game;
  private final GameSettings settings;
  private EndCountdown endCountdown;

  public EndGameStep(HideAndSeekGame game, GameSettings settings) {
    super(HideAndSeekGameState.END);
    this.game = game;
    this.settings = settings;
  }

  @Override
  public void load(Continuation continuation) {
    endCountdown = new EndCountdown(settings.getEndDuration());
    continuation.resume();
  }

  @Override
  public void start(Continuation continuation) {
    super.start(continuation);
    game.stop(HideAndSeekEndReason.TIME_UP, false);
  }

  @Override
  public void end(HideAndSeekEndReason reason, Continuation continuation) {
    endCountdown.start(continuation);
  }

  @Override
  public void reset(Continuation continuation) {
    runSync(() -> {
      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        if (!onlinePlayer.hasPermission("hideandseek.kick.bypass")) {
          onlinePlayer.kick(Component.text("Das Spiel wurde beendet.", NamedTextColor.RED));
        }

        onlinePlayer.getInventory().clear();
        onlinePlayer.setVisibleByDefault(true);
        onlinePlayer.teleportAsync(HideAndSeekManager.INSTANCE.getLobbyLocation());

        HideAndSeekPlayer.get(onlinePlayer).prepareForGame();
      }
    }).thenRun(continuation::resume);
  }
}
