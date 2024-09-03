package dev.slne.hideandnseek.listener;

import dev.slne.hideandnseek.HideAndSeek;
import dev.slne.hideandnseek.listener.listeners.DeathListener;
import dev.slne.hideandnseek.listener.listeners.HungerListener;
import dev.slne.hideandnseek.listener.listeners.InteractListener;
import dev.slne.hideandnseek.listener.listeners.InventoryListener;
import dev.slne.hideandnseek.listener.listeners.RegenerationListener;
import dev.slne.hideandnseek.listener.listeners.RespawnListener;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * The type Listener manager.
 */
public class ListenerManager {

  public static final ListenerManager INSTANCE = new ListenerManager();
  private final List<Listener> listeners;

  /**
   * Instantiates a new Listener manager.
   */
  public ListenerManager() {
    this.listeners = new ArrayList<>();

    this.listeners.add(new DeathListener());
    this.listeners.add(new InventoryListener());
    this.listeners.add(new RegenerationListener());
    this.listeners.add(new RespawnListener());
    this.listeners.add(new HungerListener());
    this.listeners.add(new InteractListener());
  }

  /**
   * On enable.
   */
  public void onEnable() {
    listeners.forEach(
        listener -> Bukkit.getPluginManager().registerEvents(listener, HideAndSeek.getInstance()));
  }

  /**
   * On disable.
   */
  public void onDisable() {
    listeners.forEach(HandlerList::unregisterAll);
  }
}
