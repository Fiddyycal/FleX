package org.fukkit.event.player;

import org.fukkit.entity.FleXHumanEntity;

/**
 * 
 * This is NOT an extension of FleXPlayerDisguiseEvent by design
 * You don't have to check if it's not instanceof FleXPlayerPreDisguiseEvent.
 *
 */
public class FleXPlayerDisguisedEvent extends FleXHumanEntityEvent {
	
	private boolean unDisguise;
	
	public FleXPlayerDisguisedEvent(FleXHumanEntity player, boolean unDisguise) {
		
		super(player, false);
		
		this.unDisguise = unDisguise;
		
	}
	
	public boolean isUnDisguise() {
		return this.unDisguise;
	}
	
}
