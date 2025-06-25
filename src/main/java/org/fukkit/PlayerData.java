package org.fukkit;

import java.io.File;
import java.util.UUID;

import org.bukkit.Location;
import org.fukkit.utils.WorldUtils;
import org.fukkit.world.FleXWorld;
import org.fukkit.world.FleXWorldNotLoadedException;

import io.flex.commons.file.DataFile;

public class PlayerData extends DataFile<UUID> {

	private static final long serialVersionUID = 7162982648405692368L;
	
	private Location lastSeen;
	
	public PlayerData(FleXWorld world, UUID uuid) throws FleXWorldNotLoadedException {
		
		super(world.getWorld().getWorldFolder().getAbsolutePath() + File.separator + "playerdata", uuid.toString() + ".dat", uuid, false);
		
		this.lastSeen = this.getTag("last_seen") != null ? WorldUtils.locationFromString(this.getTag("last_seen")) : null;
		
	}
	
	public Location getLastSeen() {
		return this.lastSeen;
	}
	
	public void setLastSeen(Location location) {
		this.setTag("last_seen", (this.lastSeen = location).toString());
	}

}
