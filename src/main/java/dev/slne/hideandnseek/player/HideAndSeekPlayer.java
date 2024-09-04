package dev.slne.hideandnseek.player;

import dev.slne.hideandnseek.HideAndSeekGame;
import dev.slne.hideandnseek.HideAndSeekManager;
import dev.slne.hideandnseek.Items;
import java.util.UUID;
import lombok.Getter;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Emitter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

/**
 * The type Hide and seek player.
 */
@Getter
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
    final Player player = getPlayer();
    final PlayerInventory playerInv = player.getInventory();

    playerInv.clear();
    playerInv.setArmorContents(null);

    player.setHealth(20);
    player.setFoodLevel(20);
    player.setSaturation(20);
    player.setFireTicks(0);
    player.setFlying(false);
    player.setAllowFlight(false);
    player.setGameMode(GameMode.ADVENTURE);
  }

  /**
   * Teleport spawn.
   */
  public void teleportSpawn() {
    getPlayer().teleportAsync(HideAndSeekManager.INSTANCE.getSpawnLocation())
        .thenRun(() -> getPlayer().playSound(
            Sound.sound()
                .type(org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT)
                .volume(.5f)
                .build(),
            Emitter.self()
        ));
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
    final HideAndSeekGame runningGame = HideAndSeekManager.INSTANCE.getRunningGame();

    return runningGame != null && runningGame.isHider(this);
  }

  /**
   * Teleport lobby.
   */
  public void teleportLobby() {
    getPlayer().teleportAsync(HideAndSeekManager.INSTANCE.getLobbyLocation())
        .thenRun(() -> getPlayer().playSound(
            Sound.sound()
                .type(org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT)
                .volume(.5f)
                .build(),
            Emitter.self()
        ));
  }

  /**
   * Is seeker boolean.
   *
   * @return the boolean
   */
  public boolean isSeeker() {
    final HideAndSeekGame runningGame = HideAndSeekManager.INSTANCE.getRunningGame();

    return runningGame != null && runningGame.isSeeker(this);
  }
}
