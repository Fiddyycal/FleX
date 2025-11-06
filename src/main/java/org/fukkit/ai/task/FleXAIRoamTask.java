package org.fukkit.ai.task;

import org.bukkit.Location;

public interface FleXAIRoamTask extends FleXAIPathfindingTask {
	
	public Location getStartingLocation();
	
	public void setStartingLocation(Location locaiton);
	
}
