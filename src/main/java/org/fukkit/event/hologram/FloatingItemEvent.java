package org.fukkit.event.hologram;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.fukkit.hologram.FloatingItem;

public class FloatingItemEvent extends Event implements Cancellable {
	
	private FloatingItem item;
	
	private boolean cancel = false;
	
	private static HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {       
		return handlers;   
	}
	
	public FloatingItemEvent(FloatingItem item) {
		this.item = item;
	}
	
	public FloatingItem getFloatingItem() {
		return this.item;
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
