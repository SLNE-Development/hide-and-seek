package dev.slne.hideandnseek;

import dev.slne.hideandnseek.player.HideAndSeekPlayer;
import dev.slne.hideandnseek.step.GameStep;
import dev.slne.hideandnseek.step.GameStepManager;
import dev.slne.hideandnseek.util.TeamUtil;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Team;

/**
 * The type Hide and seek game.
 */
@Getter
public class HideAndSeekGame {

  private static final ComponentLogger LOGGER = ComponentLogger.logger(HideAndSeekGame.class);

  private final GameSettings gameSettings;

  private Team hidersTeam;
  private Team seekersTeam;

  /**
   * Instantiates a new Hide and seek game.
   *
   * @param gameSettings the game settings
   */
  public HideAndSeekGame(GameSettings gameSettings) {
    this.gameSettings = gameSettings;
  }

  /**
   * Prepare completable future.
   *
   * @return the completable future
   */
  public CompletableFuture<Void> prepare() {
    this.hidersTeam = TeamUtil.getOrCreateTeam("hiders");
    this.seekersTeam = TeamUtil.getOrCreateTeam("seekers");

    TeamUtil.prepareTeam(hidersTeam);
    TeamUtil.prepareTeam(seekersTeam);

    return GameStepManager.INSTANCE.prepareGame(this, gameSettings);
  }

  /**
   * Start completable future.
   *
   * @return the completable future
   */
  public CompletableFuture<Void> start() {
    return GameStepManager.INSTANCE.startGame();
  }

  /**
   * Reset completable future.
   *
   * @return the completable future
   */
  public CompletableFuture<Void> reset() { // TODO: 06.09.2024 21:59 - call when? via command?+
    hidersTeam.unregister();
    seekersTeam.unregister();

    this.hidersTeam = null;
    this.seekersTeam = null;

    return GameStepManager.INSTANCE.resetGame()
        .thenRun(() -> HideAndSeekManager.INSTANCE.setRunningGame(null))
        .exceptionally(exception -> {
          LOGGER.error("An error occurred while resetting the game", exception);
          return null;
        });
  }

  /**
   * Stop completable future.
   *
   * @param reason    the reason
   * @param interrupt the interrupt
   * @return the completable future
   */
  public CompletableFuture<Void> stop(HideAndSeekEndReason reason, boolean interrupt) {
    return GameStepManager.INSTANCE.stopGame(reason, interrupt)
        .thenComposeAsync(unused -> reset())
        .exceptionally(exception -> {
          LOGGER.error("An error occurred while stopping the game", exception);
          return null;
        });
  }

  /**
   * Forcestop completable future.
   *
   * @return the completable future
   */
  public CompletableFuture<Void> forcestop() {
    return GameStepManager.INSTANCE.forceStop()
        .thenComposeAsync(unused -> reset());
  }

  /**
   * Add seeker.
   *
   * @param player the player
   */
  public void addSeeker(HideAndSeekPlayer player) {
    seekersTeam.addPlayer(player.getPlayer());
  }

  /**
   * Add hider.
   *
   * @param player the player
   */
  public void addHider(HideAndSeekPlayer player) {
    hidersTeam.addPlayer(player.getPlayer());
  }

  /**
   * Remove seeker.
   *
   * @param player the player
   */
  public void removeSeeker(HideAndSeekPlayer player) {
    seekersTeam.removePlayer(player.getPlayer());
  }

  /**
   * Remove hider.
   *
   * @param player the player
   */
  public void removeHider(HideAndSeekPlayer player) {
    hidersTeam.removePlayer(player.getPlayer());
  }

  /**
   * Is hider.
   *
   * @param player the player
   * @return the boolean
   */
  public boolean isHider(HideAndSeekPlayer player) {
    return hidersTeam != null && hidersTeam.hasPlayer(player.getPlayer());
  }

  /**
   * Is seeker.
   *
   * @param player the player
   * @return the boolean
   */
  public boolean isSeeker(HideAndSeekPlayer player) {
    return seekersTeam != null && seekersTeam.hasPlayer(player.getPlayer());
  }

  /**
   * Gets seekers.
   *
   * @return the seekers
   */
  public List<HideAndSeekPlayer> getSeekers() {
    return seekersTeam.getEntries().stream()
        .map(s -> Bukkit.getOfflinePlayer(s).getUniqueId())
        .map(HideAndSeekPlayer::get)
        .toList();
  }

  /**
   * Gets hiders.
   *
   * @return the hiders
   */
  public List<HideAndSeekPlayer> getHiders() {
    return hidersTeam.getEntries().stream()
        .map(s -> Bukkit.getOfflinePlayer(s).getUniqueId())
        .map(HideAndSeekPlayer::get)
        .toList();
  }

  /**
   * Do hiders become seekers boolean.
   *
   * @return the boolean
   */
  public boolean doHidersBecomeSeekers() {
    return gameSettings.isHidersBecomeSeekers();
  }

  /**
   * Perform player check.
   */
  public void performPlayerCheck() {
    if (!getGameState().isIngame()) {
      return;
    }

    if (seekersTeam.getEntries().isEmpty() && !hidersTeam.getEntries().isEmpty()) {
      assignNewSeeker();
    }

    if (hidersTeam.getEntries().isEmpty()) {
      stop(HideAndSeekEndReason.SEEKER_WIN, true);
    }

    if (seekersTeam.getEntries().isEmpty()) {
      stop(HideAndSeekEndReason.HIDER_WIN, true);
    }
  }

  /**
   * Assign new seeker.
   */
  private void assignNewSeeker() {
    final String newSeekerStringUuid = hidersTeam.getEntries().stream().findAny().orElseThrow();
    final HideAndSeekPlayer newSeeker = HideAndSeekPlayer.get(UUID.fromString(newSeekerStringUuid));

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

  /**
   * Gets game state.
   *
   * @return the game state
   */
  public HideAndSeekGameState getGameState() {
    final GameStep step = GameStepManager.INSTANCE.getCurrentStep();

    return step == null ? HideAndSeekGameState.UNKNOWN : step.getGameState();
  }
}
