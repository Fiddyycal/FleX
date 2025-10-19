package org.fukkit.event.flow;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.fukkit.consequence.Report;
import org.fukkit.entity.FleXPlayer;

public class AsyncFleXPlayerOverwatchReplayPreDownloadEvent extends Event implements Cancellable {

	private static HandlerList handlers = new HandlerList();
	
	private FleXPlayer player;
	
	private Report report;
	
	private boolean cancel = false;
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {       
		return handlers;
	}
	
	public AsyncFleXPlayerOverwatchReplayPreDownloadEvent(FleXPlayer player, Report report) {
		super(true);
		this.player = player;
		this.report = report;
	}
	
	public Report getReport() {
		return this.report;
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
