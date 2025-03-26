package dev.slne.hideandnseek.old.command.sub;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.hideandnseek.old.HideAndSeekManager;
import dev.slne.hideandnseek.old.Messages;
import net.kyori.adventure.text.Component;

public class HideAndSeekSettingSaveCommand extends CommandAPICommand {

  public HideAndSeekSettingSaveCommand(String commandName) {
    super(commandName);

    withPermission("hideandseek.command.savesettings");

    executesPlayer((player, args) -> {
      HideAndSeekManager.INSTANCE.save();

      player.sendMessage(Messages.prefix().append(Component.text("Die Configuration wurde erfolgreich in die config gespeichert.")));
    });
  }
}
