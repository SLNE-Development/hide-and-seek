package dev.slne.hideandnseek.timer;

import dev.slne.hideandnseek.HideAndSeekEndReason;
import dev.slne.hideandnseek.Messages;
import dev.slne.hideandnseek.step.steps.LobbyStep;
import dev.slne.hideandnseek.util.TimeUtil;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * The type Lobby timer.
 */
public class LobbyCountdown extends BukkitRunnable {

  private static final int[] ANNOUNCEMENTS = {
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

  private final LobbyStep lifecycle;
  private final long startingSeconds;

  private long currentSeconds;

  /**
   * Instantiates a new Hide and seek end timer.
   *
   * @param step     the step
   * @param timeUnit the time unit
   * @param maxTime  the max time
   */
  public LobbyCountdown(LobbyStep step, TimeUnit timeUnit, long maxTime) {
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
      Bukkit.broadcast(Messages.prefix().append(Component.text("Das Spiel beginnt in ",
          NamedTextColor.GRAY)).append(
          TimeUtil.formatLongTimestamp(TimeUnit.SECONDS, currentSeconds, NamedTextColor.GOLD)));
    }

    Bukkit.getServer().sendActionBar(
        TimeUtil.formatTimestamp(TimeUnit.SECONDS, currentSeconds, NamedTextColor.GOLD));

    currentSeconds--;
  }
}
