package dev.slne.hideandnseek.step.steps;

import dev.slne.hideandnseek.HideAndSeek;
import dev.slne.hideandnseek.HideAndSeekEndReason;
import dev.slne.hideandnseek.HideAndSeekGame;
import dev.slne.hideandnseek.HideAndSeekGameState;
import dev.slne.hideandnseek.Messages;
import dev.slne.hideandnseek.player.HideAndSeekPlayer;
import dev.slne.hideandnseek.step.GameStep;
import dev.slne.hideandnseek.timer.HiderPreparationCountdown;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;
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
public class PreparationStep implements GameStep {

  private final HideAndSeekGame game;
  private final TimeUnit timeUnit;
  private final long time;
  private final HideAndSeekPlayer initialSeeker;
  private final World world;
  private final int finalRadius;

  private HiderPreparationCountdown countdown;

  /**
   * Instantiates a new Preparation step.
   *
   * @param game     the game
   * @param timeUnit the time unit
   * @param time     the time
   */
  public PreparationStep(HideAndSeekGame game, TimeUnit timeUnit, long time,
      HideAndSeekPlayer initialSeeker, World world, int finalRadius) {
    this.game = game;
    this.timeUnit = timeUnit;
    this.time = time;
    this.initialSeeker = initialSeeker;
    this.world = world;
    this.finalRadius = finalRadius;
  }

  @Override
  public HideAndSeekGameState getGameState() {
    return HideAndSeekGameState.PREPARING;
  }

  @Override
  public void load() {
    countdown = new HiderPreparationCountdown(this, timeUnit, time);
  }

  @Override
  public void start() {
    Bukkit.getServer().setMaxPlayers(0);

    printStartMessage();
    playStartSound();

    HideAndSeekPlayer seeker = chooseSeeker();
    game.addSeeker(seeker);

    Bukkit.getOnlinePlayers().stream().map(HideAndSeekPlayer::get).forEach(onlinePlayer -> {
      onlinePlayer.prepareForGame();

      if (onlinePlayer.isSeeker()) {
        onlinePlayer.teleportLobby();
      } else {
        game.addHider(onlinePlayer);
        onlinePlayer.teleportSpawn();
      }
    });

    world.getWorldBorder().setSize(finalRadius * 2);

    countdown.runTaskTimer(HideAndSeek.getInstance(), 0, 20);
  }

  @Override
  public void end(HideAndSeekEndReason reason) {
    countdown.cancel();
  }

  @Override
  public void reset() {

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

    SecureRandom random = HideAndSeek.RANDOM;
    int index = random.nextInt(game.getHiders().size());

    return game.getHiders().get(index);
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
        Sound.sound().type(org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL).volume(.5f).pitch(.75f)
            .source(Source.MASTER).build(), Emitter.self());
  }
}
