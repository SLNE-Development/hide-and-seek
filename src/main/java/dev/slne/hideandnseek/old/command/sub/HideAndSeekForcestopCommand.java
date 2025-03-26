package dev.slne.hideandnseek.old.command.sub;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.hideandnseek.old.HideAndSeekGame;
import dev.slne.hideandnseek.old.HideAndSeekManager;
import dev.slne.hideandnseek.old.Messages;
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

      runningGame.forcestop().thenRun(() -> player.sendMessage(Messages.prefix()
          .append(Component.text("Das Spiel wurde gestoppt.", NamedTextColor.GREEN))));
    });
  }
}
