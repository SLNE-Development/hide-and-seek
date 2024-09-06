package dev.slne.hideandnseek;

import dev.slne.hideandnseek.player.HideAndSeekPlayer;
import dev.slne.hideandnseek.step.GameStepManager;
import dev.slne.hideandnseek.util.TeamUtil;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Team;

/**
 * The type Hide and seek game.
 */
@Getter
public class HideAndSeekGame {

  private final GameData gameData;

  @Setter
  private HideAndSeekGameState gameState;
  private Team hidersTeam;
  private Team seekersTeam;

  public HideAndSeekGame(GameData gameData) {
    this.gameData = gameData;
  }

  public CompletableFuture<Void> prepare() {
    return GameStepManager.INSTANCE.prepareGame(this, gameData).thenRun(() -> {
      this.hidersTeam = TeamUtil.getOrCreateTeam("hiders");
      this.seekersTeam = TeamUtil.getOrCreateTeam("seekers");

      TeamUtil.prepareTeam(hidersTeam);
      TeamUtil.prepareTeam(seekersTeam);
    });
  }

  public CompletableFuture<Void> start() {
//    this.gameState = state;

    return GameStepManager.INSTANCE.startGame();
  }

  public CompletableFuture<Void> reset() { // TODO: 06.09.2024 21:59 - call when? via command?+
    hidersTeam.unregister();
    seekersTeam.unregister();

    this.gameState = null;
    this.hidersTeam = null;
    this.seekersTeam = null;

    return GameStepManager.INSTANCE.resetGame();
  }

  public CompletableFuture<Void> stop(HideAndSeekEndReason reason) {
    return GameStepManager.INSTANCE.stopGame(reason).thenRun(() -> {
      TeamUtil.unregisterTeam(hidersTeam);
      TeamUtil.unregisterTeam(seekersTeam);
    });
  }

  /**
   * Add seeker.
   *
   * @param player the player
   */
  public void addSeeker(HideAndSeekPlayer player) {
    seekersTeam.addEntity(player.getPlayer());
  }

  /**
   * Add hider.
   *
   * @param player the player
   */
  public void addHider(HideAndSeekPlayer player) {
    hidersTeam.addEntity(player.getPlayer());
  }

  /**
   * Remove seeker.
   *
   * @param player the player
   */
  public void removeSeeker(HideAndSeekPlayer player) {
    seekersTeam.removeEntity(player.getPlayer());
  }

  /**
   * Remove hider.
   *
   * @param player the player
   */
  public void removeHider(HideAndSeekPlayer player) {
    hidersTeam.removeEntity(player.getPlayer());
  }

  /**
   * Is hider.
   *
   * @param player the player
   * @return the boolean
   */
  public boolean isHider(HideAndSeekPlayer player) {
    return hidersTeam.hasEntity(player.getPlayer());
  }

  /**
   * Is seeker.
   *
   * @param player the player
   * @return the boolean
   */
  public boolean isSeeker(HideAndSeekPlayer player) {
    return seekersTeam.hasEntity(player.getPlayer());
  }

  /**
   * Gets seekers.
   *
   * @return the seekers
   */
  public List<HideAndSeekPlayer> getSeekers() {
    return seekersTeam.getEntries().stream()
        .map(UUID::fromString)
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
        .map(UUID::fromString)
        .map(HideAndSeekPlayer::get)
        .toList();
  }

  /**
   * Do hiders become seekers boolean.
   *
   * @return the boolean
   */
  public boolean doHidersBecomeSeekers() {
    return gameData.isHidersBecomeSeekers();
  }


  /**
   * Perform player check.
   */
  public void performPlayerCheck() {
    if (seekersTeam.getEntries().isEmpty() && !hidersTeam.getEntries().isEmpty()) {
      assignNewSeeker();
    }

    if (hidersTeam.getEntries().isEmpty()) {
      stop(HideAndSeekEndReason.SEEKER_WIN);
    }

    if (seekersTeam.getEntries().isEmpty()) {
      stop(HideAndSeekEndReason.HIDER_WIN);
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
}
