package dev.slne.hideandnseek.step.steps;

import dev.slne.hideandnseek.HideAndSeekEndReason;
import dev.slne.hideandnseek.HideAndSeekGame;
import dev.slne.hideandnseek.HideAndSeekGameState;
import dev.slne.hideandnseek.step.GameStep;
import dev.slne.hideandnseek.util.Continuation;
import dev.slne.hideandnseek.GameData;
import dev.slne.hideandnseek.timer.LobbyCountdown;
import java.time.Duration;
import java.util.Optional;
import org.bukkit.scoreboard.Team;

/**
 * The type Hide and seek lobby step.
 */
public class LobbyStep extends GameStep {

  private HideAndSeekGame game;
  private LobbyCountdown countdown;



  private final Duration time;

  /**
   * Instantiates a new Lobby lifecycle.
   *
   * @param game     the game
   * @param timeUnit the time unit
   * @param time     the time
   */
  public LobbyStep(HideAndSeekGame game, GameData gameData) {
    super(HideAndSeekGameState.LOBBY);

    this.game = game;
    this.time = gameData.getLobbyTime();
  }

  @Override
  public void load(Continuation continuation) {
    super.load(continuation);
    countdown = new LobbyCountdown(time);
    continuation.resume();
  }

  @Override
  public void start(Continuation continuation) {
    super.start(continuation);
    countdown.start(continuation);
  }

  @Override
  public void end(HideAndSeekEndReason reason, Continuation continuation) {
    super.end(reason, continuation);
  }

  @Override
  public void reset(Continuation continuation) {
    super.reset(continuation);

    Optional.ofNullable(hidersTeam).ifPresent(Team::unregister);
    Optional.ofNullable(seekersTeam).ifPresent(Team::unregister);
  }
}
