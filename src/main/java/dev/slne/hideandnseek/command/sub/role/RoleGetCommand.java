package dev.slne.hideandnseek.command.sub.role;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.slne.hideandnseek.HideAndSeekGame;
import dev.slne.hideandnseek.HideAndSeekManager;
import dev.slne.hideandnseek.Messages;
import dev.slne.hideandnseek.player.HideAndSeekPlayer;
import dev.slne.hideandnseek.role.Role;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class RoleGetCommand extends CommandAPICommand {

  public RoleGetCommand(String commandName) {
    super(commandName);

    withArguments(new PlayerArgument("target"));

    executesPlayer((player, args) -> {
      Player target = args.getUnchecked("target");
      HideAndSeekPlayer hnsTarget = HideAndSeekPlayer.get(target);

      if(hnsTarget == null){
        throw CommandAPI.failWithString("Der Spieler wurde nicht gefunden.");
      }


      player.sendMessage(Messages.prefix().append(Component.text(String.format("Der Spieler %s besitzt die Rolle %s", target.getName(), hnsTarget.getRole()))));
    });
  }
}
