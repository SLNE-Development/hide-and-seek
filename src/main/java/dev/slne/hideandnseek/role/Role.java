package dev.slne.hideandnseek.role;

import dev.slne.hideandnseek.HideAndSeekGame;
import dev.slne.hideandnseek.player.HideAndSeekPlayer;

public enum Role {
  SEEKER, HIDER, SPECTATOR, UNDEFINED;

  public static boolean isSeeker(HideAndSeekGame game, HideAndSeekPlayer player){
    return game.isSeeker(player);
  }

  public static boolean isHider(HideAndSeekGame game, HideAndSeekPlayer player){
    return game.isHider(player);
  }

  public static boolean isSpectator(HideAndSeekGame game, HideAndSeekPlayer player){
    return game.isSpectator(player);
  }

  public static Role getRole(String name){
    switch (name.toLowerCase()){
      case "seeker" -> {
        return SEEKER;
      }
      case "hider" -> {
        return HIDER;
      }
      case "spectator" -> {
        return SPECTATOR;
      }
      case "undefined" -> {
        return UNDEFINED;
      }
    }
    return null;
  }
}
