package dev.slne.hideandnseek.command.sub;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.hideandnseek.HideAndSeekEndReason;
import dev.slne.hideandnseek.HideAndSeekGame;
import dev.slne.hideandnseek.HideAndSeekManager;
import dev.slne.hideandnseek.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * The type Hide and seek forcestop command.
 */
public class HideAndSeekForcestopCommand extends CommandAPICommand {

  /**
   * Instantiates a new Hide and seek forcestop command.
   *
   * @param commandName the command name
   */
  public HideAndSeekForcestopCommand(String commandName) {
    super(commandName);

    withPermission("hideandseek.command.forcestop");

    executesPlayer((player, args) -> {
      HideAndSeekGame runningGame = HideAndSeekManager.INSTANCE.getRunningGame();

      if (runningGame == null) {
        throw CommandAPI.failWithString("Es wurde noch kein Spiel erstellt.");
      }

      runningGame.end(HideAndSeekEndReason.FORCED_END); // TODO: 04.09.2024 22:04 - stop current running step
      HideAndSeekManager.INSTANCE.setRunningGame(null);

      player.sendMessage(Messages.prefix().append(Component.text("Das Spiel wurde gestoppt.",
          NamedTextColor.GREEN)));
    });
  }
}
