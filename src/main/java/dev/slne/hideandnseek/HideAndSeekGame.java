package dev.slne.hideandnseek;

import dev.slne.hideandnseek.player.HideAndSeekPlayer;
import dev.slne.hideandnseek.role.Role;
import dev.slne.hideandnseek.step.GameStep;
import dev.slne.hideandnseek.step.GameStepManager;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;

/**
 * The type Hide and seek game.
 */
@Getter
public class HideAndSeekGame {

  private static final ComponentLogger LOGGER = ComponentLogger.logger(HideAndSeekGame.class);

  private final GameSettings gameSettings;

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
  public CompletableFuture<Void> reset() {
    Bukkit.getOnlinePlayers().forEach(online -> HideAndSeekPlayer.get(online).setRole(Role.UNDEFINED));

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
   * Is hider.
   *
   * @param player the player
   * @return the boolean
   */
  public boolean isHider(HideAndSeekPlayer player) {
    if(player.getRole() == null){
      player.setRole(Role.UNDEFINED);
    }

    return player.getRole().equals(Role.HIDER);
  }

  /**
   * Is seeker.
   *
   * @param player the player
   * @return the boolean
   */
  public boolean isSeeker(HideAndSeekPlayer player) {
    if(player.getRole() == null){
      player.setRole(Role.UNDEFINED);
    }

    return player.getRole().equals(Role.SEEKER);
  }

  /**
   * Is spectator.
   *
   * @param player the player
   * @return the boolean
   */
  public boolean isSpectator(HideAndSeekPlayer player) {
    if(player.getRole() == null){
      player.setRole(Role.UNDEFINED);
    }

    return player.getRole().equals(Role.SPECTATOR);
  }

  /**
   * Gets seekers.
   *
   * @return the seekers
   */
  public ObjectList<HideAndSeekPlayer> getSeekers() {
    ObjectList<HideAndSeekPlayer> players = new ObjectArrayList<>();

    Bukkit.getOnlinePlayers().forEach(online -> {
      HideAndSeekPlayer hnsPlayer = HideAndSeekPlayer.get(online);

      if(hnsPlayer.getRole().equals(Role.SEEKER)){
        players.add(hnsPlayer);
      }
    });
    return players;
  }

  /**
   * Gets hiders.
   *
   * @return the hiders
   */
  public List<HideAndSeekPlayer> getHiders() {
    ObjectList<HideAndSeekPlayer> players = new ObjectArrayList<>();

    Bukkit.getOnlinePlayers().forEach(online -> {
      HideAndSeekPlayer hnsPlayer = HideAndSeekPlayer.get(online);

      if(hnsPlayer.getRole().equals(Role.HIDER)){
        players.add(hnsPlayer);
      }
    });
    return players;
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

    if (this.getSeekers().isEmpty() && !this.getHiders().isEmpty()) {
      assignNewSeeker();
    }

    if (this.getHiders().isEmpty()) {
      stop(HideAndSeekEndReason.SEEKER_WIN, true);
    }

    if (this.getSeekers().isEmpty()) {
      stop(HideAndSeekEndReason.HIDER_WIN, true);
    }
  }

  /**
   * Assign new seeker.
   */
  private void assignNewSeeker() {
    final HideAndSeekPlayer seeker = this.getHiders().stream().findAny().orElseThrow();

    seeker.setRole(Role.SEEKER);
    seeker.getPlayer().sendMessage(Messages.prefix().append(Component.text("Du bist jetzt ein Sucher!").color(NamedTextColor.GREEN)));
    seeker.prepareForGame();
    seeker.teleportSpawn();
  }

  /**
   * Teleport lobby.
   */
  public void teleportLobby() {
    Bukkit.getOnlinePlayers()
        .forEach(player -> player.teleport(HideAndSeekManager.INSTANCE.getLobbyLocation()));
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
