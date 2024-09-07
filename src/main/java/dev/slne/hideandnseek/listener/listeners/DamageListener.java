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

public class DamageListener implements Listener {

  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof Player target)) {
      return;
    }

    if (!(event.getDamager() instanceof Player damager)) {
      return;
    }

    final HideAndSeekGame game = HideAndSeekManager.INSTANCE.getRunningGame();
    if (game == null || !game.getGameState().isIngame() || game.isHider(HideAndSeekPlayer.get(damager))) {
      event.setCancelled(true);
      return;
    }

    if (HideAndSeekManager.INSTANCE.getGameSettings().isOhko()) {
      Bukkit.getScheduler()
          .runTaskLater(HideAndSeek.getInstance(), () -> target.damage(Float.MAX_VALUE, damager),
              1L);
    }
  }

  @EventHandler
  public void onPrePlayerAttackEntity(PrePlayerAttackEntityEvent event) {
    final HideAndSeekGame game = HideAndSeekManager.INSTANCE.getRunningGame();
    if (game == null || !game.getGameState().isIngame()) {
      event.setCancelled(true);
    }
  }
}
