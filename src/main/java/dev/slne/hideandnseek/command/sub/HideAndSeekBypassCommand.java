package dev.slne.hideandnseek.command.sub;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.hideandnseek.HideAndSeekManager;
import dev.slne.hideandnseek.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * The type Hide and seek bypass command.
 */
public class HideAndSeekBypassCommand extends CommandAPICommand {

  /**
   * Instantiates a new Hide and seek bypass command.
   *
   * @param commandName the command name
   */
  public HideAndSeekBypassCommand(String commandName) {
    super(commandName);

    withPermission("hideandseek.command.bypass");

    executesPlayer((player, args) -> {
      if (HideAndSeekManager.INSTANCE.getBypassing().contains(player)) {
        HideAndSeekManager.INSTANCE.getBypassing().remove(player);

        player.sendMessage(
            Messages.prefix().append(Component.text("Du bist nicht mehr im Bypass-Modus",
                NamedTextColor.GREEN)));
      } else {
        HideAndSeekManager.INSTANCE.getBypassing().add(player);

        player.sendMessage(Messages.prefix().append(Component.text("Du bist jetzt im Bypass-Modus",
            NamedTextColor.GREEN)));
      }
    });
  }
}
