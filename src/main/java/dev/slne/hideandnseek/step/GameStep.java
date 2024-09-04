package dev.slne.hideandnseek.step;

import dev.slne.hideandnseek.HideAndSeekEndReason;
import dev.slne.hideandnseek.HideAndSeekGameState;
import dev.slne.hideandnseek.step.GameStepManager.Continuation;
import javax.annotation.OverridingMethodsMustInvokeSuper;

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
  @OverridingMethodsMustInvokeSuper
  public void load(Continuation continuation) {

  }

  /**
   * Start.
   */
  @OverridingMethodsMustInvokeSuper
  public void start(Continuation continuation) {

  }

  /**
   * End.
   *
   * @param reason the reason
   */
  @OverridingMethodsMustInvokeSuper
  public void end(HideAndSeekEndReason reason, Continuation continuation) {

  }

  /**
   * Reset.
   */
  @OverridingMethodsMustInvokeSuper
  public void reset() {

  }

}
