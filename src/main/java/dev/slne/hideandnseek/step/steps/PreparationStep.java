package dev.slne.hideandnseek.step.steps;

import dev.slne.hideandnseek.HideAndSeek;
import dev.slne.hideandnseek.HideAndSeekEndReason;
import dev.slne.hideandnseek.HideAndSeekGame;
import dev.slne.hideandnseek.HideAndSeekGameState;
import dev.slne.hideandnseek.Messages;
import dev.slne.hideandnseek.player.HideAndSeekPlayer;
import dev.slne.hideandnseek.step.GameStep;
import dev.slne.hideandnseek.util.Continuation;
import dev.slne.hideandnseek.GameData;
import dev.slne.hideandnseek.timer.HiderPreparationCountdown;
import java.time.Duration;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Emitter;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 * The type Preparation step.
 */
public class PreparationStep extends GameStep {

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
   * @param game     the game
   * @param timeUnit the time unit
   * @param time     the time
   */
  public PreparationStep(HideAndSeekGame game, GameData gameData) {
    super(HideAndSeekGameState.PREPARING);

    this.game = game;
    this.time = gameData.getPreparationTime();
    this.initialSeeker = gameData.getInitialSeeker();
    this.world = gameData.getWorld();
    this.finalRadius = gameData.getInitialRadius();
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

    final HideAndSeekPlayer seeker = chooseSeeker();
    game.addSeeker(seeker);

    Bukkit.getOnlinePlayers().stream()
        .map(HideAndSeekPlayer::get)
        .forEach(onlinePlayer -> {
          onlinePlayer.prepareForGame();

          if (onlinePlayer.isSeeker()) {
            onlinePlayer.teleportLobby();
          } else {
            game.addHider(onlinePlayer);
            onlinePlayer.teleportSpawn();
          }
        });

    world.getWorldBorder().setSize(finalRadius * 2);

    countdown.start(continuation);
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

    return game.getHiders().get(HideAndSeek.RANDOM.nextInt(game.getHiders().size()));
  }

  private void printStartMessage() {
    TextComponent.Builder builder = Component.text();

    builder.append(Messages.prefix()).appendNewline();
    builder.append(Component.text("-".repeat(20))).appendNewline();
    builder.append(Messages.prefix()).appendNewline();

    builder.append(Messages.prefix().append(Component.text("Das Spiel beginnt!"))).appendNewline();
    builder.append(Messages.prefix()).appendNewline();

    builder.append(Component.text("-".repeat(20))).appendNewline();
    builder.append(Messages.prefix());

    Bukkit.broadcast(builder.build());
  }

  private void playStartSound() {
    Bukkit.getServer().playSound(
        Sound.sound().type(org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL)
            .volume(0.5f)
            .pitch(0.75f)
            .source(Source.MASTER)
            .build(),
        Emitter.self()
    );
  }
}
