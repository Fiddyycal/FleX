package org.fukkit.event.entity;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.fukkit.entity.FleXEntity;

public class FleXEntityEvent extends Event {

	protected FleXEntity entity;
	
	private static HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {       
		return handlers;
	}
	
	public FleXEntityEvent(final FleXEntity entity, boolean async) {
		super(async);
		this.entity = entity;
	}
	
	public FleXEntity getEntity() {
		return this.entity;
	}

}
