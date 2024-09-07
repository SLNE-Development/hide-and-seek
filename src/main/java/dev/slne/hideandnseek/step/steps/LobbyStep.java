package dev.slne.hideandnseek.step.steps;

import dev.jorel.commandapi.wrappers.Location2D;
import dev.slne.hideandnseek.GameSettings;
import dev.slne.hideandnseek.HideAndSeekGame;
import dev.slne.hideandnseek.HideAndSeekGameState;
import dev.slne.hideandnseek.HideAndSeekManager;
import dev.slne.hideandnseek.step.GameStep;
import dev.slne.hideandnseek.timer.LobbyCountdown;
import dev.slne.hideandnseek.util.Continuation;
import org.bukkit.WorldBorder;
import org.jetbrains.annotations.NotNull;

/**
 * The type Hide and seek lobby step.
 */
public class LobbyStep extends GameStep {

  private final HideAndSeekGame game;
  private LobbyCountdown countdown;


  private final GameSettings gameSettings;

  public LobbyStep(HideAndSeekGame game, GameSettings gameSettings) {
    super(HideAndSeekGameState.LOBBY);

    this.game = game;
    this.gameSettings = gameSettings;
  }

  @Override
  public void load(@NotNull Continuation continuation) {
    countdown = new LobbyCountdown(gameSettings.getLobbyTime());

    game.teleportLobby()
        .thenCompose(runSyncF(() -> {
          final WorldBorder worldBorder = gameSettings.getWorld().getWorldBorder();
          final Location2D center = gameSettings.getWorldBorderCenter();

          worldBorder.setCenter(center.getX(), center.getZ());
          worldBorder.setSize(HideAndSeekManager.INSTANCE.getLobbyWorldBorderRadius() * 2);
          worldBorder.setDamageAmount(gameSettings.getWorldBorderDamageAmount());
          worldBorder.setDamageBuffer(gameSettings.getWorldBorderDamageBuffer());
          worldBorder.setWarningDistance(0);
          worldBorder.setWarningTime(5);
        }))
        .thenRun(continuation::resume);
  }

  @Override
  public void start(Continuation continuation) {
    countdown.start(continuation);
  }

  @Override
  public void interrupt() {
    countdown.interrupt();
  }
}
