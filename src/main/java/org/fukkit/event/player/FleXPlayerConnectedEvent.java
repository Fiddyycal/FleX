package org.fukkit.event.player;

import org.fukkit.entity.FleXPlayer;

/**
 * 
 * This Event is called after the FleXPlayer
 * object is created, loaded and teleported.
 * 
 * Not to be confused with FleXPlayerConnectEvent.
 * 
 */
public class FleXPlayerConnectedEvent extends FleXPlayerEvent {
	
	public FleXPlayerConnectedEvent(FleXPlayer player) {
		super(player, false);
	}
	
}
