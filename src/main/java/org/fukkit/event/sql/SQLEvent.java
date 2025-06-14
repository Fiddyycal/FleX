package org.fukkit.event.sql;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.flex.commons.sql.SQLDatabase;

public class SQLEvent extends Event {
	
	protected SQLDatabase connection;
	
	private static HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {       
		return handlers;
	}
	
	public SQLEvent(SQLDatabase connection) {
		
		super(false);
		
		this.connection = connection;
		
	}
	
	public SQLDatabase getConnection() {
		return this.connection;
	}

}
