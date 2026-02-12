package org.fukkit.event.player;

import org.bukkit.event.Cancellable;
import org.fukkit.entity.FleXPlayer;

public class FleXPlayerDebugEvent extends FleXPlayerEvent implements Cancellable {

	private String reference;
	
	private boolean cancel = false;
	
	public FleXPlayerDebugEvent(FleXPlayer player, String reference) {
		
		super(player, false);
		
		this.reference = reference;
		
	}

	public String getReference() {
		return this.reference;
	}
	
	@Override
	public boolean isCancelled() {
		return this.cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

}
