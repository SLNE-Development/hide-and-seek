package dev.slne.hideandnseek.player;

import dev.slne.hideandnseek.HideAndSeekGame;
import dev.slne.hideandnseek.HideAndSeekManager;
import dev.slne.hideandnseek.Items;
import java.util.UUID;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Emitter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * The type Hide and seek player.
 */
public class HideAndSeekPlayer {

  private final UUID uuid;

  /**
   * Instantiates a new Hide and seek player.
   *
   * @param uuid the uuid
   */
  public HideAndSeekPlayer(UUID uuid) {
    this.uuid = uuid;
  }

  /**
   * Get hide and seek player.
   *
   * @param uuid the uuid
   * @return the hide and seek player
   */
  public static HideAndSeekPlayer get(UUID uuid) {
    return HideAndSeekPlayerManager.INSTANCE.getPlayer(uuid);
  }

  /**
   * Get hide and seek player.
   *
   * @param player the player
   * @return the hide and seek player
   */
  public static HideAndSeekPlayer get(Player player) {
    return player == null ? null : HideAndSeekPlayer.get(player.getUniqueId());
  }

  /**
   * Give inventory.
   */
  public void giveSeekerInventory() {
    Items.prepareSeekerInventory(getPlayer());
  }

  /**
   * Gets uuid.
   *
   * @return the uuid
   */
  public UUID getUuid() {
    return uuid;
  }

  /**
   * Gets player.
   *
   * @return the player
   */
  public Player getPlayer() {
    return Bukkit.getPlayer(uuid);
  }

  /**
   * Gets offline player.
   *
   * @return the offline player
   */
  public OfflinePlayer getOfflinePlayer() {
    return Bukkit.getOfflinePlayer(uuid);
  }

  /**
   * Prepare for game.
   */
  public void prepareForGame() {
    getPlayer().getInventory().clear();
    getPlayer().getInventory().setArmorContents(null);
    getPlayer().setHealth(20);
    getPlayer().setFoodLevel(20);
    getPlayer().setSaturation(20);
    getPlayer().setFireTicks(0);
    getPlayer().setFlying(false);
    getPlayer().setAllowFlight(false);
    getPlayer().setGameMode(GameMode.ADVENTURE);
  }

  /**
   * Teleport spawn.
   */
  public void teleportSpawn() {
    getPlayer().teleportAsync(HideAndSeekManager.INSTANCE.getSpawnLocation())
        .thenAcceptAsync(result -> {
          getPlayer().playSound(
              Sound.sound().type(org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT).volume(.5f).build(),
              Emitter.self());
        });
  }

  /**
   * Is online boolean.
   *
   * @return the boolean
   */
  public boolean isOnline() {
    return getPlayer() != null && getPlayer().isOnline();
  }

  /**
   * Is hider boolean.
   *
   * @return the boolean
   */
  public boolean isHider() {
    HideAndSeekGame runningGame = HideAndSeekManager.INSTANCE.getRunningGame();

    return runningGame != null && runningGame.isHider(this);
  }

  /**
   * Teleport lobby.
   */
  public void teleportLobby() {
    getPlayer().teleportAsync(HideAndSeekManager.INSTANCE.getLobbyLocation())
        .thenAcceptAsync(result -> {
          getPlayer().playSound(
              Sound.sound().type(org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT).volume(.5f).build(),
              Emitter.self());
        });
  }

  /**
   * Is seeker boolean.
   *
   * @return the boolean
   */
  public boolean isSeeker() {
    HideAndSeekGame runningGame = HideAndSeekManager.INSTANCE.getRunningGame();

    return runningGame != null && runningGame.isSeeker(this);
  }
}
