package dev.slne.hideandnseek.listener.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class PotteryProtectionListener implements Listener {
  @EventHandler
  public void onProjectileHit(ProjectileHitEvent event) {
    if(event.getHitBlock() == null){
      return;
    }

    if(event.getHitBlock().getType() != Material.DECORATED_POT){
      return;
    }

    event.setCancelled(true);
  }
}
