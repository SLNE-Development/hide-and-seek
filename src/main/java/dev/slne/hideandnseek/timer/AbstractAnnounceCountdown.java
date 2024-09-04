package dev.slne.hideandnseek.timer;

import java.time.Duration;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractAnnounceCountdown extends AbstractCountdown{

  private final int[] announcements;

  public AbstractAnnounceCountdown(@NotNull Duration startingTime, int[] announcements) {
    super(startingTime);
    this.announcements = announcements;
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  protected void onTick() {
    for (final int announcement : announcements) {
      if (getCurrentSeconds() == announcement) {
        announce(announcement);
      }
    }
  }

  protected abstract void announce(int second);
}
