package dev.slne.hideandnseek.old.command.sub.role;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.slne.hideandnseek.old.Messages;
import dev.slne.hideandnseek.old.player.HideAndSeekPlayer;
import dev.slne.hideandnseek.old.role.Role;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class RoleSetCommand extends CommandAPICommand {

  public RoleSetCommand(String commandName) {
    super(commandName);

    withArguments(new PlayerArgument("target"));
    withArguments(new StringArgument("role").replaceSuggestions(ArgumentSuggestions.strings("seeker", "hider", "spectator")));

    executesPlayer((player, args) -> {
      Player target = args.getUnchecked("target");
      Role role = Role.getRole(args.getUnchecked("role"));
      HideAndSeekPlayer hnsTarget = HideAndSeekPlayer.get(target);

      if(role == null){
        throw CommandAPI.failWithString("Die Rolle wurde nicht gefunden.");
      }

      if(hnsTarget == null){
        throw CommandAPI.failWithString("Der Spieler wurde nicht gefunden.");
      }

      hnsTarget.setRole(role);

      player.sendMessage(Messages.prefix().append(Component.text(String.format("Der Spieler %s besitzt nun die Rolle %s", target.getName(), role)).color(NamedTextColor.GREEN)));
    });
  }
}
