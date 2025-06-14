package org.fukkit.event.command;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.fukkit.command.FleXCommand;

public class FleXCommandEvent extends Event {

	protected FleXCommand command;
	private static HandlerList handlers = new HandlerList();
	
	public FleXCommandEvent(FleXCommand command, boolean async) {
		super(async);
		this.command = command;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public FleXCommand getCommand() {
		return this.command;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
