package dev.slne.hideandnseek;

import dev.slne.hideandnseek.player.HideAndSeekPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * The type Messages.
 */
public class Messages {

  /**
   * Display name component.
   *
   * @param player the player
   * @return the component
   */
  public static Component displayName(HideAndSeekPlayer player) {
    HideAndSeekGame runningGame = HideAndSeekManager.INSTANCE.getRunningGame();
    boolean isHider = runningGame != null && runningGame.isHider(player);

    return Component.text(player.getPlayer().getName(),
        isHider ? NamedTextColor.AQUA : NamedTextColor.GRAY);
  }

  /**
   * Prefix component.
   *
   * @return the component
   */
  public static Component prefix() {
    return Component.text(">> ", NamedTextColor.DARK_GRAY)
        .append(Component.text("HideAndSeek", NamedTextColor.GOLD))
        .append(Component.text(" | ", NamedTextColor.DARK_GRAY));
  }
}
