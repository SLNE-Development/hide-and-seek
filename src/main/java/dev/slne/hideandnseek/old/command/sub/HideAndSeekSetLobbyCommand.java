package dev.slne.hideandnseek.old.command.sub;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.slne.hideandnseek.old.HideAndSeekManager;
import dev.slne.hideandnseek.old.Messages;
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

    withArguments(
        new LocationArgument("location", LocationType.BLOCK_POSITION, true),
        new IntegerArgument("radius")
    );

    executes((sender, args) -> {
      final Location location = args.getUnchecked("location");
      final int radius = args.getOrDefaultUnchecked("radius", 0);

      HideAndSeekManager.INSTANCE.setLobbyLocation(location);
      HideAndSeekManager.INSTANCE.setLobbyWorldBorderRadius(radius);

      sender.sendMessage(Messages.prefix().append(Component.text("Du hast die Lobby gesetzt.",
          NamedTextColor.GREEN)));
    });
  }
}
