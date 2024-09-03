package dev.slne.hideandnseek.command.sub;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.TimeArgument;
import dev.slne.hideandnseek.HideAndSeek;
import dev.slne.hideandnseek.HideAndSeekGame;
import dev.slne.hideandnseek.HideAndSeekManager;
import dev.slne.hideandnseek.Messages;
import dev.slne.hideandnseek.player.HideAndSeekPlayer;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * The type Hide and seek create command.
 */
public class HideAndSeekCreateCommand extends CommandAPICommand {

  /**
   * Instantiates a new Hide and seek create command.
   *
   * @param commandName the command name
   */
  public HideAndSeekCreateCommand(String commandName) {
    super(commandName);

    withPermission("hideandseek.command.create");

    withArguments(new TimeArgument("seek_time"));
    withArguments(new TimeArgument("hider_lead_time"));
    withArguments(new BooleanArgument("hiders_become_seekers"));
    withArguments(new IntegerArgument("initial_radius"));
    withArguments(new IntegerArgument("final_radius"));
    withArguments(new TimeArgument("shrink_time"));

    withOptionalArguments(new PlayerArgument("initial_seeker"));
    withOptionalArguments(new IntegerArgument("damage_amount"));
    withOptionalArguments(new IntegerArgument("damage_buffer"));

    executesPlayer((player, args) -> {
      int seekTimeTicks = args.getOrDefaultUnchecked("seek_time", 0);
      int hiderLeadTimeTicks = args.getOrDefaultUnchecked("hider_lead_time", 0);
      boolean hidersBecomeSeekers = args.getOrDefaultUnchecked("hiders_become_seekers", true);

      int initialRadius = args.getOrDefaultUnchecked("initial_radius", 0);
      int finalRadius = args.getOrDefaultUnchecked("final_radius", 1);
      long shrinkTimeTicks = args.getOrDefaultUnchecked("shrink_time", 0);

      int damageAmount = args.getOrDefaultUnchecked("damage_amount", 1);
      int damageBuffer = args.getOrDefaultUnchecked("damage_buffer", 0);
      Player initialSeeker = args.getUnchecked("initial_seeker");
      HideAndSeekPlayer initialSeekerPlayer = HideAndSeekPlayer.get(initialSeeker);

      HideAndSeekGame runningGame = HideAndSeekManager.INSTANCE.getRunningGame();

      if (runningGame != null) {
        throw CommandAPI.failWithString("Es läuft bereits ein Spiel");
      }

      Location lobbyLocation = HideAndSeekManager.INSTANCE.getLobbyLocation();
      int lobbyRadius = HideAndSeekManager.INSTANCE.getLobbyWorldBorderRadius();

      HideAndSeekGame game = new HideAndSeekGame(TimeUnit.SECONDS, seekTimeTicks, TimeUnit.SECONDS,
          hiderLeadTimeTicks, hidersBecomeSeekers, player.getWorld(), finalRadius, initialRadius,
          TimeUnit.SECONDS, shrinkTimeTicks * 20, initialSeekerPlayer);
      HideAndSeekManager.INSTANCE.setRunningGame(game);

      game.teleportLobby().thenAcceptAsync(v -> new BukkitRunnable() {
        @Override
        public void run() {
          WorldBorder worldBorder = player.getWorld().getWorldBorder();
          worldBorder.setCenter(lobbyLocation.getX(), lobbyLocation.getZ());
          worldBorder.setSize(lobbyRadius * 2);
          worldBorder.setDamageAmount(damageAmount);
          worldBorder.setDamageBuffer(damageBuffer);
        }
      }.runTask(HideAndSeek.getInstance()));

      game.load();

      player.sendMessage(Messages.prefix()
          .append(Component.text("Das Spiel wurde erstellt", NamedTextColor.GREEN)));
    });
  }
}
