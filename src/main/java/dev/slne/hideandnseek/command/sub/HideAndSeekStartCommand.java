package dev.slne.hideandnseek.command.sub;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.hideandnseek.HideAndSeekGame;
import dev.slne.hideandnseek.HideAndSeekManager;
import dev.slne.hideandnseek.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * The type Hide and seek start command.
 */
public class HideAndSeekStartCommand extends CommandAPICommand {

  /**
   * Instantiates a new Hide and seek start command.
   *
   * @param commandName the command name
   */
  public HideAndSeekStartCommand(String commandName) {
    super(commandName);

    withPermission("hideandseek.command.start");

    executesPlayer((player, args) -> {
      HideAndSeekGame runningGame = HideAndSeekManager.INSTANCE.getRunningGame();

      if (runningGame != null) {
        throw CommandAPI.failWithString("Es läuft bereits ein Spiel.");
      }

      final HideAndSeekGame game = new HideAndSeekGame(
          HideAndSeekManager.INSTANCE.getGameSettings());
      HideAndSeekManager.INSTANCE.setRunningGame(game);

      player.sendMessage(Messages.prefix()
          .append(Component.text("Das Spiel wird vorbereitet...", NamedTextColor.GREEN)));

      game.prepare()
          .thenRun(() -> player.sendMessage(Messages.prefix()
              .append(Component.text("Das Spiel wurde vorbereitet und wird jetzt gestartet.",
                  NamedTextColor.GREEN))))
          .thenRun(game::start);
    });
  }
}
