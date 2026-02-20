package org.fukkit.event.player;

import org.fukkit.entity.FleXHumanEntity;

public class FleXPlayerMaskedEvent extends FleXHumanEntityEvent {
	
	private boolean unMask;
	
	public FleXPlayerMaskedEvent(FleXHumanEntity player, boolean unMask) {
		
		super(player, false);
		
		this.unMask = unMask;
		
	}
	
	public boolean isUnMask() {
		return this.unMask;
	}
	
}
