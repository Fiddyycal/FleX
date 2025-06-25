package org.fukkit.world;

import org.bukkit.World;
import org.bukkit.WorldCreator;

public interface WorldFactory {

	/**
	 * @param world A world that already exists.
	 * @return FleXWorld object for existing world.
	 */
	public FleXWorld createWorld(World world);
	
	/**
	 * @param creator The world creator for a new world.
	 * @return FleXWorld new object or existing world.
	 */
	public FleXWorld createWorld(WorldCreator creator);
	
}
