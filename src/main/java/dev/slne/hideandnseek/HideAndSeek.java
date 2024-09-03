package dev.slne.hideandnseek;

import dev.slne.hideandnseek.command.HideAndSeekCommand;
import dev.slne.hideandnseek.listener.ListenerManager;
import java.security.SecureRandom;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The type Hide and seek.
 */
public final class HideAndSeek extends JavaPlugin {

  public static final SecureRandom RANDOM = new SecureRandom();

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
    new HideAndSeekCommand("hideandseek").register();

    HideAndSeekManager.INSTANCE.onEnable();
  }

  @Override
  public void onDisable() {
    ListenerManager.INSTANCE.onDisable();
    HideAndSeekManager.INSTANCE.onDisable();
  }
}
