package dev.slne.hideandnseek.step;

import dev.slne.hideandnseek.HideAndSeekEndReason;
import dev.slne.hideandnseek.HideAndSeekGameState;

/**
 * The interface Game step.
 */
public interface GameStep {

  /**
   * Gets game state.
   *
   * @return the game state
   */
  HideAndSeekGameState getGameState();

  /**
   * Load.
   */
  void load();

  /**
   * Start.
   */
  void start();

  /**
   * End.
   *
   * @param reason the reason
   */
  void end(HideAndSeekEndReason reason);

  /**
   * Reset.
   */
  void reset();

}
