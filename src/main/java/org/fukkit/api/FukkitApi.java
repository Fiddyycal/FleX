package org.fukkit.api;

import java.util.stream.IntStream;

import org.fukkit.entity.FleXPlayer;
import org.fukkit.scoreboard.entry.ScoredTeamEntry;
import org.fukkit.scoreboard.sidebar.Sidebar;

public class FukkitApi {
	
	public static Sidebar createSidebar(FleXPlayer player, String title, ScoredTeamEntry... lines) {
		return new Sidebar(player, title, lines);
	}
	
	public static Sidebar createSidebar(FleXPlayer player, String title, String... lines) {
		
		ScoredTeamEntry[] entries = IntStream.range(0, lines.length).mapToObj(i -> {
			
			return new ScoredTeamEntry(i, lines[i]);
			
		}).toArray(t -> new ScoredTeamEntry[lines.length]);
		
		return new Sidebar(player, title, entries);
	}

}
