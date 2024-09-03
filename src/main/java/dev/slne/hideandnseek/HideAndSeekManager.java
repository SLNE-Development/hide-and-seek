package dev.slne.hideandnseek;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * The type Hide and seek manager.
 */
public class HideAndSeekManager {

  public static final HideAndSeekManager INSTANCE = new HideAndSeekManager();

  private final List<Player> bypassing;

  private HideAndSeekGame runningGame;

  private Location lobbyLocation;
  private int lobbyWorldBorderRadius;
  private int lobbyCountdown;
  private Location spawnLocation;

  /**
   * Instantiates a new Hide and seek manager.
   */
  public HideAndSeekManager() {
    this.bypassing = new ArrayList<>();
  }

  /**
   * On enable.
   */
  public void onEnable() {
    spawnLocation = HideAndSeek.getInstance().getConfig().getLocation("spawn");
    lobbyLocation = HideAndSeek.getInstance().getConfig().getLocation("lobby");
    lobbyWorldBorderRadius = HideAndSeek.getInstance().getConfig().getInt("lobbyWorldBorderRadius");
    lobbyCountdown = HideAndSeek.getInstance().getConfig().getInt("lobbyCountdown");
  }

  /**
   * On disable.
   */
  public void onDisable() {
  }

  /**
   * Gets bypassing.
   *
   * @return the bypassing
   */
  public List<Player> getBypassing() {
    return bypassing;
  }

  /**
   * Remove by passing.
   *
   * @param player the player
   */
  public void removeByPassing(Player player) {
    bypassing.remove(player);
  }

  /**
   * Add by passing.
   *
   * @param player the player
   */
  public void addByPassing(Player player) {
    bypassing.add(player);
  }

  /**
   * Is by passing boolean.
   *
   * @param player the player
   * @return the boolean
   */
  public boolean isByPassing(Player player) {
    return bypassing.contains(player);
  }

  /**
   * Gets spawn location.
   *
   * @return the spawn location
   */
  public Location getSpawnLocation() {
    return spawnLocation;
  }

  /**
   * Sets spawn location.
   *
   * @param playerLocation the player location
   */
  public void setSpawnLocation(Location playerLocation) {
    this.spawnLocation = playerLocation;

    HideAndSeek.getInstance().getConfig().set("spawn", playerLocation);
    HideAndSeek.getInstance().saveConfig();
  }

  /**
   * Gets running game.
   *
   * @return the running game
   */
  public HideAndSeekGame getRunningGame() {
    return runningGame;
  }

  /**
   * Sets running game.
   *
   * @param runningGame the running game
   */
  public void setRunningGame(HideAndSeekGame runningGame) {
    this.runningGame = runningGame;
  }

  /**
   * Gets lobby world border radius.
   *
   * @return the lobby world border radius
   */
  public int getLobbyWorldBorderRadius() {
    return lobbyWorldBorderRadius;
  }

  /**
   * Sets lobby location.
   *
   * @param lobbyLocation the lobby location
   */
  public void setLobbyLocation(Location lobbyLocation) {
    this.lobbyLocation = lobbyLocation;

    HideAndSeek.getInstance().getConfig().set("lobby", lobbyLocation);
    HideAndSeek.getInstance().saveConfig();
  }

  /**
   * Gets lobby location.
   *
   * @return the lobby location
   */
  public Location getLobbyLocation() {
    return lobbyLocation;
  }

  /**
   * Sets lobby world border radius.
   *
   * @param lobbyWorldBorderRadius the lobby world border radius
   */
  public void setLobbyWorldBorderRadius(int lobbyWorldBorderRadius) {
    this.lobbyWorldBorderRadius = lobbyWorldBorderRadius;

    HideAndSeek.getInstance().getConfig().set("lobbyWorldBorderRadius", lobbyWorldBorderRadius);
    HideAndSeek.getInstance().saveConfig();
  }

  public int getLobbyCountdown() {
    return lobbyCountdown;
  }

  public void setLobbyCountdown(int lobbyCountdown) {
    this.lobbyCountdown = lobbyCountdown;

    HideAndSeek.getInstance().getConfig().set("lobbyCountdown", lobbyCountdown);
    HideAndSeek.getInstance().saveConfig();
  }
}
