package silence.simsool.lucent.general.utils;

import static silence.simsool.lucent.Lucent.mc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public class ScoreboardUtils {

	public static List<String> getSidebarScores(Scoreboard scoreboard, Objective objective) {
		List<String> lines = new ArrayList<>();
		
		for (PlayerScoreEntry score : scoreboard.listPlayerScores(objective)) {
			String owner = score.owner();
			PlayerTeam team = scoreboard.getPlayersTeam(owner);
			
			String prefix = team != null ? team.getPlayerPrefix().getString() : "";
			String suffix = team != null ? team.getPlayerSuffix().getString() : "";
			
			lines.add(prefix + owner + suffix);
		}
		
		return lines;
	}

	public static List<String> getSidebarLines() {
		List<String> lines = new ArrayList<>(); if (mc.level == null) return lines;
		Scoreboard scoreboard = mc.level.getScoreboard(); if (scoreboard == null) return lines;
		Objective objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR); if (objective == null) return lines;
		return ScoreboardUtils.getSidebarScores(scoreboard, objective);
	}

	public static String getSiderbarTitle() {
		if (mc.level == null) return "NO SIDEBAR";
		Scoreboard scoreboard = mc.level.getScoreboard();
		Objective sidebarObjective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
		if (sidebarObjective != null) return sidebarObjective.getDisplayName().getString();
		else return "NO SIDEBAR";
	}

	public static String cleanSB(String scoreboard) {
		char[] nvString = scoreboard.replaceAll("\u00A7.", "").toCharArray();
		StringBuilder cleaned = new StringBuilder();
		for (char c : nvString) {
			if (((int) c > 20 && (int) c < 127) || c == '⏣' || c == '☽') cleaned.append(c);
		}
		return cleaned.toString();
	}

	public static List<String> getTablistInfo(boolean FORMAT) {
		List<String> list = new ArrayList<>();
		if (mc.getConnection() == null) return list;
		Collection<PlayerInfo> players = new ArrayList<>(mc.getConnection().getOnlinePlayers());
		for (PlayerInfo entry : players) {
			if (entry != null) {
				String display = entry.getTabListDisplayName() != null ? entry.getTabListDisplayName().getString() : entry.getProfile().name();
				list.add(display);
			}
		}
		return list;
	}

}