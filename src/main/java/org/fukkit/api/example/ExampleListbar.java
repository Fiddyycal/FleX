package org.fukkit.api.example;

import org.fukkit.entity.FleXPlayer;
import org.fukkit.scoreboard.entry.ScoredTeamEntry;
import org.fukkit.scoreboard.playerlist.ListScore;

public class ExampleListbar extends ListScore {

	public ExampleListbar(FleXPlayer player) {
		super(player, new ScoredTeamEntry(1));
	}

}
