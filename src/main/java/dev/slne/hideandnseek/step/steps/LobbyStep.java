package dev.slne.hideandnseek.step.steps;

import dev.slne.hideandnseek.HideAndSeek;
import dev.slne.hideandnseek.HideAndSeekEndReason;
import dev.slne.hideandnseek.HideAndSeekGame;
import dev.slne.hideandnseek.HideAndSeekGameState;
import dev.slne.hideandnseek.step.GameStep;
import dev.slne.hideandnseek.timer.LobbyCountdown;
import dev.slne.hideandnseek.util.TeamUtil;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

/**
 * The type Hide and seek lobby step.
 */
public class LobbyStep implements GameStep {

  private HideAndSeekGame game;
  private LobbyCountdown countdown;

  private Team hidersTeam;
  private Team seekersTeam;

  private final TimeUnit timeUnit;
  private final long time;

  /**
   * Instantiates a new Lobby lifecycle.
   *
   * @param game     the game
   * @param timeUnit the time unit
   * @param time     the time
   */
  public LobbyStep(HideAndSeekGame game, TimeUnit timeUnit, long time) {
    this.game = game;
    this.timeUnit = timeUnit;
    this.time = time;
  }

  @Override
  public HideAndSeekGameState getGameState() {
    return HideAndSeekGameState.LOBBY;
  }

  @Override
  public void load() {
    ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();

    hidersTeam = TeamUtil.getOrCreateTeam(scoreboardManager, "hiders");
    TeamUtil.prepareTeam(hidersTeam);

    seekersTeam = TeamUtil.getOrCreateTeam(scoreboardManager, "seekers");
    TeamUtil.prepareTeam(seekersTeam);

    countdown = new LobbyCountdown(this, timeUnit, time);
  }

  @Override
  public void start() {
    countdown.runTaskTimer(HideAndSeek.getInstance(), 0, 20);
  }

  @Override
  public void end(HideAndSeekEndReason reason) {
    countdown.cancel();
  }

  @Override
  public void reset() {
    Optional.ofNullable(hidersTeam).ifPresent(Team::unregister);
    Optional.ofNullable(seekersTeam).ifPresent(Team::unregister);
  }
}
