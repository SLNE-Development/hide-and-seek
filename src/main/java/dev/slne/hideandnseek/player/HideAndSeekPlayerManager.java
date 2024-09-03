package dev.slne.hideandnseek.player;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.time.Duration;
import java.util.UUID;

/**
 * The type Hide and seek player manager.
 */
public class HideAndSeekPlayerManager {

  public static final HideAndSeekPlayerManager INSTANCE = new HideAndSeekPlayerManager();

  private final LoadingCache<UUID, HideAndSeekPlayer> playerCache;

  /**
   * Instantiates a new Hide and seek player manager.
   */
  public HideAndSeekPlayerManager() {
    this.playerCache = Caffeine.newBuilder()
        .expireAfterAccess(Duration.ofMinutes(10))
        .build(HideAndSeekPlayer::new);
  }

  /**
   * Gets player cache.
   *
   * @return the player cache
   */
  public LoadingCache<UUID, HideAndSeekPlayer> getPlayerCache() {
    return playerCache;
  }

  /**
   * Gets player.
   *
   * @param uuid the uuid
   * @return the player
   */
  public HideAndSeekPlayer getPlayer(UUID uuid) {
    return playerCache.get(uuid);
  }
}
