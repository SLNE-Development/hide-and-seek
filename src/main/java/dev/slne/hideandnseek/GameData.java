package dev.slne.hideandnseek;

import dev.slne.hideandnseek.player.HideAndSeekPlayer;
import java.time.Duration;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.World;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class GameData {

  private final Duration lobbyTime;
  private final Duration preparationTime;
  private final Duration gameDuration;
  private final Duration shrinkTime;
  private final Duration endingTime; // TODO: 06.09.2024 21:45 - set
  private final HideAndSeekPlayer initialSeeker;
  private final World world;
  private final int initialRadius;
  private final int finalRadius; // TODO: 04.09.2024 21:49 - not used?
  private final boolean hidersBecomeSeekers;
}
