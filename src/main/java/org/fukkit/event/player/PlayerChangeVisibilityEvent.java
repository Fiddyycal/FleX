package org.fukkit.event.player;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.fukkit.disguise.Visibility;
import org.fukkit.entity.FleXPlayer;

public class PlayerChangeVisibilityEvent extends Event implements Cancellable {
	
	private FleXPlayer player;
	
	private Visibility visibility;
	
	private boolean cancel = false;
	
	private static HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {       
		return handlers;
	}
	
	public PlayerChangeVisibilityEvent(FleXPlayer player, Visibility visibility, boolean async) {
		
		super(async);
		
		this.player = player;
		
		this.visibility = visibility;
		
	}
	
	public FleXPlayer getPlayer() {
		return this.player;
	}
	
	public Visibility getTo() {
		return this.visibility;
	}

	@Override
	public boolean isCancelled() {
		return this.cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

}
