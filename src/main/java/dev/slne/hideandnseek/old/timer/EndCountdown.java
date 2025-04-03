package dev.slne.hideandnseek.old.timer;

import dev.slne.hideandnseek.old.util.Continuation;
import dev.slne.hideandnseek.old.util.TimeUtil;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class EndCountdown extends AbstractCountdown{

  private Continuation continuation;

  public EndCountdown(@NotNull Duration startingTime) {
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
