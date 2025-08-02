package org.fukkit;

import java.util.HashMap;
import java.util.Map;

import org.fukkit.world.FleXWorld;

import io.flex.FleX.Task;
import io.flex.commons.Severity;
import io.flex.commons.console.Console;

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
					/*  TODO RE-DO ALL OF THIS.
					String writeToPath = "network";
					String containerPath = plugin_path_absolute + separator + writeToPath + separator + "worlds" + separator + name;
					
					FileUtils.copy(new File(world_container_path_absolute + separator + world.getName()), new File(containerPath), "puid.dat", "uid.dat", "session.lock");
					
					*/
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
