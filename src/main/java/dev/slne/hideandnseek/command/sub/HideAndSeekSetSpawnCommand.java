package dev.slne.hideandnseek.command.sub;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.slne.hideandnseek.HideAndSeekManager;
import dev.slne.hideandnseek.Messages;
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

    withOptionalArguments(new BooleanArgument("include_yaw_pitch"));

    executesPlayer((player, args) -> {
      boolean includeYawPitch = args.getOrDefaultUnchecked("include_yaw_pitch", true);

      Location playerLocation = player.getLocation().clone();

      if (!includeYawPitch) {
        playerLocation.setYaw(0);
        playerLocation.setPitch(0);
      }

      HideAndSeekManager.INSTANCE.setSpawnLocation(playerLocation);

      player.sendMessage(Messages.prefix().append(Component.text("Du hast den Spawn gesetzt.",
          NamedTextColor.GREEN)));
    });
  }
}
