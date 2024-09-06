package dev.slne.hideandnseek.step;

import dev.slne.hideandnseek.HideAndSeekEndReason;
import dev.slne.hideandnseek.HideAndSeekGameState;
import dev.slne.hideandnseek.util.Continuation;

/**
 * The interface Game step.
 */
public abstract class GameStep {

  private final HideAndSeekGameState gameState;

  protected GameStep(HideAndSeekGameState gameState) {
    this.gameState = gameState;
  }

  /**
   * Gets game state.
   *
   * @return the game state
   */
  public final HideAndSeekGameState getGameState() {
    return gameState;
  }

  /**
   * Load.
   */
  public void load(Continuation continuation) {
    continuation.resume();
  }

  /**
   * Start.
   */
  public void start(Continuation continuation) {
    continuation.resume();
  }

  /**
   * End.
   *
   * @param reason the reason
   */
  public void end(HideAndSeekEndReason reason, Continuation continuation) {
    continuation.resume();
  }

  /**
   * Reset.
   */
  public void reset(Continuation continuation) {
    continuation.resume();
  }

}
