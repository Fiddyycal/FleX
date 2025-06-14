package org.fukkit.event.consequence;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.fukkit.consequence.PreConsequence;

public class FleXPreConsequenceEvent extends Event implements Cancellable {

	private static HandlerList handlers = new HandlerList();
	
	private PreConsequence consequence;
	
	private boolean cancel = false;
	
	public FleXPreConsequenceEvent(PreConsequence consequence, boolean async) {
		
		super(async);
		
		this.consequence = consequence;
		
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public PreConsequence getPreConsequence() {
		return this.consequence;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
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
