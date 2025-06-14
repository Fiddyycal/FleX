package org.fukkit;

import java.util.Arrays;

public enum WorldSetting {
	
	SPAWN_RADIUS("Spawn.Radius", 1),
	SPAWN_PROTECTION("Spawn.Protection", 10),
	
	SPAWN_CREATURE_ANIMALS("Spawn.Creatures.Animals", false),
	SPAWN_CREATURE_MONSTERS("Spawn.Creatures.Monsters", false),
	SPAWN_CREATURE_TESTIFICATES("Spawn.Creatures.Testificates", false),
	
	AUTO_SAVE("Settings.Auto-save", -1),
	
	VOID_TELEPORT("Settings.Void-Teleport", true),
	
	CYCLE_WEATHER("Settings.Cycle.Weather", false),
	CYCLE_DAYLIGHT("Settings.Cycle.Daylight", false),

	DAMAGE_PVP("Settings.Damage.PvP", false),
	DAMAGE_PVE("Settings.Damage.PvE", false),
	
	BLOCK_PLACE("Settings.Blocks.Place", false),
	BLOCK_BREAK("Settings.Blocks.Break", false),
	BLOCK_WHITELIST("Settings.Blocks.Whitelist", Arrays.asList("*")),
	BLOCK_BLACKLIST("Settings.Blocks.Blacklist", Arrays.asList("BEDROCK"));
	
	private String key;
	private Object def;
	
	private WorldSetting(String key, Object def) {
		this.key = key;
		this.def = def;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public Object getDefault() {
		return this.def;
	}
	
}
