package dev.slne.hideandnseek;

import dev.slne.hideandnseek.player.HideAndSeekPlayer;
import dev.slne.hideandnseek.step.GameStep;
import dev.slne.hideandnseek.step.steps.LobbyStep;
import dev.slne.hideandnseek.step.steps.PreparationStep;
import dev.slne.hideandnseek.timer.GameCountdown;
import dev.slne.hideandnseek.timer.HiderPreparationCountdown;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scoreboard.Team;

/**
 * The type Hide and seek game.
 */
public class HideAndSeekGame {

  private HideAndSeekGameState gameState;

  private final List<HideAndSeekPlayer> seekers;
  private final List<HideAndSeekPlayer> hiders;

  private HideAndSeekPlayer initialSeeker;

  private final boolean doHidersBecomeSeekers;
  private final World world;
  private final int finalRadius;
  private final int initialRadius;
  private final long shrinkTimeTicks;

  private final List<GameStep> steps;

  /**
   * Instantiates a new Hide and seek game.
   *
   * @param timeUnit              the time unit
   * @param maxTime               the max time
   * @param prepTimeUnit          the prep time unit
   * @param prepMaxTime           the prep max time
   * @param doHidersBecomeSeekers the do hiders become seekers
   * @param world                 the world
   * @param finalRadius           the final radius
   * @param initialRadius         the initial radius
   * @param shrinkTimeUnit        the shrink time unit
   * @param shrinkTime            the shrink time
   * @param initialSeeker         the initial seeker
   */
  public HideAndSeekGame(TimeUnit timeUnit, long maxTime, TimeUnit prepTimeUnit, long prepMaxTime,
      boolean doHidersBecomeSeekers, World world, int finalRadius, int initialRadius,
      TimeUnit shrinkTimeUnit, long shrinkTime, HideAndSeekPlayer initialSeeker) {
    this.steps = new ArrayList<>();

    this.steps.add(new LobbyStep(this, TimeUnit.SECONDS,
        HideAndSeekManager.INSTANCE.getLobbyCountdown()));
    this.steps.add(new PreparationStep(this, prepTimeUnit, prepMaxTime));

    this.seekers = new ArrayList<>();
    this.hiders = new ArrayList<>();
    this.initialSeeker = initialSeeker;

    this.world = world;
    this.finalRadius = finalRadius;
    this.initialRadius = initialRadius;
    this.shrinkTimeTicks = shrinkTimeUnit.toSeconds(shrinkTime) * 20;

    this.doHidersBecomeSeekers = doHidersBecomeSeekers;
  }

  /**
   * Start.
   *
   * @param state the state
   */
  public void start(HideAndSeekGameState state) {
    this.gameState = state;

    this.steps.stream().filter(lifecycle -> lifecycle.getGameState().equals(state))
        .forEach(GameStep::start);
  }

  /**
   * Load.
   */
  public void load() {
    steps.forEach(GameStep::load);
  }

  /**
   * Reset.
   */
  public void reset() {
    steps.forEach(GameStep::reset);
  }


  /**
   * Add seeker.
   *
   * @param player the player
   */
  public void addSeeker(HideAndSeekPlayer player) {
    seekers.add(player);

    seekersTeam.addPlayer(player.getPlayer());
  }

  /**
   * Add hider.
   *
   * @param player the player
   */
  public void addHider(HideAndSeekPlayer player) {
    hiders.add(player);

    hidersTeam.addPlayer(player.getPlayer());
  }

  /**
   * Remove seeker.
   *
   * @param player the player
   */
  public void removeSeeker(HideAndSeekPlayer player) {
    seekers.remove(player);

    seekersTeam.removePlayer(player.getPlayer());
  }

  /**
   * Remove hider.
   *
   * @param player the player
   */
  public void removeHider(HideAndSeekPlayer player) {
    hiders.remove(player);

    hidersTeam.removePlayer(player.getPlayer());
  }

  /**
   * Is hider.
   *
   * @param player the player
   * @return the boolean
   */
  public boolean isHider(HideAndSeekPlayer player) {
    return hiders.contains(player);
  }

  /**
   * Is seeker.
   *
   * @param player the player
   * @return the boolean
   */
  public boolean isSeeker(HideAndSeekPlayer player) {
    return seekers.contains(player);
  }

  /**
   * Sets game state.
   *
   * @param gameState the game state
   */
  public void setGameState(HideAndSeekGameState gameState) {
    this.gameState = gameState;
  }

  /**
   * Is do hiders become seekers boolean.
   *
   * @return the boolean
   */
  public boolean isDoHidersBecomeSeekers() {
    return doHidersBecomeSeekers;
  }

  /**
   * Gets hiders team.
   *
   * @return the hiders team
   */
  public Team getHidersTeam() {
    return hidersTeam;
  }

  /**
   * Gets seekers team.
   *
   * @return the seekers team
   */
  public Team getSeekersTeam() {
    return seekersTeam;
  }

  /**
   * Gets game state.
   *
   * @return the game state
   */
  public HideAndSeekGameState getGameState() {
    return gameState;
  }

  /**
   * Gets seekers.
   *
   * @return the seekers
   */
  public List<HideAndSeekPlayer> getSeekers() {
    return seekers;
  }

  /**
   * Gets hiders.
   *
   * @return the hiders
   */
  public List<HideAndSeekPlayer> getHiders() {
    return hiders;
  }

  /**
   * Gets timer.
   *
   * @return the timer
   */
  public GameCountdown getTimer() {
    return timer;
  }

  /**
   * Gets final radius.
   *
   * @return the final radius
   */
  public int getFinalRadius() {
    return finalRadius;
  }

  /**
   * Gets initial radius.
   *
   * @return the initial radius
   */
  public int getInitialRadius() {
    return initialRadius;
  }

  /**
   * Gets shrink time ticks.
   *
   * @return the shrink time ticks
   */
  public long getShrinkTimeTicks() {
    return shrinkTimeTicks;
  }

  /**
   * Do hiders become seekers boolean.
   *
   * @return the boolean
   */
  public boolean doHidersBecomeSeekers() {
    return doHidersBecomeSeekers;
  }

  /**
   * Are hiders remaining boolean.
   *
   * @return the boolean
   */
  public boolean areHidersRemaining() {
    return !hiders.isEmpty();
  }

  /**
   * Gets hider prep timer.
   *
   * @return the hider prep timer
   */
  public HiderPreparationCountdown getHiderPrepTimer() {
    return hiderPrepTimer;
  }

  /**
   * Perform player check.
   */
  public void performPlayerCheck() {
    if (seekers.isEmpty() && !hiders.isEmpty()) {
      assignNewSeeker();
    }

    if (hiders.isEmpty()) {
      end(HideAndSeekEndReason.SEEKER_WIN);
    }

    if (seekers.isEmpty()) {
      end(HideAndSeekEndReason.HIDER_WIN);
    }
  }

  /**
   * Assign new seeker.
   */
  private void assignNewSeeker() {
    HideAndSeekPlayer newSeeker = hiders.getFirst();

    removeHider(newSeeker);
    addSeeker(newSeeker);

    newSeeker.getPlayer()
        .sendMessage(Messages.prefix().append(Component.text("Du bist jetzt ein Sucher!")));
    newSeeker.prepareForGame();
    newSeeker.teleportSpawn();
  }

  /**
   * Teleport lobby completable future.
   *
   * @return the completable future
   */
  public CompletableFuture<Void> teleportLobby() {
    return CompletableFuture.allOf(Bukkit.getOnlinePlayers().stream()
        .map(player -> player.teleportAsync(HideAndSeekManager.INSTANCE.getLobbyLocation()))
        .toArray(CompletableFuture[]::new));
  }
}
