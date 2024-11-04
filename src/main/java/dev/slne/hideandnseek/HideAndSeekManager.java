package dev.slne.hideandnseek;

import dev.jorel.commandapi.wrappers.Location2D;
import dev.slne.hideandnseek.player.HideAndSeekPlayer;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.time.Duration;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * The type Hide and seek manager.
 */
public enum HideAndSeekManager {
  INSTANCE;

  private final ObjectSet<UUID> bypassing;

  @Getter(lazy = true)
  private final GameSettings gameSettings = this.load();

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
    HideAndSeekManager.INSTANCE.save();
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

  public void save() {
    GameSettings settings = this.getGameSettings();
    FileConfiguration config = HideAndSeek.getInstance().getConfig();

    config.set("settings.lobbyTime", settings.getLobbyTime().toSeconds());
    config.set("settings.preparationTime", settings.getPreparationTime().toSeconds());
    config.set("settings.gameDuration", settings.getGameDuration().toMinutes());
    config.set("settings.initialRadius", settings.getInitialRadius());
    config.set("settings.finalRadius", settings.getFinalRadius());
    config.set("settings.hidersBecomeSeekers", settings.isHidersBecomeSeekers());
    config.set("settings.worldBorderDamageAmount", settings.getWorldBorderDamageAmount());
    config.set("settings.worldBorderDamageBuffer", settings.getWorldBorderDamageBuffer());
    config.set("settings.ohko", settings.isOhko());
    config.set("settings.endDuration", settings.getEndDuration().toSeconds());
    config.set("settings.world", settings.getWorld().getName());

    if (settings.getInitialSeeker() != null) {
      config.set("settings.initialSeeker", settings.getInitialSeeker().getUuid());
    }

    Location2D center = settings.getWorldBorderCenter();
    if (center != null) {
      config.set("settings.worldBorderCenter.x", center.getX());
      config.set("settings.worldBorderCenter.z", center.getZ());
    }

    HideAndSeek.getInstance().saveConfig();
  }


  public GameSettings load() {
    FileConfiguration config = HideAndSeek.getInstance().getConfig();

    Duration lobbyTime = Duration.ofSeconds(config.getLong("settings.lobbyTime", 10));
    Duration preparationTime = Duration.ofSeconds(config.getLong("settings.preparationTime", 30));
    Duration gameDuration = Duration.ofMinutes(config.getLong("settings.gameDuration", 5));
    Duration endDuration = Duration.ofSeconds(config.getLong("settings.endDuration", 15));
    String seeker = config.getString("settings.initialSeeker");

    int initialRadius = config.getInt("settings.initialRadius", 1000);
    int finalRadius = config.getInt("settings.finalRadius", 100);
    boolean hidersBecomeSeekers = config.getBoolean("settings.hidersBecomeSeekers", false);
    double worldBorderDamageAmount = config.getDouble("settings.worldBorderDamageAmount", 1);
    double worldBorderDamageBuffer = config.getDouble("settings.worldBorderDamageBuffer", 0);
    boolean ohko = config.getBoolean("settings.ohko", true);

    World world = Bukkit.getWorld(config.getString("settings.world", Bukkit.getWorlds().getFirst().getName()));
    HideAndSeekPlayer initialSeeker = null;
    Location2D worldBorderCenter = null;

    if (seeker != null && !seeker.isBlank()) {
      initialSeeker = HideAndSeekPlayer.get(UUID.fromString(seeker));
    }

    if (config.contains("settings.worldBorderCenter.x") && config.contains("settings.worldBorderCenter.z")) {
      double x = config.getDouble("settings.worldBorderCenter.x");
      double z = config.getDouble("settings.worldBorderCenter.z");

      worldBorderCenter = new Location2D(world, x, z);
    }else{
      worldBorderCenter = new Location2D(world, world.getSpawnLocation().x(), world.getSpawnLocation().z());
    }

    return GameSettings.builder()
        .lobbyTime(lobbyTime)
        .preparationTime(preparationTime)
        .gameDuration(gameDuration)
        .initialRadius(initialRadius)
        .finalRadius(finalRadius)
        .hidersBecomeSeekers(hidersBecomeSeekers)
        .worldBorderDamageAmount(worldBorderDamageAmount)
        .worldBorderDamageBuffer(worldBorderDamageBuffer)
        .ohko(ohko)
        .endDuration(endDuration)
        .world(world)
        .initialSeeker(initialSeeker)
        .worldBorderCenter(worldBorderCenter)
        .build();
  }
}
