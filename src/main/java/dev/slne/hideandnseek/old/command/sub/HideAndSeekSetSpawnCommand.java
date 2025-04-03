package dev.slne.hideandnseek.old.command.sub;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.arguments.RotationArgument;
import dev.jorel.commandapi.wrappers.Rotation;
import dev.slne.hideandnseek.old.HideAndSeekManager;
import dev.slne.hideandnseek.old.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;

/**
 * The type Hide and seek set spawn command.
 */
public class HideAndSeekSetSpawnCommand extends CommandAPICommand {

  /**
   * Instantiates a new Hide and seek set spawn command.
   *
   * @param commandName the command name
   */
  public HideAndSeekSetSpawnCommand(String commandName) {
    super(commandName);

    withPermission("hideandseek.command.setspawn");

    withArguments(new LocationArgument("location", LocationType.BLOCK_POSITION, true));
    withOptionalArguments(new RotationArgument("rotation"));

    executesPlayer((player, args) -> {
      final Location location = args.getUnchecked("location");
      final Rotation rotation = args.getOrDefaultUnchecked("include_yaw_pitch", new Rotation(0, 0));

      assert location != null;

      location.setYaw(rotation.getYaw());
      location.setPitch(rotation.getPitch());

      HideAndSeekManager.INSTANCE.setSpawnLocation(location);

      player.sendMessage(Messages.prefix().append(Component.text("Du hast den Spawn gesetzt.",
          NamedTextColor.GREEN)));
    });
  }
}
