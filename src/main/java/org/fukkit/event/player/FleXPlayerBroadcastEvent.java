package org.fukkit.event.player;

import org.bukkit.event.Cancellable;
import org.fukkit.entity.FleXPlayer;

public class FleXPlayerBroadcastEvent extends FleXPlayerEvent implements Cancellable {

	private String announcement;
	
	private boolean cancel = false;
	
	public FleXPlayerBroadcastEvent(FleXPlayer player, String broadcast) {
		
		super(player, false);
		
		this.announcement = broadcast;
		
	}
	
	public String getMessage() {
		return this.announcement;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	@Override
	public boolean isCancelled() {
		return this.cancel;
	}

}
