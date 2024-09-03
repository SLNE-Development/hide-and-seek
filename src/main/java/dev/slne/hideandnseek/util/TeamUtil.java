package dev.slne.hideandnseek.util;

import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class TeamUtil {

  /**
   * Gets or create team.
   *
   * @param scoreboardManager the scoreboard manager
   * @param teamName          the team name
   * @return the team
   */
  public static Team getOrCreateTeam(ScoreboardManager scoreboardManager, String teamName) {
    Team team = scoreboardManager.getMainScoreboard().getTeam(teamName);

    if (team == null) {
      team = scoreboardManager.getMainScoreboard().registerNewTeam(teamName);
    }

    return team;
  }

  /**
   * Prepare team.
   *
   * @param team the team
   */
  public static void prepareTeam(Team team) {
    team.setAllowFriendlyFire(false);
    team.setCanSeeFriendlyInvisibles(false);
    team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
    team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
  }
}
