package org.fukkit.event.player;

import org.bukkit.Location;
import org.fukkit.entity.FleXPlayer;

/**
 * 
 * This Event is called after the FleXPlayer
 * object is created and before it has loaded.
 * 
 * This is the event you should set a spawn location to.
 * 
 */
public class FleXPlayerConnectEvent extends FleXPlayerEvent {
	
	private Location spawn;
	
	public FleXPlayerConnectEvent(FleXPlayer player) {
		super(player, false);
	}
	
	public void setSpawnLocation(Location spawn) {
		this.spawn = spawn;
	}
	
	public Location getSpawnLocation() {
		return this.spawn;
	}
	
}
