package org.fukkit.event.consequence;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.fukkit.consequence.Consequence;

public class FleXPreConsequenceEvent extends Event implements Cancellable {

	private static HandlerList handlers = new HandlerList();
	
	private Consequence consequence;
	
	private boolean cancel = false;
	
	public FleXPreConsequenceEvent(Consequence consequence, boolean async) {
		
		super(async);
		
		this.consequence = consequence;
		
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public Consequence getPreConsequence() {
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
