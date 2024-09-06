package dev.slne.hideandnseek.step.steps;

import dev.slne.hideandnseek.HideAndSeekGameState;
import dev.slne.hideandnseek.HideAndSeekManager;
import dev.slne.hideandnseek.step.GameStep;
import dev.slne.hideandnseek.util.Continuation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EndGameStep extends GameStep {

  public EndGameStep() {
    super(HideAndSeekGameState.END);
  }

  @Override
  public void reset(Continuation continuation) {
    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      if (!onlinePlayer.hasPermission("hideandseek.kick.bypass")) {
        onlinePlayer.kick(Component.text("Das Spiel wurde beendet.", NamedTextColor.RED));
      }

      onlinePlayer.getInventory().clear();
    }

//    Bukkit.getServer().setMaxPlayers(Settings.MAX_PLAYERS); // TODO: not needed due to PreperationStep reset method
    continuation.resume();
    HideAndSeekManager.INSTANCE.setRunningGame(null);
  }
}
