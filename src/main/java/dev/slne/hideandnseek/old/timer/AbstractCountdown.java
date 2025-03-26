package dev.slne.hideandnseek.old.timer;

import dev.slne.hideandnseek.old.HideAndSeek;
import java.time.Duration;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class AbstractCountdown implements Runnable {

  private int taskId;
  private long currentSeconds;


  @Contract(pure = true)
  public AbstractCountdown(@NotNull Duration startingTime) {
    this.currentSeconds = startingTime.getSeconds();
  }

  @Override
  public final void run() {
    if (currentSeconds <= 0) {
      onEnd();
      Bukkit.getScheduler().cancelTask(taskId);
      return;
    }

    onTick();
    currentSeconds--;
  }

  @OverridingMethodsMustInvokeSuper
  public void start() {
    taskId = Bukkit.getScheduler()
        .runTaskTimerAsynchronously(HideAndSeek.getInstance(), this, 0, 20)
        .getTaskId();
  }

  protected void onEnd() {

  }

  protected void onTick() {

  }

  public final void interrupt() {
    Bukkit.getScheduler().cancelTask(taskId);
//    onEnd();
  }
}
