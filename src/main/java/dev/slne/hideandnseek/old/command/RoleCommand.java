package dev.slne.hideandnseek.old.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.hideandnseek.old.command.sub.role.RoleGetCommand;
import dev.slne.hideandnseek.old.command.sub.role.RoleSetCommand;

public class RoleCommand extends CommandAPICommand {

  public RoleCommand(String commandName) {
    super(commandName);

    withPermission("hideandseek.command.role");

    withSubcommand(new RoleGetCommand("info"));
    withSubcommand(new RoleSetCommand("set"));
  }
}
