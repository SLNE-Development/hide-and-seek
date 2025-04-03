package dev.slne.hideandnseek.old.command.sub;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument.OnePlayer;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.Location2DArgument;
import dev.jorel.commandapi.arguments.TimeArgument;
import dev.jorel.commandapi.arguments.WorldArgument;
import dev.slne.hideandnseek.old.GameSettings;
import dev.slne.hideandnseek.old.HideAndSeekManager;
import dev.slne.hideandnseek.old.Messages;
import dev.slne.hideandnseek.old.player.HideAndSeekPlayer;
import io.papermc.paper.util.Tick;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class HideAndSeekSettingsCommand extends CommandAPICommand {

//  private final TimeArgument seekTime = new TimeArgument("seek_time");
//  private final TimeArgument hiderLeadTime = new TimeArgument("hider_lead_time");
//  private final BooleanArgument hidersBecomeSeekers = new BooleanArgument("hiders_become_seekers");
//  private final IntegerArgument initialRadius = new IntegerArgument("initial_radius");
//  private final IntegerArgument finalRadius = new IntegerArgument("final_radius");
//  private final TimeArgument shrinkTime = new TimeArgument("shrink_time");
//
//  private final OnePlayer initialSeeker = new OnePlayer("initial_seeker");
//  private final IntegerArgument damageAmount = new IntegerArgument("damage_amount");
//  private final IntegerArgument damageBuffer = new IntegerArgument("damage_buffer");

  private final ObjectList<Setting<?>> settings = ObjectList.of(
      Setting.of(
          "seekTime",
          new TimeArgument("seek_time"),
          () -> Tick.tick().fromDuration(gameSettings().getGameDuration()),
          value -> gameSettings().setGameDuration(Tick.of(value))
      ),
      Setting.of(
          "hiderLeadTime",
          new TimeArgument("hider_lead_time"),
          () -> Tick.tick().fromDuration(gameSettings().getPreparationTime()),
          value -> gameSettings().setPreparationTime(Tick.of(value))
      ),
      Setting.of(
          "hidersBecomeSeekers",
          new BooleanArgument("hiders_become_seekers"),
          gameSettings()::isHidersBecomeSeekers,
          gameSettings()::setHidersBecomeSeekers
      ),
      Setting.of(
          "initialRadius",
          new IntegerArgument("initial_radius"),
          gameSettings()::getInitialRadius,
          gameSettings()::setInitialRadius
      ),
      Setting.of(
          "finalRadius",
          new IntegerArgument("final_radius"),
          gameSettings()::getFinalRadius,
          gameSettings()::setFinalRadius
      ),
      Setting.of(
          "initialSeeker",
          new OnePlayer("initial_seeker"),
          () -> gameSettings().getInitialSeeker().getPlayer(),
          value -> gameSettings().setInitialSeeker(HideAndSeekPlayer.get(value))
      ),
      Setting.of(
          "damageAmount",
          new DoubleArgument("damage_amount", 0),
          gameSettings()::getWorldBorderDamageAmount,
          gameSettings()::setWorldBorderDamageAmount
      ),
      Setting.of(
          "damageBuffer",
          new DoubleArgument("damage_buffer", 0),
          gameSettings()::getWorldBorderDamageBuffer,
          gameSettings()::setWorldBorderDamageBuffer
      ),
      Setting.of(
          "lobbyTime",
          new TimeArgument("lobby_time"),
          () -> Tick.tick().fromDuration(gameSettings().getLobbyTime()),
          value -> gameSettings().setLobbyTime(Tick.of(value))
      ),
      Setting.of(
          "world",
          new WorldArgument("world"),
          () -> gameSettings().getWorld(),
          value -> gameSettings().setWorld(value)
      ),
      Setting.of(
          "worldBorderCenter",
          new Location2DArgument("world_border_center"),
          () -> gameSettings().getWorldBorderCenter(),
          value -> gameSettings().setWorldBorderCenter(value)
      ),
      Setting.of(
          "ohko",
          new BooleanArgument("ohko"),
          gameSettings()::isOhko,
          gameSettings()::setOhko
      ),
      Setting.of(
          "endDuration",
          new TimeArgument("end_duration"),
          () -> Tick.tick().fromDuration(gameSettings().getEndDuration()),
          value -> gameSettings().setEndDuration(Tick.of(value))
      )
  );

  private static GameSettings gameSettings() {
    return HideAndSeekManager.INSTANCE.getGameSettings();
  }

  public HideAndSeekSettingsCommand(String commandName) {
    super(commandName);

    withPermission("hideandseek.command.settings");

    for (final Setting<?> setting : settings) {
      withSubcommand(new CommandAPICommand(setting.settingName)
          .executes((sender, args) -> {
            final Object currentValue = setting.getter().get();
            sender.sendMessage(Messages.prefix()
                .append(Component.text("Der aktuelle Wert von ").color(NamedTextColor.GREEN))
                .append(Component.text(setting.settingName).color(NamedTextColor.AQUA))
                .append(Component.text(" ist ").color(NamedTextColor.GREEN))
                .append(Component.text(Objects.toString(currentValue)).color(NamedTextColor.AQUA)));
          }));

      withSubcommand(new CommandAPICommand(setting.settingName)
          .withArguments(setting.argument)
          .executes((sender, args) -> {
            final Object value = args.getByArgument(setting.argument);

            assert value != null;

            setting.set(value);
            sender.sendMessage(Messages.prefix()
                .append(Component.text("Der Wert von ").color(NamedTextColor.GREEN))
                .append(Component.text(setting.settingName).color(NamedTextColor.AQUA))
                .append(Component.text(" wurde auf ").color(NamedTextColor.GREEN))
                .append(Component.text(Objects.toString(value)).color(NamedTextColor.AQUA))
                .append(Component.text(" gesetzt.").color(NamedTextColor.GREEN)));
          }));
    }

//    withArguments(
//        seekTime,
//        hiderLeadTime,
//        hidersBecomeSeekers,
//        initialRadius,
//        finalRadius,
//        shrinkTime,
//        initialSeeker
//    );
//
//    withOptionalArguments(
//        damageAmount,
//        damageBuffer
//    );
//
//    executesPlayer((player, args) -> {
//      //noinspection DataFlowIssue
//      final int seekTimeTicks = args.getByArgument(seekTime);
//      //noinspection DataFlowIssue
//      final int hiderLeadTimeTicks = args.getByArgument(hiderLeadTime);
//      //noinspection DataFlowIssue
//      final boolean hidersBecomeSeekers = args.getByArgument(this.hidersBecomeSeekers);
//      //noinspection DataFlowIssue
//      final int initialRadius = args.getByArgument(this.initialRadius);
//      //noinspection DataFlowIssue
//      final int finalRadius = args.getByArgument(this.finalRadius);
//      //noinspection DataFlowIssue
//      final long shrinkTimeTicks = args.getByArgument(this.shrinkTime);
//      final Player initialSeeker = args.getByArgumentOrDefault(this.initialSeeker, player);
//      final HideAndSeekPlayer initialSeekerPlayer = HideAndSeekPlayer.get(initialSeeker);
//
//      final int damageAmount = args.getByArgumentOrDefault(this.damageAmount, 1);
//      final int damageBuffer = args.getByArgumentOrDefault(this.damageBuffer, 0);
//
//      final HideAndSeekGame runningGame = HideAndSeekManager.INSTANCE.getRunningGame();
//
//      if (runningGame != null) {
//        throw CommandAPI.failWithString("Es läuft bereits ein Spiel");
//      }
//
//      final Location lobbyLocation = HideAndSeekManager.INSTANCE.getLobbyLocation();
//      final int lobbyRadius = HideAndSeekManager.INSTANCE.getLobbyWorldBorderRadius();
//
////      HideAndSeekGame game = new HideAndSeekGame(TimeUnit.SECONDS, seekTimeTicks, TimeUnit.SECONDS,
////          hiderLeadTimeTicks, hidersBecomeSeekers, player.getWorld(), finalRadius, initialRadius,
////          TimeUnit.SECONDS, shrinkTimeTicks * 20, initialSeekerPlayer);
//
//      final HideAndSeekGame game = new HideAndSeekGame(GameSettings.builder()
//          .gameDuration(Tick.of(seekTimeTicks))
//          .preparationTime(Tick.of(hiderLeadTimeTicks))
//          .world(player.getWorld())
//          .initialRadius(initialRadius)
//          .finalRadius(finalRadius)
//          .shrinkTime(Tick.of(shrinkTimeTicks))
//          .initialSeeker(initialSeekerPlayer)
//          .lobbyTime(Tick.of(10)) // TODO: 04.09.2024 21:50 - ?
//          .hidersBecomeSeekers(hidersBecomeSeekers)
//          .build());
//
//      HideAndSeekManager.INSTANCE.setRunningGame(game);
//
//      game.teleportLobby()
//          .thenRun(() -> Bukkit.getScheduler().runTask(HideAndSeek.getInstance(), () -> {
//            WorldBorder worldBorder = player.getWorld().getWorldBorder();
//            worldBorder.setCenter(lobbyLocation.getX(), lobbyLocation.getZ());
//            worldBorder.setSize(lobbyRadius * 2);
//            worldBorder.setDamageAmount(damageAmount);
//            worldBorder.setDamageBuffer(damageBuffer);
//          }));
//
//      player.sendMessage(Messages.prefix()
//          .append(Component.text("Preperiere das Spiel...").color(NamedTextColor.GREEN)));
//      game.prepare().thenRun(() -> {
//        player.sendMessage(Messages.prefix()
//            .append(Component.text("Das Spiel wurde vorbereitet").color(NamedTextColor.GREEN)));
//      });
//    });
  }

  private record Setting<A>(
      String settingName,
      Argument<A> argument,
      Supplier<A> getter,
      Consumer<A> setter
  ) {

    @Contract("_, _, _, _ -> new")
    private static <A> @NotNull Setting<A> of(
        String settingName,
        Argument<A> argument,
        Supplier<A> getter,
        Consumer<A> setter
    ) {
      return new Setting<>(settingName, argument, getter, setter);
    }

    @SuppressWarnings("unchecked")
    private void set(Object value) {
      setter.accept((A) value);
    }
  }
}
