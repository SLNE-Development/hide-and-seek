package dev.slne.hideandnseek;

/**
 * The enum Hide and seek game state.
 */
public enum HideAndSeekGameState {

  LOBBY,
  PREPARING,
  INGAME,
  ENDING,
  END, UNKNOWN;

  /**
   * Is preparing boolean.
   *
   * @return the boolean
   */
  public boolean isPreparing() {
    return this.equals(PREPARING);
  }

  /**
   * Is ending boolean.
   *
   * @return the boolean
   */
  public boolean isEnding() {
    return this.equals(ENDING);
  }

  /**
   * Is lobby boolean.
   *
   * @return the boolean
   */
  public boolean isLobby() {
    return this.equals(LOBBY);
  }

  /**
   * Is end boolean.
   *
   * @return the boolean
   */
  public boolean isEnd() {
    return this.equals(END);
  }

  /**
   * Is ingame boolean.
   *
   * @return the boolean
   */
  public boolean isIngame() {
    return this.equals(INGAME);
  }

  /**
   * Is damagable boolean.
   *
   * @return the boolean
   */
  public boolean isDamagable() {
    return this.equals(INGAME) || this.equals(PREPARING);
  }
}
