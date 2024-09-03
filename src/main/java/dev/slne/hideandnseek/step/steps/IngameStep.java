package dev.slne.hideandnseek.step.steps;

import dev.slne.hideandnseek.HideAndSeek;
import dev.slne.hideandnseek.HideAndSeekEndReason;
import dev.slne.hideandnseek.HideAndSeekGame;
import dev.slne.hideandnseek.HideAndSeekGameState;
import dev.slne.hideandnseek.Messages;
import dev.slne.hideandnseek.player.HideAndSeekPlayer;
import dev.slne.hideandnseek.step.GameStep;
import dev.slne.hideandnseek.timer.GameCountdown;
import java.util.concurrent.TimeUnit;
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
public class IngameStep implements GameStep {

  private final HideAndSeekGame game;
  private final TimeUnit timeUnit;
  private final long time;
  private final World world;
  private final int finalRadius;
  private final TimeUnit shrinkTimeUnit;
  private final long shrinkTime;

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
  public IngameStep(HideAndSeekGame game, TimeUnit timeUnit, long time, World world,
      int finalRadius, TimeUnit shrinkTimeUnit, long shrinkTime) {
    this.game = game;
    this.timeUnit = timeUnit;
    this.time = time;
    this.world = world;
    this.finalRadius = finalRadius;
    this.shrinkTimeUnit = shrinkTimeUnit;
    this.shrinkTime = shrinkTime;
  }

  @Override
  public HideAndSeekGameState getGameState() {
    return HideAndSeekGameState.INGAME;
  }

  @Override
  public void load() {
    countdown = new GameCountdown(this, timeUnit, time);
  }

  @Override
  public void start() {
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

    world.getWorldBorder().setSize(finalRadius * 2, shrinkTimeUnit.toSeconds(shrinkTime));
    countdown.runTaskTimer(HideAndSeek.getInstance(), 0, 20);
  }

  @Override
  public void end(HideAndSeekEndReason reason) {

  }

  @Override
  public void reset() {
  }
}
