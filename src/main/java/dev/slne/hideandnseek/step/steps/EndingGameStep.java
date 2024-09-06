package dev.slne.hideandnseek.step.steps;

import dev.slne.hideandnseek.HideAndSeekEndReason;
import dev.slne.hideandnseek.HideAndSeekGameState;
import dev.slne.hideandnseek.Messages;
import dev.slne.hideandnseek.step.GameStep;
import dev.slne.hideandnseek.GameData;
import dev.slne.hideandnseek.timer.EndCountdown;
import dev.slne.hideandnseek.util.Continuation;
import java.time.Duration;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Emitter;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;

/**
 * The type Ending game step.
 */
public class EndingGameStep extends GameStep { // TODO: 06.09.2024 21:48 - move to one class with Ingame Step?

  private final Duration endingTime;

  private EndCountdown countdown;

  /**
   * Instantiates a new Ending game step.
   *
   * @param reason   the reason
   * @param timeUnit the time unit
   * @param time     the time
   */
  public EndingGameStep(GameData gameData) {
    super(HideAndSeekGameState.ENDING);
    this.endingTime = gameData.getEndingTime();
  }


  @Override
  public void load(Continuation continuation) {
    countdown = new EndCountdown(endingTime);
    continuation.resume();
  }

  @Override
  public void start(Continuation continuation) {
    countdown.start(continuation);
  }

  @Override
  public void end(HideAndSeekEndReason reason, Continuation continuation) {
    TextComponent.Builder builder = Component.text();

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

    Bukkit.getServer().playSound(
        Sound.sound().type(org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL).volume(.75f).pitch(.75f)
            .source(Source.MASTER).build(), Emitter.self());
  }
}
