package org.fukkit;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.fukkit.world.FleXWorld;

import io.flex.FleX.Task;
import io.flex.commons.Severity;
import io.flex.commons.console.Console;
import io.flex.commons.utils.FileUtils;

import static org.fukkit.api.helper.ConfigHelper.plugin_path_absolute;

import static java.io.File.separator;

public class WorldSaver extends FukkitRunnable {

	private long i = 0;
	
	private Map<FleXWorld, Long> worlds = new HashMap<FleXWorld, Long>();	
	
	public WorldSaver() {
		this.runTaskTimer(10L, /* TODO Make 10 Minutes after testing */ 1600L);
	}

	@Override
	public void execute() {
		
		this.i++;
		
		this.worlds.entrySet().forEach(entry -> {
			
			if (entry.getValue() != -1 && this.i % entry.getValue() == 0) {
				
				FleXWorld world = entry.getKey();
				
				String name = world.getName();
				
				Task.print("AutoSave", "Auto-saving level " + name + "...");
				
				try {
					
					World w = world.getWorld();
					
					String writeToPath = "network" + separator + "worlds" + separator + name;
					String containerPath = plugin_path_absolute + separator + writeToPath;
					
					FileUtils.copy(w.getWorldFolder(), new File(containerPath), "puid.dat", "uid.dat", "session.lock");
					
					Task.print("AutoSave", "Save complete.");
					
				} catch (Exception e) {
					
					Task.error("AutoSave", "There was a problem auto-saving level " + name + ".");
					
					Console.log(name, Severity.CRITICAL, e);
					
				}
				
			}
			
		});
		
	}
	
	public Map<FleXWorld, Long> getWorlds() {
		return this.worlds;
	}
	
}
