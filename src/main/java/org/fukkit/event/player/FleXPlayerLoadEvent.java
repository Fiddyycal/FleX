package org.fukkit.event.player;

import org.fukkit.entity.FleXPlayer;

import net.md_5.fungee.event.FleXPlayerEvent;

public class FleXPlayerLoadEvent extends FleXPlayerEvent {
	
	private boolean offline;
	
	public FleXPlayerLoadEvent(FleXPlayer player, boolean offline) {
		
		super(player, false);
		
		this.offline = offline;
		
	}
	
	public boolean isOffline() {
		return this.offline;
	}
	
}
