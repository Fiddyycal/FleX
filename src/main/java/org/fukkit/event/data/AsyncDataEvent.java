package org.fukkit.event.data;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.flex.commons.socket.Data;

public class AsyncDataEvent extends Event {

	private static HandlerList handlers = new HandlerList();
	
	private Data data;
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {       
		return handlers;
	}
	
	public AsyncDataEvent(Data data) {
		
		super(true);
		
		this.data = data;
		
	}
	
	public Data getData() {
		return this.data;
	}

}
