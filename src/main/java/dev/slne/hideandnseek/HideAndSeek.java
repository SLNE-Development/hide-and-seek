package dev.slne.hideandnseek;

import dev.slne.hideandnseek.command.HideAndSeekCommand;
import dev.slne.hideandnseek.listener.ListenerManager;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The type Hide and seek.
 */
public final class HideAndSeek extends JavaPlugin {

  private static final ComponentLogger LOGGER = ComponentLogger.logger("HideAndSeek");
  public static final SecureRandom RANDOM;

  static {
    SecureRandom tempRandom;
    try {
      tempRandom = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {
      tempRandom = new SecureRandom();

      LOGGER.error("Failed to create a secure random instance, falling back to a less secure one");
    }

    RANDOM = tempRandom;
  }

  /**
   * Returns the plugin instance
   *
   * @return the plugin instance, which is collected by using the getPlugin Method in
   * {@link JavaPlugin#getPlugin}
   */
  public static HideAndSeek getInstance() {
    return getPlugin(HideAndSeek.class);
  }

  @Override
  public void onEnable() {
    saveDefaultConfig();

    ListenerManager.INSTANCE.onEnable();
    HideAndSeekManager.INSTANCE.onEnable();
    new HideAndSeekCommand("hideandseek").register();

  }

  @Override
  public void onDisable() {
    ListenerManager.INSTANCE.onDisable();
    HideAndSeekManager.INSTANCE.onDisable();
  }
}
