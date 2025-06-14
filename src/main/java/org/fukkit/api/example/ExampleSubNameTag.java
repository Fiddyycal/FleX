package org.fukkit.api.example;

import org.fukkit.entity.FleXPlayer;
import org.fukkit.scoreboard.entry.TeamEntry;
import org.fukkit.scoreboard.playerlist.NameBar;
import org.fukkit.scoreboard.value.ColorValue;

public class ExampleSubNameTag extends NameBar {
	
	public ExampleSubNameTag(FleXPlayer player, String suffix) {
		super(player, new TeamEntry("&7Animate with ", () -> new ColorValue().get()), p -> p.getEntity().getEntityId());
	}
	
}
