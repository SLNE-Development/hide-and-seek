package dev.slne.hideandnseek.old.command.sub;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.hideandnseek.old.HideAndSeekManager;
import dev.slne.hideandnseek.old.Messages;
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
      if (HideAndSeekManager.INSTANCE.isBypassing(player)) {
        HideAndSeekManager.INSTANCE.removeBypassing(player);

        player.sendMessage(
            Messages.prefix().append(Component.text("Du bist nicht mehr im Bypass-Modus",
                NamedTextColor.GREEN)));
      } else {
        HideAndSeekManager.INSTANCE.addBypassing(player);

        player.sendMessage(Messages.prefix().append(Component.text("Du bist jetzt im Bypass-Modus",
            NamedTextColor.GREEN)));
      }
    });
  }
}
