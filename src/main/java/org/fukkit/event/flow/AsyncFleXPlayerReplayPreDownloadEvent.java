package org.fukkit.event.flow;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.fukkit.entity.FleXPlayer;

public class AsyncFleXPlayerReplayPreDownloadEvent extends Event implements Cancellable {

	private static HandlerList handlers = new HandlerList();
	
	private String name;
	
	private FleXPlayer player;
	
	private boolean cancel = false;
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {       
		return handlers;
	}
	
	public AsyncFleXPlayerReplayPreDownloadEvent(FleXPlayer player, String name) {
		
		super(true);
		
		this.name = name;
		
		this.player = player;
		
	}
	
	public String getName() {
		return this.name;
	}
	
	public FleXPlayer getPlayer() {
		return this.player;
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
