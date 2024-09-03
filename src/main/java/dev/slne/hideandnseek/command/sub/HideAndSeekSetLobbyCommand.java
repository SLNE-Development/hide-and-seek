package dev.slne.hideandnseek.command.sub;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.slne.hideandnseek.HideAndSeekManager;
import dev.slne.hideandnseek.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;

/**
 * The type Hide and seek set spawn command.
 */
public class HideAndSeekSetLobbyCommand extends CommandAPICommand {

  /**
   * Instantiates a new Hide and seek set spawn command.
   *
   * @param commandName the command name
   */
  public HideAndSeekSetLobbyCommand(String commandName) {
    super(commandName);

    withPermission("hideandseek.command.setspawn");

    withArguments(new IntegerArgument("radius"));
    withArguments(new IntegerArgument("countdown"));

    executesPlayer((player, args) -> {
      int radius = args.getOrDefaultUnchecked("radius", 0);
      int countdown = args.getOrDefaultUnchecked("countdown", 0);

      Location playerLocation = player.getLocation().clone();

      HideAndSeekManager.INSTANCE.setLobbyLocation(playerLocation);
      HideAndSeekManager.INSTANCE.setLobbyWorldBorderRadius(radius);

      player.sendMessage(Messages.prefix().append(Component.text("Du hast die Lobby gesetzt.",
          NamedTextColor.GREEN)));
    });
  }
}
