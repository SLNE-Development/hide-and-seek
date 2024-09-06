package dev.slne.hideandnseek.timer;

import dev.slne.hideandnseek.util.Continuation;
import dev.slne.hideandnseek.util.TimeUtil;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;

/**
 * The type Hide and seek end timer.
 */
public class EndCountdown extends AbstractCountdown {

  private Continuation continuation;

  public EndCountdown(Duration startingTime) {
    super(startingTime);
  }

  @Override
  protected void onTick() {
    Bukkit.getServer().sendActionBar(
        TimeUtil.formatTimestamp(TimeUnit.SECONDS, getCurrentSeconds(), NamedTextColor.GOLD));
  }

  public void start(Continuation continuation) {
    this.continuation = continuation;
    start();
  }

  @Override
  protected void onEnd() {
    continuation.resume();
  }
}
