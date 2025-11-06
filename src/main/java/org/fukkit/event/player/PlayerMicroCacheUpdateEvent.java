package org.fukkit.event.player;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.fukkit.cache.PlayerCache.PlayerCacheMeta;
import org.fukkit.cache.PlayerCache.PlayerCacheMeta.PlayerCacheMetaType;

public class PlayerMicroCacheUpdateEvent extends Event {

	private static HandlerList handlers = new HandlerList();
	
	private PlayerCacheMeta meta;
	
	private PlayerCacheMetaType type;
	
	private Object obj;
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {       
		return handlers;
	}
	
	public PlayerMicroCacheUpdateEvent(PlayerCacheMeta meta, PlayerCacheMetaType type, Object obj) {
		
		super(false);
		
		this.meta = meta;
		
		this.type = type;
		
		this.obj = obj;
		
	}
	
	public PlayerCacheMeta getMeta() {
		return this.meta;
	}
	
	public PlayerCacheMetaType getType() {
		return this.type;
	}
	
	public Object getValue() {
		return this.obj;
	}

}
