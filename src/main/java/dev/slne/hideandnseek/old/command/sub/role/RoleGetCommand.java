package dev.slne.hideandnseek.old.command.sub.role;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.slne.hideandnseek.old.Messages;
import dev.slne.hideandnseek.old.player.HideAndSeekPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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


      player.sendMessage(Messages.prefix().append(Component.text(String.format("Der Spieler %s besitzt die Rolle %s", target.getName(), hnsTarget.getRole())).color(NamedTextColor.GREEN)));
    });
  }
}
