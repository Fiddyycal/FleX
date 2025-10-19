package org.fukkit.event.player;

import org.bukkit.event.Cancellable;
import org.fukkit.entity.FleXPlayer;

public class FleXPlayerMessageReceiveEvent extends FleXPlayerEvent implements Cancellable {
	
	private String message = null;
	
	private boolean cancel = false;
	
	public FleXPlayerMessageReceiveEvent(final FleXPlayer player, String message) {
		
		super(player, false);
		
		this.message = message;
		
	}
	
	public String getMessage() {
		return this.message;
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
