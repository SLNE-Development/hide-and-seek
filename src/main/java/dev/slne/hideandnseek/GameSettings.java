package dev.slne.hideandnseek;

import dev.jorel.commandapi.wrappers.Location2D;
import dev.slne.hideandnseek.player.HideAndSeekPlayer;
import java.time.Duration;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
public final class GameSettings {

  private @Default Duration lobbyTime = Duration.ofSeconds(5);
  private @Default Duration preparationTime = Duration.ofSeconds(5);
  private @Default Duration gameDuration = Duration.ofSeconds(5);
  private HideAndSeekPlayer initialSeeker;
  private World world;
  private @Default int initialRadius = 100;
  private @Default int finalRadius = 10;
  private @Default boolean hidersBecomeSeekers = false;
  private @Default double worldBorderDamageAmount = 1;
  private @Default double worldBorderDamageBuffer = 0;
  private Location2D worldBorderCenter;
  private @Default boolean ohko = true;
  private @Default Duration endDuration = Duration.ofSeconds(5);

  public static GameSettings defaultSettings() {
    final World overworld = Bukkit.getWorlds().getFirst();
    final Location spawnLocation = HideAndSeekManager.INSTANCE.getLobbyLocation();

    return GameSettings.builder()
        .world(overworld)
        .worldBorderCenter(new Location2D(overworld, spawnLocation.getX(), spawnLocation.getY(),
            spawnLocation.getZ(), spawnLocation.getYaw(), spawnLocation.getPitch()))
        .build();
  }
}
