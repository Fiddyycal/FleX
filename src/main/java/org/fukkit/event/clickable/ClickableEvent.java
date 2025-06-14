package org.fukkit.event.clickable;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.fukkit.clickable.Clickable;

public class ClickableEvent extends Event {
	
	protected Clickable clickable;
	
	private static HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {       
		return handlers;
	}
	
	public ClickableEvent(Clickable clickable, boolean async) {
		
		super(async);
		
		this.clickable = clickable;
		
	}
	
	public Clickable getClickable() {
		return this.clickable;
	}

}
