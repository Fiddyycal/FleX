package org.fukkit.event.player;

import org.fukkit.entity.FleXPlayer;

import net.md_5.fungee.event.FleXPlayerEvent;

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
