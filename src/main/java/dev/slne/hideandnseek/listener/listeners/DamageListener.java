package dev.slne.hideandnseek.listener.listeners;

import dev.slne.hideandnseek.HideAndSeek;
import dev.slne.hideandnseek.HideAndSeekGame;
import dev.slne.hideandnseek.HideAndSeekManager;
import dev.slne.hideandnseek.player.HideAndSeekPlayer;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class DamageListener implements Listener {

  @EventHandler
  public void onProjectileHit(ProjectileHitEvent event) {
    if (!(event.getEntity().getShooter() instanceof Player damager)) {
      return;
    }

    if (!(event.getHitEntity() instanceof Player target)) {
      return;
    }

    if (HideAndSeekManager.INSTANCE.getGameSettings().isOhko()) {
      Bukkit.getScheduler()
          .runTaskLater(HideAndSeek.getInstance(), () -> target.damage(Float.MAX_VALUE, damager),
              1L);
    }
  }

  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof Player target)) {
      return;
    }

    final HideAndSeekGame game = HideAndSeekManager.INSTANCE.getRunningGame();
    if (game == null || !game.getGameState().isDamagable()) {
      event.setCancelled(true);
      return;
    }

    if (event.getDamager() instanceof Player damager) {
      if (game.isHider(HideAndSeekPlayer.get(damager))) {
        event.setCancelled(true);
        return;
      }

      if (game.isSeeker(HideAndSeekPlayer.get(damager)) && game.isSeeker(
          HideAndSeekPlayer.get(target))) {
        event.setCancelled(true);
        return;
      }

      if (HideAndSeekManager.INSTANCE.getGameSettings().isOhko()) {
        Bukkit.getScheduler()
            .runTaskLater(HideAndSeek.getInstance(), () -> target.damage(Float.MAX_VALUE, damager),
                1L);
      }
    }
  }

  @EventHandler
  public void onPrePlayerAttackEntity(PrePlayerAttackEntityEvent event) {
    final HideAndSeekGame game = HideAndSeekManager.INSTANCE.getRunningGame();
    if (game == null || !game.getGameState().isIngame()) {
      event.setCancelled(true);
    }

    HideAndSeekPlayer player = HideAndSeekPlayer.get(event.getPlayer());
    if (!player.isHider() && !player.isSeeker()) {
      event.setCancelled(true);
    }

  }
}
