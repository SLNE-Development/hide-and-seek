package dev.slne.hideandnseek;

import dev.jorel.commandapi.wrappers.Location2D;
import dev.slne.hideandnseek.player.HideAndSeekPlayer;
import lombok.*;
import lombok.Builder.Default;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.time.Duration;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
public final class GameSettings {

  private @Default Duration lobbyTime = Duration.ofSeconds(60);
  private @Default Duration preparationTime = Duration.ofSeconds(120);
  private @Default Duration gameDuration = Duration.ofMinutes(60);
  private HideAndSeekPlayer initialSeeker;
  private World world;
  private @Default int initialRadius = 1000;
  private @Default int finalRadius = 25;
  private @Default boolean hidersBecomeSeekers = false;
  private @Default double worldBorderDamageAmount = 1;
  private @Default double worldBorderDamageBuffer = 0;
  private Location2D worldBorderCenter;
  private @Default boolean ohko = true;
  private @Default Duration endDuration = Duration.ofSeconds(60);

  public static GameSettings defaultSettings() {
    final World overworld = Bukkit.getWorlds().getFirst();
    Location spawnLocation = HideAndSeekManager.INSTANCE.getLobbyLocation();

    if(spawnLocation == null){
      spawnLocation = overworld.getSpawnLocation();
    }

    return GameSettings.builder()
        .world(overworld)
        .worldBorderCenter(new Location2D(overworld, spawnLocation.getX(), spawnLocation.getY(),
            spawnLocation.getZ(), spawnLocation.getYaw(), spawnLocation.getPitch()))
        .build();
  }
}
