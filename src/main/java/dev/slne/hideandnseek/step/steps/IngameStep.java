package dev.slne.hideandnseek.step.steps;

import dev.slne.hideandnseek.GameSettings;
import dev.slne.hideandnseek.HideAndSeekEndReason;
import dev.slne.hideandnseek.HideAndSeekGame;
import dev.slne.hideandnseek.HideAndSeekGameState;
import dev.slne.hideandnseek.Messages;
import dev.slne.hideandnseek.player.HideAndSeekPlayer;
import dev.slne.hideandnseek.step.GameStep;
import dev.slne.hideandnseek.timer.GameCountdown;
import dev.slne.hideandnseek.util.Continuation;
import java.time.Duration;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Emitter;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;

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

  public IngameStep(HideAndSeekGame game, GameSettings gameSettings) {
    super(HideAndSeekGameState.INGAME);

    this.game = game;
    this.time = gameSettings.getGameDuration();
    this.world = gameSettings.getWorld();
    this.finalRadius = gameSettings.getFinalRadius();
    this.shrinkTime = gameSettings.getGameDuration();
  }

  @Override
  public void load(Continuation continuation) {
    countdown = new GameCountdown(time);
    continuation.resume();
  }

  @Override
  public void start(Continuation continuation) {
    TextComponent.Builder builder = Component.text(); // TODO: 06.09.2024 21:58 - extract to HiderPreparationCountdown#onEnd() ?

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

    runSync(() -> {
      Bukkit.getServer().playSound(Sound.sound()
          .type(org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL)
          .volume(.75f)
          .pitch(.75f)
          .source(Source.MASTER)
          .build(), Emitter.self());

      for (HideAndSeekPlayer seeker : game.getSeekers()) {
        seeker.teleportSpawn();
      }

      world.getWorldBorder().setSize(finalRadius * 2, shrinkTime.getSeconds());
    }).thenRun(() -> countdown.start(continuation));
  }

  @Override
  public void end(HideAndSeekEndReason reason, Continuation continuation) {
    final TextComponent.Builder builder = Component.text();

    builder.append(Messages.prefix()).appendNewline();
    builder.append(Component.text("-".repeat(20))).appendNewline();
    builder.append(Messages.prefix()).appendNewline();

    builder.append(Messages.prefix().append(Component.text("Das Spiel ist vorbei!")))
        .appendNewline();
    builder.append(Messages.prefix()).appendNewline();

    if (reason.equals(HideAndSeekEndReason.FORCED_END)) {
      builder.append(Messages.prefix().append(Component.text("Das Spiel wurde vorzeitig beendet.")))
          .appendNewline();
    } else if (reason.equals(HideAndSeekEndReason.TIME_UP) || reason.equals(
        HideAndSeekEndReason.HIDER_WIN)) {
      builder.append(Messages.prefix().append(Component.text("Die Verstecker haben gewonnen.")))
          .appendNewline();
    } else if (reason.equals(HideAndSeekEndReason.SEEKER_WIN)) {
      builder.append(Messages.prefix().append(Component.text("Die Sucher haben gewonnen.")))
          .appendNewline();
    }

    builder.append(Messages.prefix()).appendNewline();
    builder.append(Component.text("-".repeat(20))).appendNewline();
    builder.append(Messages.prefix());

    Bukkit.broadcast(builder.build());

    runSync(() -> {
      Bukkit.getServer().playSound(
          Sound.sound().type(org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL).volume(.75f).pitch(.75f)
              .source(Source.MASTER).build(), Emitter.self());

      final WorldBorder worldBorder = world.getWorldBorder();
      worldBorder.setSize((int) worldBorder.getSize());
    }).thenRun(() -> {
      System.err.println("Continuation resume 5165654");
      continuation.resume();
    }).exceptionally(ex -> {
      System.err.println("An error occurred while ending the game 5165654");
      return null;
    });
  }

  @Override
  public void interrupt() {
    countdown.interrupt();
  }
}
