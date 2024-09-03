package dev.slne.hideandnseek.timer;

import dev.slne.hideandnseek.HideAndSeekEndReason;
import dev.slne.hideandnseek.step.steps.EndingGameStep;
import dev.slne.hideandnseek.util.TimeUtil;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * The type Hide and seek end timer.
 */
public class EndCountdown extends BukkitRunnable {

  private final EndingGameStep step;
  private final long startingSeconds;

  private long currentSeconds;

  /**
   * Instantiates a new Hide and seek end timer.
   *
   * @param step     the step
   * @param timeUnit the time unit
   * @param maxTime  the max time
   */
  public EndCountdown(EndingGameStep step, TimeUnit timeUnit, long maxTime) {
    this.step = step;

    this.startingSeconds = timeUnit.toSeconds(maxTime);
    this.currentSeconds = startingSeconds;
  }

  @Override
  public void run() {
    if (currentSeconds <= 0) {
      step.end(HideAndSeekEndReason.TIME_UP);

      return;
    }

    Bukkit.getServer().sendActionBar(
        TimeUtil.formatTimestamp(TimeUnit.SECONDS, currentSeconds, NamedTextColor.GOLD));

    currentSeconds--;
  }
}
