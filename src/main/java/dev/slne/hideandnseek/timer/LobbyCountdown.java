package dev.slne.hideandnseek.timer;

import dev.slne.hideandnseek.HideAndSeek;
import dev.slne.hideandnseek.Messages;
import dev.slne.hideandnseek.step.GameStepManager.Continuation;
import dev.slne.hideandnseek.util.TimeUtil;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;

/**
 * The type Lobby timer.
 */
public class LobbyCountdown implements Runnable {

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

  private int taskId;
  private long currentSeconds;
  private Continuation continuation;

  /**
   * Instantiates a new Hide and seek end timer.
   *
   * @param step     the step
   * @param timeUnit the time unit
   * @param maxTime  the max time
   */
  public LobbyCountdown(Duration maxTime) {
    this.currentSeconds = maxTime.getSeconds();
  }

  @Override
  public void run() {
    if (currentSeconds <= 0) {
      continuation.resume();
      Bukkit.getScheduler().cancelTask(taskId);
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

  public void start(Continuation continuation) {
    this.continuation = continuation;
    this.taskId = Bukkit.getScheduler()
        .runTaskTimerAsynchronously(HideAndSeek.getInstance(), this, 0, 20).getTaskId();
  }
}
