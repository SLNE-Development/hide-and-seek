package dev.slne.hideandnseek.command.sub;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument.OnePlayer;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.TimeArgument;
import dev.slne.hideandnseek.GameData;
import dev.slne.hideandnseek.HideAndSeek;
import dev.slne.hideandnseek.HideAndSeekGame;
import dev.slne.hideandnseek.HideAndSeekManager;
import dev.slne.hideandnseek.Messages;
import dev.slne.hideandnseek.player.HideAndSeekPlayer;
import io.papermc.paper.util.Tick;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

/**
 * The type Hide and seek create command.
 */
public class HideAndSeekCreateCommand extends CommandAPICommand {

  private final TimeArgument seekTime = new TimeArgument("seek_time");
  private final TimeArgument hiderLeadTime = new TimeArgument("hider_lead_time");
  private final BooleanArgument hidersBecomeSeekers = new BooleanArgument("hiders_become_seekers");
  private final IntegerArgument initialRadius = new IntegerArgument("initial_radius");
  private final IntegerArgument finalRadius = new IntegerArgument("final_radius");
  private final TimeArgument shrinkTime = new TimeArgument("shrink_time");

  private final OnePlayer initialSeeker = new OnePlayer("initial_seeker");
  private final IntegerArgument damageAmount = new IntegerArgument("damage_amount");
  private final IntegerArgument damageBuffer = new IntegerArgument("damage_buffer");

  /**
   * Instantiates a new Hide and seek create command.
   *
   * @param commandName the command name
   */
  public HideAndSeekCreateCommand(String commandName) {
    super(commandName);

    withPermission("hideandseek.command.create");

    withArguments(
        seekTime,
        hiderLeadTime,
        hidersBecomeSeekers,
        initialRadius,
        finalRadius,
        shrinkTime,
        initialSeeker
    );

    withOptionalArguments(
        damageAmount,
        damageBuffer
    );

    executesPlayer((player, args) -> {
      //noinspection DataFlowIssue
      final int seekTimeTicks = args.getByArgument(seekTime);
      //noinspection DataFlowIssue
      final int hiderLeadTimeTicks = args.getByArgument(hiderLeadTime);
      //noinspection DataFlowIssue
      final boolean hidersBecomeSeekers = args.getByArgument(this.hidersBecomeSeekers);
      //noinspection DataFlowIssue
      final int initialRadius = args.getByArgument(this.initialRadius);
      //noinspection DataFlowIssue
      final int finalRadius = args.getByArgument(this.finalRadius);
      //noinspection DataFlowIssue
      final long shrinkTimeTicks = args.getByArgument(this.shrinkTime);
      final Player initialSeeker = args.getByArgumentOrDefault(this.initialSeeker, player);
      final HideAndSeekPlayer initialSeekerPlayer = HideAndSeekPlayer.get(initialSeeker);

      final int damageAmount = args.getByArgumentOrDefault(this.damageAmount, 1);
      final int damageBuffer = args.getByArgumentOrDefault(this.damageBuffer, 0);

      final HideAndSeekGame runningGame = HideAndSeekManager.INSTANCE.getRunningGame();

      if (runningGame != null) {
        throw CommandAPI.failWithString("Es läuft bereits ein Spiel");
      }

      final Location lobbyLocation = HideAndSeekManager.INSTANCE.getLobbyLocation();
      final int lobbyRadius = HideAndSeekManager.INSTANCE.getLobbyWorldBorderRadius();

//      HideAndSeekGame game = new HideAndSeekGame(TimeUnit.SECONDS, seekTimeTicks, TimeUnit.SECONDS,
//          hiderLeadTimeTicks, hidersBecomeSeekers, player.getWorld(), finalRadius, initialRadius,
//          TimeUnit.SECONDS, shrinkTimeTicks * 20, initialSeekerPlayer);

      final HideAndSeekGame game = new HideAndSeekGame(GameData.builder()
          .gameDuration(Tick.of(seekTimeTicks))
          .preparationTime(Tick.of(hiderLeadTimeTicks))
          .world(player.getWorld())
          .initialRadius(initialRadius)
          .finalRadius(finalRadius)
          .shrinkTime(Tick.of(shrinkTimeTicks))
          .initialSeeker(initialSeekerPlayer)
          .lobbyTime(Tick.of(10)) // TODO: 04.09.2024 21:50 - ?
          .hidersBecomeSeekers(hidersBecomeSeekers)
          .build());

      HideAndSeekManager.INSTANCE.setRunningGame(game);

      game.teleportLobby()
          .thenRun(() -> Bukkit.getScheduler().runTask(HideAndSeek.getInstance(), () -> {
            WorldBorder worldBorder = player.getWorld().getWorldBorder();
            worldBorder.setCenter(lobbyLocation.getX(), lobbyLocation.getZ());
            worldBorder.setSize(lobbyRadius * 2);
            worldBorder.setDamageAmount(damageAmount);
            worldBorder.setDamageBuffer(damageBuffer);
          }));

      player.sendMessage(Messages.prefix()
          .append(Component.text("Preperiere das Spiel...").color(NamedTextColor.GREEN)));
      game.prepare().thenRun(() -> {
        player.sendMessage(Messages.prefix()
            .append(Component.text("Das Spiel wurde vorbereitet").color(NamedTextColor.GREEN)));
      });
    });
  }
}
