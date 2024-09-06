package dev.slne.hideandnseek.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class TeamUtil {

  /**
   * Gets or create team.
   *
   * @param scoreboardManager the scoreboard manager
   * @param teamName          the team name
   * @return the team
   */
  public @NotNull Team getOrCreateTeam(String teamName) {
    final Scoreboard mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    Team team = mainScoreboard.getTeam(teamName);

    if (team == null) {
      team = mainScoreboard.registerNewTeam(teamName);
    }

    return team;
  }

  /**
   * Prepare team.
   *
   * @param team the team
   */
  public void prepareTeam(@NotNull Team team) {
    team.setAllowFriendlyFire(false);
    team.setCanSeeFriendlyInvisibles(false);
    team.setOption(Option.COLLISION_RULE, OptionStatus.NEVER);
    team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
  }

  public static void unregisterTeam(Team team) {
    team.unregister();
  }
}
