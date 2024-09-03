package dev.slne.hideandnseek.timer;

import dev.slne.hideandnseek.HideAndSeekEndReason;
import dev.slne.hideandnseek.Messages;
import dev.slne.hideandnseek.step.steps.PreparationStep;
import dev.slne.hideandnseek.util.TimeUtil;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * The type Hide and seek hider prep timer.
 */
public class HiderPreparationCountdown extends BukkitRunnable {

  private static final int[] ANNOUNCEMENTS = {60, 30, 15, 10, 5, 4, 3, 2, 1};

  private final PreparationStep lifecycle;
  private final long startingSeconds;

  private long currentSeconds;

  /**
   * Instantiates a new Hide and seek hider prep timer.
   *
   * @param step     the step
   * @param timeUnit the time unit
   * @param maxTime  the max time
   */
  public HiderPreparationCountdown(PreparationStep step, TimeUnit timeUnit,
      long maxTime) {
    this.lifecycle = step;
    this.startingSeconds = timeUnit.toSeconds(maxTime);
    this.currentSeconds = startingSeconds;
  }

  @Override
  public void run() {
    if (currentSeconds <= 0) {
      lifecycle.end(HideAndSeekEndReason.TIME_UP);

      return;
    }

    if (Arrays.stream(ANNOUNCEMENTS).anyMatch(i -> i == currentSeconds)) {
      Bukkit.broadcast(Messages.prefix().append(Component.text("Die Verstecker haben noch ",
              NamedTextColor.GRAY)).append(
              TimeUtil.formatLongTimestamp(TimeUnit.SECONDS, currentSeconds, NamedTextColor.GOLD))
          .append(Component.text(" Vorbereitungszeit!", NamedTextColor.GRAY)));
    }

    Bukkit.getServer().sendActionBar(
        TimeUtil.formatTimestamp(TimeUnit.SECONDS, currentSeconds, NamedTextColor.GOLD));

    currentSeconds--;
  }
}
