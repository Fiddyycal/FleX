package org.fukkit.event.command;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.fukkit.command.FleXCommandAdapter;

public class FleXCommandEvent extends Event {

	protected FleXCommandAdapter command;
	private static HandlerList handlers = new HandlerList();
	
	public FleXCommandEvent(FleXCommandAdapter command, boolean async) {
		super(async);
		this.command = command;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public FleXCommandAdapter getCommand() {
		return this.command;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
