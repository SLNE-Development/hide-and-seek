package dev.slne.hideandnseek.old.player;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.time.Duration;
import java.util.UUID;

/**
 * The type Hide and seek player manager.
 */
public enum HideAndSeekPlayerManager {
  INSTANCE;

  private final LoadingCache<UUID, HideAndSeekPlayer> playerCache;

  /**
   * Instantiates a new Hide and seek player manager.
   */
  HideAndSeekPlayerManager() {
    this.playerCache = Caffeine.newBuilder()
        .expireAfterAccess(Duration.ofMinutes(10))
        .build(HideAndSeekPlayer::new);
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
