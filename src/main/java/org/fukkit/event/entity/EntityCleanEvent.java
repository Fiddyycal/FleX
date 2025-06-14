package org.fukkit.event.entity;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.util.Vector;

import io.flex.commons.Nullable;

public class EntityCleanEvent extends EntityEvent implements Cancellable {
	
	private CleanType type;
	
	private boolean cancel = false;
	
	private static HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {       
		return handlers;   
	}
	
	public EntityCleanEvent(Entity entity, @Nullable CleanType type) {
		
		super(entity);
		
		this.type = type;
		
		if (entity == null)
			return;
		
		if (type == null || type == CleanType.NONE)
			return;
		
		if (type == CleanType.SQUEAKY) {

			entity.getWorld().setWeatherDuration(0);
			entity.getWorld().setStorm(false);
			
			entity.setVelocity(new Vector());
			
		}
		
		entity.setFallDistance(0);
		entity.setFireTicks(0);
		
	}
	
	public CleanType getType() {
		return this.type;
	}

	@Override
	public boolean isCancelled() {
		return this.cancel;
	}

	@Override
	public void setCancelled(boolean arg0) {
		this.cancel = arg0;
	}
	
	public enum CleanType {
		NONE, RINSE, FRESH, SQUEAKY;
	}
	
}
