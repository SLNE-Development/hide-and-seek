package dev.slne.hideandnseek.old;

import dev.slne.hideandnseek.old.player.HideAndSeekPlayer;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * The type Messages.
 */
@UtilityClass
public class Messages {

  /**
   * Display name component.
   *
   * @param player the player
   * @return the component
   */
  public Component displayName(HideAndSeekPlayer player) {
    final HideAndSeekGame runningGame = HideAndSeekManager.INSTANCE.getRunningGame();
    final boolean isSeeker = runningGame != null && runningGame.isSeeker(player);

    return Component.text(player.getPlayer().getName(),
        isSeeker ? NamedTextColor.AQUA : NamedTextColor.YELLOW);
  }

  /**
   * Prefix component.
   *
   * @return the component
   */
  public Component prefix() {
    return Component.text(">> ", NamedTextColor.DARK_GRAY)
        .append(Component.text("HideAndSeek", NamedTextColor.GOLD))
        .append(Component.text(" | ", NamedTextColor.DARK_GRAY));
  }
}
