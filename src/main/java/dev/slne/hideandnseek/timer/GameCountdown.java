package dev.slne.hideandnseek.timer;

import dev.slne.hideandnseek.HideAndSeekEndReason;
import dev.slne.hideandnseek.Messages;
import dev.slne.hideandnseek.step.steps.IngameStep;
import dev.slne.hideandnseek.util.TimeUtil;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * The type Hide and seek timer.
 */
public class GameCountdown extends BukkitRunnable {

  private static final int[] ANNOUNCEMENTS = {
      3600,
      1800,
      900,
      600,
      300,
      240,
      180,
      120,
      60,
      30,
      15,
      10,
      5,
      4,
      3,
      2,
      1
  };

  private final IngameStep step;

  private long currentSeconds;

  /**
   * Instantiates a new Hide and seek timer.
   *
   * @param step     the step
   * @param timeUnit the time unit
   * @param maxTime  the max time
   */
  public GameCountdown(IngameStep step, Duration maxTime) {
    this.step = step;

    this.currentSeconds = maxTime.getSeconds();
  }

  @Override
  public void run() {
    if (currentSeconds <= 0) {
      step.end(HideAndSeekEndReason.TIME_UP);

      return;
    }

    if (Arrays.stream(ANNOUNCEMENTS).anyMatch(i -> i == currentSeconds)) {
      Bukkit.broadcast(Messages.prefix().append(Component.text("Das Spiel endet in ",
          NamedTextColor.GRAY)).append(
          TimeUtil.formatLongTimestamp(TimeUnit.SECONDS, currentSeconds, NamedTextColor.GOLD)));
    }

    Bukkit.getServer().sendActionBar(
        TimeUtil.formatTimestamp(TimeUnit.SECONDS, currentSeconds, NamedTextColor.GOLD));

    currentSeconds--;
  }
}
