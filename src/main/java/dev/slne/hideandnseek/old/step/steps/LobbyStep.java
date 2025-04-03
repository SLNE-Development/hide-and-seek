package dev.slne.hideandnseek.old.step.steps;

import dev.jorel.commandapi.wrappers.Location2D;
import dev.slne.hideandnseek.old.GameSettings;
import dev.slne.hideandnseek.old.HideAndSeekGame;
import dev.slne.hideandnseek.old.HideAndSeekGameState;
import dev.slne.hideandnseek.old.HideAndSeekManager;
import dev.slne.hideandnseek.old.step.GameStep;
import dev.slne.hideandnseek.old.timer.LobbyCountdown;
import dev.slne.hideandnseek.old.util.Continuation;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.GameRule;
import org.bukkit.WorldBorder;
import org.jetbrains.annotations.NotNull;

/**
 * The type Hide and seek lobby step.
 */
public class LobbyStep extends GameStep {

  private static final ComponentLogger LOGGER = ComponentLogger.logger(LobbyStep.class);

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

    runSync(() -> {
      game.teleportLobby();
      
      final WorldBorder worldBorder = gameSettings.getWorld().getWorldBorder();
      final Location2D center = gameSettings.getWorldBorderCenter();

      worldBorder.setCenter(center.getX(), center.getZ());
      worldBorder.setSize(HideAndSeekManager.INSTANCE.getLobbyWorldBorderRadius() * 2);
      worldBorder.setDamageAmount(gameSettings.getWorldBorderDamageAmount());
      worldBorder.setDamageBuffer(gameSettings.getWorldBorderDamageBuffer());
      worldBorder.setWarningDistance(0);
      worldBorder.setWarningTime(5);

      worldBorder.getWorld().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
    })
        .thenRun(continuation::resume)
        .exceptionally(exception -> {
          LOGGER.error("Error while loading lobby", exception);
          return null;
        });
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
