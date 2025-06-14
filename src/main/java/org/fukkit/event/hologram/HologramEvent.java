package org.fukkit.event.hologram;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.fukkit.hologram.Hologram;

public class HologramEvent extends Event implements Cancellable {
	
	private Hologram hologram;
	
	private boolean cancel = false;
	
	private static HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {       
		return handlers;   
	}
	
	public HologramEvent(Hologram hologram) {
		this.hologram = hologram;
	}
	
	public Hologram getHologram() {
		return this.hologram;
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
