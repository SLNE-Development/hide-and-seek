package dev.slne.hideandnseek.step.steps;

import dev.slne.hideandnseek.GameSettings;
import dev.slne.hideandnseek.HideAndSeek;
import dev.slne.hideandnseek.HideAndSeekGame;
import dev.slne.hideandnseek.HideAndSeekGameState;
import dev.slne.hideandnseek.Messages;
import dev.slne.hideandnseek.player.HideAndSeekPlayer;
import dev.slne.hideandnseek.role.Role;
import dev.slne.hideandnseek.step.GameStep;
import dev.slne.hideandnseek.timer.HiderPreparationCountdown;
import dev.slne.hideandnseek.util.Continuation;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Emitter;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * The type Preparation step.
 */
public class PreparationStep extends GameStep {

  private static final ComponentLogger LOGGER = ComponentLogger.logger(PreparationStep.class);

  private final HideAndSeekGame game;
  private final Duration time;
  private final HideAndSeekPlayer initialSeeker;
  private final World world;
  private final int finalRadius;
  private int previousMaxPlayers;

  private HiderPreparationCountdown countdown;

  /**
   * Instantiates a new Preparation step.
   *
   * @param game the game
   */
  public PreparationStep(HideAndSeekGame game, GameSettings gameSettings) {
    super(HideAndSeekGameState.PREPARING);

    this.game = game;
    this.time = gameSettings.getPreparationTime();
    this.initialSeeker = gameSettings.getInitialSeeker();
    this.world = gameSettings.getWorld();
    this.finalRadius = gameSettings.getInitialRadius();
  }

  @Override
  public void load(Continuation continuation) {
    countdown = new HiderPreparationCountdown(time);

    continuation.resume();
  }

  @Override
  public void start(Continuation continuation) {
    previousMaxPlayers = Bukkit.getServer().getMaxPlayers();
    Bukkit.getServer().setMaxPlayers(0);

    printStartMessage();
    playStartSound();

    final HideAndSeekPlayer chosenSeeker = chooseSeeker();
    chosenSeeker.setRole(Role.SEEKER);

    runSync(() -> {
      Bukkit.getOnlinePlayers().stream()
          .map(HideAndSeekPlayer::get)
          .forEach(onlinePlayer -> {
            onlinePlayer.prepareForGame();

            if (onlinePlayer.isSeeker()) {
              onlinePlayer.giveSeekerInventory();
              onlinePlayer.teleportLobby();
            } else {
              onlinePlayer.setRole(Role.HIDER);
              onlinePlayer.teleportSpawn();
            }
          });

      world.getWorldBorder().setSize(finalRadius * 2);
    }).thenRun(() -> countdown.start(continuation)).exceptionally(exception -> {
      LOGGER.error("An error occurred while starting the game", exception);
      return null;
    });
  }

  @Override
  public void reset(Continuation continuation) {
    Bukkit.getServer().setMaxPlayers(previousMaxPlayers);
    continuation.resume();
  }

  /**
   * Choose seeker hide and seek player.
   *
   * @return the hide and seek player
   */
  private HideAndSeekPlayer chooseSeeker() {
    if (initialSeeker != null && initialSeeker.isOnline()) {
      return initialSeeker;
    }

    final List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
    return HideAndSeekPlayer.get(
        players.get(HideAndSeek.RANDOM.nextInt(players.size())).getUniqueId());
  }

  private void printStartMessage() {
    TextComponent.Builder builder = Component.text();

    builder.append(Messages.prefix()).appendNewline();
    builder.append(Messages.prefix().append(Component.text("-".repeat(20), NamedTextColor.GRAY)))
        .appendNewline();
    builder.append(Messages.prefix()).appendNewline();

    builder.append(
            Messages.prefix().append(Component.text("Das Spiel beginnt!", NamedTextColor.GOLD)))
        .appendNewline();
    builder.append(Messages.prefix()).appendNewline();

    builder.append(Messages.prefix().append(Component.text("-".repeat(20), NamedTextColor.GRAY)))
        .appendNewline();
    builder.append(Messages.prefix());

    Bukkit.broadcast(builder.build());
  }

  private void playStartSound() {
    runSync(() -> {
      Bukkit.getServer().playSound(
          Sound.sound().type(org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL)
              .volume(0.5f)
              .pitch(0.75f)
              .source(Source.MASTER)
              .build(),
          Emitter.self()
      );
    });
  }

  @Override
  public void interrupt() {
    countdown.interrupt();
  }
}
