package dev.slne.hideandnseek.util;

import java.util.concurrent.TimeUnit;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

/**
 * The type Time util.
 */
@UtilityClass
public class TimeUtil {

  /**
   * Format long timestamp component.
   *
   * @param timeUnit  the time unit
   * @param time      the time
   * @param textColor the text color
   * @return the component
   */
  public Component formatLongTimestamp(@NotNull TimeUnit timeUnit, long time, TextColor textColor) {
    final long hours = timeUnit.toHours(time);
    final long minutes = timeUnit.toMinutes(time) % 60;
    final long seconds = timeUnit.toSeconds(time) % 60;

    String formatted = "";

    if (hours > 0) {
      formatted += hours + " Stunde" + (hours > 1 ? "n" : "") + " ";
    }

    if (minutes > 0) {
      formatted += minutes + " Minute" + (minutes > 1 ? "n" : "") + " ";
    }

    if (seconds > 0) {
      formatted += seconds + " Sekunde" + (seconds > 1 ? "n" : "") + " ";
    }

    return Component.text(formatted.trim(), textColor);
  }

  /**
   * Format timestamp component.
   *
   * @param timeUnit  the time unit
   * @param time      the time
   * @param textColor the text color
   * @return the component
   */
  public Component formatTimestamp(@NotNull TimeUnit timeUnit, long time, TextColor textColor) {
    long hours = timeUnit.toHours(time);
    long minutes = timeUnit.toMinutes(time) % 60;
    long seconds = timeUnit.toSeconds(time) % 60;

    return Component.text("%1$02d:%2$02d:%3$02d".formatted(hours, minutes, seconds), textColor);
  }
}
