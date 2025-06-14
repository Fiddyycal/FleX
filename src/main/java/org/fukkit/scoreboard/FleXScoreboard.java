package org.fukkit.scoreboard;

import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public interface FleXScoreboard {
	
	public Objective getObjective();
	
	public Scoreboard getScoreboard();
	
	public void setIntervals(long ticks);
	
	public void onTick();
	
	public void clear();

}
