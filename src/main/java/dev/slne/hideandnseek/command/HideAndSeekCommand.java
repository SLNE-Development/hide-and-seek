package dev.slne.hideandnseek.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.hideandnseek.command.sub.HideAndSeekBypassCommand;
import dev.slne.hideandnseek.command.sub.HideAndSeekForcestopCommand;
import dev.slne.hideandnseek.command.sub.HideAndSeekSetLobbyCommand;
import dev.slne.hideandnseek.command.sub.HideAndSeekSetSpawnCommand;
import dev.slne.hideandnseek.command.sub.HideAndSeekSettingsCommand;
import dev.slne.hideandnseek.command.sub.HideAndSeekStartCommand;

/**
 * The type Hide and seek command.
 */
public class HideAndSeekCommand extends CommandAPICommand {

  /**
   * Instantiates a new Hide and seek command.
   *
   * @param commandName the command name
   */
  public HideAndSeekCommand(String commandName) {
    super(commandName);

    withPermission("hideandseek.command.use");

    withAliases("hns");

    withSubcommand(new HideAndSeekSettingsCommand("setting"));
    withSubcommand(new HideAndSeekStartCommand("start"));
    withSubcommand(new HideAndSeekBypassCommand("bypass"));
    withSubcommand(new HideAndSeekSetSpawnCommand("setspawn"));
    withSubcommand(new HideAndSeekForcestopCommand("forcestop"));
    withSubcommand(new HideAndSeekSetLobbyCommand("setlobby"));
  }
}
