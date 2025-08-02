package org.fukkit.event.player;

import org.bukkit.event.Cancellable;
import org.fukkit.entity.FleXPlayer;

public class FleXPlayerAsyncChatReceiveEvent extends FleXPlayerEvent implements Cancellable {
	
	private FleXPlayer recipient;
	
	private String message = null;
	
	private boolean cancel = false;
	
	public FleXPlayerAsyncChatReceiveEvent(final FleXPlayer player, FleXPlayer recipient, String message) {
		
		super(player, true);
		
		this.recipient = recipient;
		
		this.message = message;
		
	}
	
	public FleXPlayer getRecipient() {
		return this.recipient;
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
