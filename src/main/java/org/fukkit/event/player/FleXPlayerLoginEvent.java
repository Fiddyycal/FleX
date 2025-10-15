package org.fukkit.event.player;

import org.bukkit.Location;
import org.fukkit.entity.FleXPlayer;

/**
 * 
 * This Event is called after the FleXPlayer
 * object is created and before it has loaded.
 * 
 */
public class FleXPlayerLoginEvent extends FleXPlayerEvent {
	
	private Location spawn;
	
	public FleXPlayerLoginEvent(FleXPlayer player) {
		super(player, false);
	}
	
	public void setSpawnLocation(Location spawn) {
		this.spawn = spawn;
	}
	
	public Location getSpawnLocation() {
		return this.spawn;
	}
	
}
