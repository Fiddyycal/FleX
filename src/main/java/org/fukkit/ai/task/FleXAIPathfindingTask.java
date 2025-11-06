package org.fukkit.ai.task;

import org.bukkit.Location;

public interface FleXAIPathfindingTask extends FleXAIRepeatingTask {
	
	public Location getLocation();
	
	public void setLocation(Location location);
	
}
