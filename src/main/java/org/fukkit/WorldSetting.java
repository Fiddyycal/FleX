package org.fukkit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.fukkit.world.FleXWorld;

public enum WorldSetting {
	
	SPAWN_RADIUS("spawn.radius", 1),
	SPAWN_PROTECTION("spawn.protection", 10),
	
	SPAWN_CREATURE_ANIMALS("spawn.creatures.animals", false),
	SPAWN_CREATURE_MONSTERS("spawn.creatures.monsters", false),
	SPAWN_CREATURE_TESTIFICATES("spawn.creatures.testificates", false),
	
	AUTO_SAVE("settings.auto-save", -1),
	
	VOID_TELEPORT("settings.void-teleport.enabled", true),
	VOID_TELEPORT_SPAWN("settings.void-teleport.spawn", true),
	
	CYCLE_WEATHER("settings.cycle.Weather", false),
	CYCLE_DAYLIGHT("settings.cycle.Daylight", false),

	DAMAGE_PVP("settings.damage.pvp", false),
	DAMAGE_PVE("settings.damage.pve", false),
	
	BLOCK_PLACE("settings.blocks.place", false),
	BLOCK_BREAK("settings.blocks.break", false),
	BLOCK_WHITELIST("settings.blocks.whitelist", Arrays.asList("*")),
	BLOCK_BLACKLIST("settings.blocks.blacklist", Arrays.asList("BEDROCK"));
	
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
