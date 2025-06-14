package net.md_5.fungee.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.fukkit.entity.FleXHumanEntity;

public class FleXFinalizeEvent extends Event {

	protected FleXHumanEntity human;
	
	private static HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {       
		return handlers;   
	}

	public FleXFinalizeEvent() {
		super();
	}

}
