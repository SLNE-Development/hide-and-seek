package dev.slne.hideandnseek;

import dev.slne.hideandnseek.player.HideAndSeekPlayer;
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
    final boolean isHider = runningGame != null && runningGame.isHider(player);

    return Component.text(player.getPlayer().getName(),
        isHider ? NamedTextColor.YELLOW
            : NamedTextColor.AQUA); // TODO: 04.09.2024 22:33 - just use team display name?
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
