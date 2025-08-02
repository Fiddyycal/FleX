package org.fukkit.event.player;

import org.fukkit.entity.FleXPlayer;

/**
 * 
 * This Event is called after the FleXPlayer
 * object is created and before it has loaded.
 * 
 */
public class FleXPlayerLoginEvent extends FleXPlayerEvent {
	
	public FleXPlayerLoginEvent(FleXPlayer player) {
		super(player, false);
	}
	
}
