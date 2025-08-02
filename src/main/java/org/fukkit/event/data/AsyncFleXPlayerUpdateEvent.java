package org.fukkit.event.data;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.fukkit.entity.FleXPlayer;

import net.md_5.fungee.ProtocolVersion;

/**
 * This event is called when a players information is updated by the proxy.
 */
public class AsyncFleXPlayerUpdateEvent extends Event {

	private static HandlerList handlers = new HandlerList();
	
	private FleXPlayer player;
	
	private String domain;
	
	private ProtocolVersion version;
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {       
		return handlers;
	}
	
	public AsyncFleXPlayerUpdateEvent(FleXPlayer player, String domain, ProtocolVersion version) {
		super(true);
	}
	
	public FleXPlayer getPlayer() {
		return this.player;
	}
	
	public String getDomain() {
		return this.domain;
	}
	
	public ProtocolVersion getVersion() {
		return this.version;
	}

}
