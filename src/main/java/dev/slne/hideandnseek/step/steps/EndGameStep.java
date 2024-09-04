package dev.slne.hideandnseek.step.steps;

import dev.slne.hideandnseek.HideAndSeekEndReason;
import dev.slne.hideandnseek.HideAndSeekGameState;
import dev.slne.hideandnseek.HideAndSeekManager;
import dev.slne.hideandnseek.Settings;
import dev.slne.hideandnseek.step.GameStep;
import dev.slne.hideandnseek.util.Continuation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EndGameStep implements GameStep {

  @Override
  public HideAndSeekGameState getGameState() {
    return null;
  }

  @Override
  public void load() {

  }

  @Override
  public void start() {

  }

  @Override
  public void end(HideAndSeekEndReason reason) {

  }

  @Override
  public void reset(Continuation continuation) {
    super.reset(continuation);

    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      if (!onlinePlayer.hasPermission("hideandseek.kick.bypass")) {
        onlinePlayer.kick(Component.text("Das Spiel wurde beendet.", NamedTextColor.RED));
      }

      onlinePlayer.getInventory().clear();
    }

    Bukkit.getServer().setMaxPlayers(Settings.MAX_PLAYERS);
    HideAndSeekManager.INSTANCE.setRunningGame(null);
  }
}
