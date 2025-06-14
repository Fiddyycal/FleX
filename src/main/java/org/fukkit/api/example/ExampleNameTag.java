package org.fukkit.api.example;

import org.fukkit.entity.FleXPlayer;
import org.fukkit.scoreboard.playerlist.NameTag;

public class ExampleNameTag extends NameTag {

	public ExampleNameTag(FleXPlayer player, FleXPlayer viewer) {
		super(player, "Eg. Prefix", "Eg. Suffix", viewer);
	}

}
