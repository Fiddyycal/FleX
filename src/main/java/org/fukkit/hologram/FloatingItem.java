package org.fukkit.hologram;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import io.flex.commons.cache.Cacheable;

public interface FloatingItem extends Cacheable {

	public UUID getUniqueId();
	
	public Location getLocation();
	
	public Set<Entity> getEntities();
	
	public void teleport(Location location);
	
	public void destroy();
	
}
