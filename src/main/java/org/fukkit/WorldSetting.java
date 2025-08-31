package org.fukkit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.fukkit.world.FleXWorld;

public enum WorldSetting {
	
	SPAWN_RADIUS("Spawn.Radius", 1),
	SPAWN_PROTECTION("Spawn.Protection", 10),
	SPAWN_TELEPORT("Spawn.Join-Teleport", true),
	
	SPAWN_CREATURE_ANIMALS("Spawn.Creatures.Animals", false),
	SPAWN_CREATURE_MONSTERS("Spawn.Creatures.Monsters", false),
	SPAWN_CREATURE_TESTIFICATES("Spawn.Creatures.Testificates", false),
	
	AUTO_SAVE("Settings.Auto-save", -1),
	
	VOID_TELEPORT("Settings.Void-Teleport.Enabled", true),
	VOID_TELEPORT_SPAWN("Settings.Void-Teleport.Spawn", true),
	
	CYCLE_WEATHER("Settings.Cycle.Weather", false),
	CYCLE_DAYLIGHT("Settings.Cycle.Daylight", false),

	DAMAGE_PVP("Settings.Damage.PvP", false),
	DAMAGE_PVE("Settings.Damage.PvE", false),
	
	BLOCK_PLACE("Settings.Blocks.Place", false),
	BLOCK_BREAK("Settings.Blocks.Break", false),
	BLOCK_WHITELIST("Settings.Blocks.Whitelist", Arrays.asList("*")),
	BLOCK_BLACKLIST("Settings.Blocks.Blacklist", Arrays.asList("BEDROCK"));
	
	public static final HashMap<UUID, Location> LAST_KNOWN = new HashMap<UUID, Location>();
	
	static {
		
		new FukkitRunnable() {
			
			boolean cancel = true;
			
			@Override
			public void execute() {
				
				for (FleXWorld fw : Memory.WORLD_CACHE) {
					
					if ((boolean)fw.getSetting(WorldSetting.VOID_TELEPORT) && !(boolean)fw.getSetting(WorldSetting.VOID_TELEPORT_SPAWN)) {
						
						this.cancel = false;
						
						fw.getOnlinePlayers().forEach(p -> {
							
							if (p.isOnGround())
								LAST_KNOWN.put(p.getUniqueId(), p.getLocation());
							
						});
						
					}
					
		        }
				
				if (this.cancel) {
					this.cancel();
					return;
				}
				
				LAST_KNOWN.keySet().removeIf(u -> {
					
					Player player = Bukkit.getPlayer(u);
					
					return player == null || !player.isValid();
					
				});
				
			}
			
		}.runTaskTimerAsynchronously(120L, 120L);
		
	}
	
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
