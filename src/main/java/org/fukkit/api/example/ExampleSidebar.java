package org.fukkit.api.example;

import org.fukkit.Fukkit;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.scoreboard.entry.ScoredTeamEntry;
import org.fukkit.scoreboard.sidebar.Minimizable;
import org.fukkit.scoreboard.sidebar.Sidebar;
import org.fukkit.scoreboard.value.ColorValue;

public class ExampleSidebar extends Sidebar implements Minimizable {

	public ExampleSidebar(FleXPlayer player) {
		
		super(player, "&4&lFleX&8: &7Example", 10L,
				
				new ScoredTeamEntry(14, " &8&m                            "),
				new ScoredTeamEntry(13, "&cCharacter length&8: &732"),
				new ScoredTeamEntry(12, "&cLine score&8: &712"),
				new ScoredTeamEntry(11, ""),
				new ScoredTeamEntry(10, "&cLine breaks&8: &aYes"),
				new ScoredTeamEntry(10, "&cDuplicate scores&8: &aYes"),
				new ScoredTeamEntry(9, ""),
				new ScoredTeamEntry(8, "&cVersion&8: &7" + Fukkit.getInstance().getDescription().getVersion()),
				new ScoredTeamEntry(7, "&cAnimate with ", () -> new ColorValue().get()),
				new ScoredTeamEntry(6, ""),
				new ScoredTeamEntry(5, "&cSee methods at&8:"),
				new ScoredTeamEntry(4, "&7org.fukkit.api.example"),
				new ScoredTeamEntry(3, ""),
				new ScoredTeamEntry(2, "&9&lFukkitApi&r.create();"),
				new ScoredTeamEntry(1, "&6&lnew &fExampleSidebar();")
				
		);
		
	}

	@Override
	public void minimize() {
		//
	}

	@Override
	public void maximize() {
		//
	}

	private boolean unchanged = true;
	
	@Override
	public void onUpdate() {
		
		this.setTitle((this.unchanged = !this.unchanged) ? "&4&lFleX&8: &7Example" : "&4&lFleX&8: &7Sidebar");
		
	}

}
