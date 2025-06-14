package org.fukkit.hologram;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import io.flex.commons.cache.Cacheable;

public interface Hologram extends Cacheable {

	public UUID getUniqueId();
	
	public Location getLocation();
	
	public List<LivingEntity> getEntities();
	
	public List<String> getLinesUnsafe();
	
	public void teleport(Location location);
	
	public void destroy();
	
}
