package dev.slne.hideandnseek.listener;

import dev.slne.hideandnseek.HideAndSeek;
import dev.slne.hideandnseek.listener.listeners.DeathListener;
import dev.slne.hideandnseek.listener.listeners.HungerListener;
import dev.slne.hideandnseek.listener.listeners.InteractListener;
import dev.slne.hideandnseek.listener.listeners.InventoryListener;
import dev.slne.hideandnseek.listener.listeners.RegenerationListener;
import dev.slne.hideandnseek.listener.listeners.RespawnListener;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * The type Listener manager.
 */
public enum ListenerManager {
  INSTANCE;

  /**
   * On enable.
   */
  public void onEnable() {
    register(new DeathListener());
    register(new InventoryListener());
    register(new RegenerationListener());
    register(new RespawnListener());
    register(new HungerListener());
    register(new InteractListener());
  }

  private void register(Listener listener) {
    Bukkit.getPluginManager().registerEvents(listener, HideAndSeek.getInstance());
  }

  /**
   * On disable.
   */
  public void onDisable() {
    HandlerList.unregisterAll(HideAndSeek.getInstance());
  }
}
