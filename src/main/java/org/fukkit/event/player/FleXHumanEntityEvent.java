package org.fukkit.event.player;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.fukkit.entity.FleXHumanEntity;

public class FleXHumanEntityEvent extends Event {

	protected FleXHumanEntity human;
	
	private static HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {       
		return handlers;   
	}
	
	public FleXHumanEntityEvent(final FleXHumanEntity human, boolean async) {
		
		super(async);
		
		this.human = human;
		
	}
	
	public FleXHumanEntity getPlayer() {
		return this.human;
	}

}
