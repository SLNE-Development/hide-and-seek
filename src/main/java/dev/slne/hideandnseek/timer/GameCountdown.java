package dev.slne.hideandnseek.timer;

import dev.slne.hideandnseek.Messages;
import dev.slne.hideandnseek.util.Continuation;
import dev.slne.hideandnseek.util.TimeUtil;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;

/**
 * The type Hide and seek timer.
 */
public class GameCountdown extends AbstractAnnounceCountdown {

  private static final int[] ANNOUNCEMENTS = {3600, 1800, 900, 600, 300, 240, 180, 120, 60, 30, 15,
      10, 5, 4, 3, 2, 1};

  private Continuation continuation;

  public GameCountdown(Duration startingTime) {
    super(startingTime, ANNOUNCEMENTS);
  }

  @Override
  protected void onTick() {
    super.onTick();

    Bukkit.getServer().sendActionBar(
        TimeUtil.formatTimestamp(TimeUnit.SECONDS, getCurrentSeconds(), NamedTextColor.GOLD));
  }

  @Override
  protected void announce(int second) {
    Bukkit.broadcast(Messages.prefix()
        .append(Component.text("Das Spiel endet in ", NamedTextColor.GRAY))
        .append(TimeUtil.formatLongTimestamp(TimeUnit.SECONDS, getCurrentSeconds(),
            NamedTextColor.GOLD)));
  }

  public void start(Continuation continuation) {
    this.continuation = continuation;
    start();
  }
}
