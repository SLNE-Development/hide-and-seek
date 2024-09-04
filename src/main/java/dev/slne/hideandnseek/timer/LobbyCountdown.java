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
 * The type Lobby timer.
 */
public class LobbyCountdown extends AbstractAnnounceCountdown {

  private static final int[] ANNOUNCEMENTS = {60, 30, 15, 10, 5, 4, 3, 2, 1};

  private Continuation continuation;

  public LobbyCountdown(Duration startTime) {
    super(startTime, ANNOUNCEMENTS);
  }

  public void start(Continuation continuation) {
    this.continuation = continuation;
    start();
  }

  @Override
  protected void onTick() {
    super.onTick();

    Bukkit.getServer().sendActionBar(
        TimeUtil.formatTimestamp(TimeUnit.SECONDS, getCurrentSeconds(), NamedTextColor.GOLD));
  }

  @Override
  protected void onEnd() {
    continuation.resume();
  }

  @Override
  protected void announce(int second) {
    Bukkit.broadcast(Messages.prefix()
        .append(Component.text("Das Spiel beginnt in ", NamedTextColor.GRAY))
        .append(
            TimeUtil.formatLongTimestamp(TimeUnit.SECONDS, getCurrentSeconds(),
                NamedTextColor.GOLD)));
  }
}
