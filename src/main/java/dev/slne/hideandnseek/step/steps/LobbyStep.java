package dev.slne.hideandnseek.step.steps;

import dev.slne.hideandnseek.HideAndSeek;
import dev.slne.hideandnseek.HideAndSeekEndReason;
import dev.slne.hideandnseek.HideAndSeekGame;
import dev.slne.hideandnseek.HideAndSeekGameState;
import dev.slne.hideandnseek.step.GameStep;
import dev.slne.hideandnseek.step.GameStepManager.Continuation;
import dev.slne.hideandnseek.step.GameStepManager.GameData;
import dev.slne.hideandnseek.timer.LobbyCountdown;
import dev.slne.hideandnseek.util.TeamUtil;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

/**
 * The type Hide and seek lobby step.
 */
public class LobbyStep extends GameStep {

  private HideAndSeekGame game;
  private LobbyCountdown countdown;

  private Team hidersTeam;
  private Team seekersTeam;

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

    final ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();

    hidersTeam = TeamUtil.getOrCreateTeam(scoreboardManager, "hiders");
    seekersTeam = TeamUtil.getOrCreateTeam(scoreboardManager, "seekers");

    TeamUtil.prepareTeam(hidersTeam);
    TeamUtil.prepareTeam(seekersTeam);

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
  public void reset() {
    super.reset();

    Optional.ofNullable(hidersTeam).ifPresent(Team::unregister);
    Optional.ofNullable(seekersTeam).ifPresent(Team::unregister);
  }
}
