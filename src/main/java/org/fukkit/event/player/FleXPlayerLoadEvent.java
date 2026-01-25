package org.fukkit.event.player;

import org.fukkit.entity.FleXPlayer;

public class FleXPlayerLoadEvent extends FleXPlayerEvent {
	
	private boolean offline;
	
	public FleXPlayerLoadEvent(FleXPlayer player, boolean offline) {
		
		super(player, false);
		
		this.offline = offline || !player.isOnline();
		
	}
	
	public boolean isOffline() {
		return this.offline;
	}
	
}
