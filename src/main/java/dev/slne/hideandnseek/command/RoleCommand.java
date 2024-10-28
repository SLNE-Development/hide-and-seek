package dev.slne.hideandnseek.command;

import dev.jorel.commandapi.CommandAPICommand;

public class RoleCommand extends CommandAPICommand {

  public RoleCommand(String commandName) {
    super(commandName);

    withPermission("hideandseek.command.role");
  }
}
