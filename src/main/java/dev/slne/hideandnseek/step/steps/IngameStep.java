package dev.slne.hideandnseek.step.steps;

import dev.slne.hideandnseek.HideAndSeekEndReason;
import dev.slne.hideandnseek.HideAndSeekGame;
import dev.slne.hideandnseek.HideAndSeekGameState;
import dev.slne.hideandnseek.Messages;
import dev.slne.hideandnseek.player.HideAndSeekPlayer;
import dev.slne.hideandnseek.step.GameStep;
import dev.slne.hideandnseek.util.Continuation;
import dev.slne.hideandnseek.GameData;
import dev.slne.hideandnseek.timer.GameCountdown;
import java.time.Duration;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Emitter;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 * The type Ingame step.
 */
public class IngameStep extends GameStep {

  private final HideAndSeekGame game;
  private final Duration time;
  private final Duration shrinkTime;
  private final World world;
  private final int finalRadius;

  private GameCountdown countdown;

  /**
   * Instantiates a new Ingame step.
   *
   * @param game           the game
   * @param timeUnit       the time unit
   * @param time           the time
   * @param world          the world
   * @param finalRadius    the final radius
   * @param shrinkTimeUnit the shrink time unit
   * @param shrinkTime     the shrink time
   */
  public IngameStep(HideAndSeekGame game, GameData gameData) {
    super(HideAndSeekGameState.INGAME);

    this.game = game;
    this.time = gameData.getGameDuration();
    this.world = gameData.getWorld();
    this.finalRadius = gameData.getInitialRadius();
    this.shrinkTime = gameData.getShrinkTime();
  }

  @Override
  public void load(Continuation continuation) {
    countdown = new GameCountdown(time);
    continuation.resume();
  }

  @Override
  public void start(Continuation continuation) {
    TextComponent.Builder builder = Component.text();

    builder.append(Messages.prefix()).appendNewline();
    builder.append(Component.text("-".repeat(20))).appendNewline();
    builder.append(Messages.prefix()).appendNewline();

    builder.append(
            Messages.prefix().append(Component.text("Die Vorbereitungszeit ist abgelaufen")))
        .appendNewline();
    builder.append(Messages.prefix()).appendNewline();

    builder.append(Component.text("-".repeat(20))).appendNewline();
    builder.append(Messages.prefix());

    Bukkit.broadcast(builder.build());

    Bukkit.getServer().playSound(
        Sound.sound().type(org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL).volume(.75f).pitch(.75f)
            .source(Source.MASTER).build(), Emitter.self());

    for (HideAndSeekPlayer seeker : game.getSeekers()) {
      seeker.prepareForGame();
      seeker.teleportSpawn();
    }

    world.getWorldBorder().setSize(finalRadius * 2, shrinkTime.getSeconds());
    countdown.start();
    continuation.resume();
  }
}
