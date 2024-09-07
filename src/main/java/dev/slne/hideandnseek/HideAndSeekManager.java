package dev.slne.hideandnseek;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * The type Hide and seek manager.
 */
public enum HideAndSeekManager {
  INSTANCE;

  private final ObjectSet<UUID> bypassing;

  @Getter(lazy = true)
  private final GameSettings gameSettings = GameSettings.defaultSettings();

  @Setter
  @Getter
  private HideAndSeekGame runningGame;

  @Getter
  private Location lobbyLocation;

  @Getter
  private int lobbyWorldBorderRadius;

  @Getter
  private int lobbyCountdown;

  @Getter
  private Location spawnLocation;

  /**
   * Instantiates a new Hide and seek manager.
   */
  HideAndSeekManager() {
    this.bypassing = new ObjectOpenHashSet<>();
  }

  /**
   * On enable.
   */
  public void onEnable() {
    final FileConfiguration config = HideAndSeek.getInstance().getConfig();

    spawnLocation = config.getLocation("spawn");
    lobbyLocation = config.getLocation("lobby");
    lobbyWorldBorderRadius = config.getInt("lobbyWorldBorderRadius");
    lobbyCountdown = config.getInt("lobbyCountdown");
  }

  /**
   * On disable.
   */
  public void onDisable() {
  }

  public boolean isBypassing(Player player) {
    return bypassing.contains(player.getUniqueId());
  }

  public void addBypassing(Player player) {
    bypassing.add(player.getUniqueId());
  }

  public void removeBypassing(Player player) {
    bypassing.remove(player.getUniqueId());
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
   * Sets lobby world border radius.
   *
   * @param lobbyWorldBorderRadius the lobby world border radius
   */
  public void setLobbyWorldBorderRadius(int lobbyWorldBorderRadius) {
    this.lobbyWorldBorderRadius = lobbyWorldBorderRadius;

    HideAndSeek.getInstance().getConfig().set("lobbyWorldBorderRadius", lobbyWorldBorderRadius);
    HideAndSeek.getInstance().saveConfig();
  }

  public void setLobbyCountdown(int lobbyCountdown) {
    this.lobbyCountdown = lobbyCountdown;

    HideAndSeek.getInstance().getConfig().set("lobbyCountdown", lobbyCountdown);
    HideAndSeek.getInstance().saveConfig();
  }
}
