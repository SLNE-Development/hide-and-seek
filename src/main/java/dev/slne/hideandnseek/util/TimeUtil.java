package dev.slne.hideandnseek.util;

import java.util.concurrent.TimeUnit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

/**
 * The type Time util.
 */
public class TimeUtil {

  /**
   * Format long timestamp component.
   *
   * @param timeUnit  the time unit
   * @param time      the time
   * @param textColor the text color
   * @return the component
   */
  public static Component formatLongTimestamp(TimeUnit timeUnit, long time, TextColor textColor) {
    long hours = timeUnit.toHours(time);
    long minutes = timeUnit.toMinutes(time) % 60;
    long seconds = timeUnit.toSeconds(time) % 60;

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

    return Component.text(formatted, textColor);
  }

  /**
   * Format timestamp component.
   *
   * @param timeUnit  the time unit
   * @param time      the time
   * @param textColor the text color
   * @return the component
   */
  public static Component formatTimestamp(TimeUnit timeUnit, long time, TextColor textColor) {
    long hours = timeUnit.toHours(time);
    long minutes = timeUnit.toMinutes(time) % 60;
    long seconds = timeUnit.toSeconds(time) % 60;

    return Component.text("%1$02d:%2$02d:%3$02d".formatted(hours, minutes, seconds), textColor);
  }

}
