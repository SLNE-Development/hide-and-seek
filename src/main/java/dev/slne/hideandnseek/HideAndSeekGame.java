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
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Team;

/**
 * The type Hide and seek game.
 */
@Getter
public class HideAndSeekGame {

  private final GameSettings gameSettings;

  private Team hidersTeam;
  private Team seekersTeam;

  public HideAndSeekGame(GameSettings gameSettings) {
    this.gameSettings = gameSettings;
  }

  public CompletableFuture<Void> prepare() {
    this.hidersTeam = TeamUtil.getOrCreateTeam("hiders");
    this.seekersTeam = TeamUtil.getOrCreateTeam("seekers");

    TeamUtil.prepareTeam(hidersTeam);
    TeamUtil.prepareTeam(seekersTeam);

    return GameStepManager.INSTANCE.prepareGame(this, gameSettings);
  }

  public CompletableFuture<Void> start() {
    return GameStepManager.INSTANCE.startGame();
  }

  public CompletableFuture<Void> reset() { // TODO: 06.09.2024 21:59 - call when? via command?+
    hidersTeam.unregister();
    seekersTeam.unregister();

    this.hidersTeam = null;
    this.seekersTeam = null;

    return GameStepManager.INSTANCE.resetGame()
        .thenRun(() -> HideAndSeekManager.INSTANCE.setRunningGame(null))
        .thenRun(() -> {
          System.err.println("Game reset");
        }).exceptionally(ex -> {
          System.err.println("Error while resetting game");
          ex.printStackTrace();
          return null;
        });
  }

  public CompletableFuture<Void> stop(HideAndSeekEndReason reason, boolean interrupt) {
    return GameStepManager.INSTANCE.stopGame(reason, interrupt)
        .thenComposeAsync(unused -> reset())
        .exceptionally(ex -> {
          System.err.println("Error while stopping game");
          ex.printStackTrace();
          return null;
        });
  }

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
    return hidersTeam.hasPlayer(player.getPlayer());
  }

  /**
   * Is seeker.
   *
   * @param player the player
   * @return the boolean
   */
  public boolean isSeeker(HideAndSeekPlayer player) {
    return seekersTeam.hasPlayer(player.getPlayer());
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

  public HideAndSeekGameState getGameState() {
    final GameStep step = GameStepManager.INSTANCE.getCurrentStep();

    return step == null ? HideAndSeekGameState.UNKNOWN : step.getGameState();
  }
}
