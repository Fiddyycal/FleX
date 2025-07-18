package org.fukkit.event.player;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.fukkit.PlayerState;
import org.fukkit.entity.FleXPlayer;

public class PlayerChangeStateEvent extends Event {
	
	private FleXPlayer player;
	
	private PlayerState state;
	
	private static HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {       
		return handlers;
	}
	
	public PlayerChangeStateEvent(FleXPlayer player, PlayerState state, boolean async) {
		
		super(async);
		
		this.player = player;
		
		this.state = state;
		
	}
	
	public FleXPlayer getPlayer() {
		return this.player;
	}
	
	public PlayerState getTo() {
		return this.state;
	}

}
