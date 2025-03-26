package dev.slne.hideandnseek.old.listener;

import dev.slne.hideandnseek.old.HideAndSeek;
import dev.slne.hideandnseek.old.listener.listeners.ConnectionListener;
import dev.slne.hideandnseek.old.listener.listeners.DamageListener;
import dev.slne.hideandnseek.old.listener.listeners.DeathListener;
import dev.slne.hideandnseek.old.listener.listeners.HungerListener;
import dev.slne.hideandnseek.old.listener.listeners.InteractListener;
import dev.slne.hideandnseek.old.listener.listeners.InventoryListener;
import dev.slne.hideandnseek.old.listener.listeners.PlayerMoveListener;
import dev.slne.hideandnseek.old.listener.listeners.PotteryProtectionListener;
import dev.slne.hideandnseek.old.listener.listeners.RegenerationListener;
import dev.slne.hideandnseek.old.listener.listeners.RespawnListener;
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
    register(new DamageListener());
    register(new ConnectionListener());
    register(new PlayerMoveListener());
    register(new PotteryProtectionListener());
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
