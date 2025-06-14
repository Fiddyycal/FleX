package org.fukkit.entity;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.fukkit.world.FleXWorld;

import io.flex.commons.cache.Cacheable;

public interface FleXEntity extends Cacheable {

	public String getName();
	
	public UUID getUniqueId();
	
	public List<MetadataValue> getMetadata(String metadata);
	
    public List<Block> getNearbyBlocks(int radius);
	
    public Location getLocation();
    
    public Server getServer();
    
	public FleXWorld getWorld();
    
    public Entity getEntity();
    
	public void setMetadata(String metadataKey, MetadataValue newMetadataValue);
    
	public void removeMetadata(String metadataKey, Plugin plugin);
	
	public boolean teleport(Location location);
	
	public boolean teleport(Entity entity);
	
	public boolean teleport(FleXEntity entity);
	
	public boolean teleport(Location location, TeleportCause cause);
	
	public boolean teleport(Entity entity, TeleportCause cause);
	
	public boolean teleport(FleXEntity entity, TeleportCause cause);
	
	public boolean hasMetadata(String metadataKey);
	
	public boolean isUploaded();
	
	public boolean isLoaded();
	
	public void onSpawn(Entity entity);
	
	public void onDespawn();
	
}
