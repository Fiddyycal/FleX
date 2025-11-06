package org.fukkit.world;

import java.io.File;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.fukkit.PlayerData;
import org.fukkit.WorldSetting;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.entity.FleXPlayer;

import io.flex.commons.cache.Cacheable;

public interface FleXWorld extends Cacheable {
	
	public UUID getUniqueId();
	
	public String getName();
	
	public String getWorldName();
	
	public String getPassword();
	
	public World getWorld();
	
	public WorldState getState();
	
	public File getWorldFolder();

	public Location getSpawnLocation();
	
	public Location getBackupSpawnLocation();
	
	public <T> T getSetting(WorldSetting setting);
	
	public PlayerData getPlayerData(FleXPlayer player);
	
	public Set<FleXHumanEntity> getOnlinePlayers();
	
	public void setSpawnLocation(Location location);
	
	public void setSpawnLocation(int x, int y, int z);
	
	public void setSpawnLocation(double x, double y, double z, float yaw, float pitch);
	
	public void setBackupSpawnLocation(double x, double y, double z, float yaw, float pitch);
	
	public <T> void setSetting(WorldSetting setting, T persistant);
	
	public void setPassword(String password);
	
	public void setState(WorldState state);
	
	public default boolean hasPassword() {
		return this.getPassword() != null;
	}
	
	public boolean hasWhitelist();
	
	public boolean isAutoSaving();
	
	public boolean isWhitelisted(FleXPlayer player);
	
	public default boolean isJoinable() {
		return this.getState().isJoinable();
	}
	
	public FleXWorld clone(FleXWorldCreator destination);
	
	public void unload();
	
	public void backup();
	
	public void delete();
	
}
