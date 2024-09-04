package dev.slne.hideandnseek.timer;

import dev.slne.hideandnseek.util.TimeUtil;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;

/**
 * The type Hide and seek end timer.
 */
public class EndCountdown extends AbstractCountdown {

  /**
   * Instantiates a new Hide and seek end timer.
   *
   * @param step     the step
   * @param timeUnit the time unit
   * @param maxTime  the max time
   */
  public EndCountdown(Duration startingTime) {
    super(startingTime);
  }

  @Override
  protected void onTick() {
    Bukkit.getServer().sendActionBar(
        TimeUtil.formatTimestamp(TimeUnit.SECONDS, getCurrentSeconds(), NamedTextColor.GOLD));
  }
}
