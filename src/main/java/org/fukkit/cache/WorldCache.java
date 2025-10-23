package org.fukkit.cache;

import static java.io.File.separator;
import static org.fukkit.api.helper.ConfigHelper.defaults;
import static org.fukkit.api.helper.ConfigHelper.exe_path_absolute;
import static org.fukkit.api.helper.ConfigHelper.plugin_path_absolute;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.fukkit.Fukkit;
import org.fukkit.api.helper.ConfigHelper;
import org.fukkit.config.YamlConfig;
import org.fukkit.utils.BukkitUtils;
import org.fukkit.world.FleXWorld;
import org.fukkit.world.FleXWorldCreator;

import io.flex.FleX.Task;
import io.flex.commons.cache.LinkedCache;
import io.flex.commons.utils.ArrayUtils;
import io.flex.commons.utils.FileUtils;
import io.flex.commons.utils.NumUtils;

public class WorldCache extends LinkedCache<FleXWorld, UUID> {

	private static final long serialVersionUID = 5475465443410972475L;
	
	public WorldCache() {
		super((world, uuid) -> world.getUniqueId().equals(uuid));
	}
	
	public FleXWorld getByName(String name) {
		return this.stream().filter(w -> w.getName().equals(name) || w.getWorldName().equals(name)).findFirst().orElse(null);
	}
	
	public FleXWorld getByWorld(World world) {
		return this.stream().filter(w -> w.getUniqueId().equals(world.getUID())).findFirst().orElse(null);
	}
	
	@Override
	public boolean load() {
		
		/**
		 * The server needs to see the worlds first before modifying anything?
		 */
		BukkitUtils.runLater(() -> {
			
			// TODO Add to this depending on if world backups/resets are on, etc...
			long guestimatedLoadTime = NumUtils.getRng().getInt(100, 500);
			long worldTime = NumUtils.getRng().getInt(1, 3) * guestimatedLoadTime;
			
			File worlds = new File(plugin_path_absolute + separator + "network", "worlds");
			
			int size = worlds.exists() ? worlds.list().length : 0;
			
			long average = size != 0 ? worldTime * size : guestimatedLoadTime;
			
			Task.print("Network",
					
					"Loading worlds...",
					"Average load time: " + NumUtils.asString(average).toLowerCase() + ". (" + average + "ms)");
			
			this.unloadWorlds();
			this.loadWorlds();

			Task.print("Network", "Done!");
			
		}, 20L);
		
		return true;
		
	}
	
	private static final String ignore = ".ignore";
	
	private void unloadWorlds() {
		
		YamlConfig bukkitYml = new YamlConfig(null, null, "bukkit");
		
		bukkitYml.getConfig().set("ticks-per.autosave", -1);
		bukkitYml.save();
		
		String defaultWorld = null;
		
		try (InputStream input = new FileInputStream(exe_path_absolute + separator + "server.properties")) {
			 
			 Properties properties = new Properties();
			 
	         properties.load(input);
	         
	         defaultWorld = properties.getProperty("level-name", "world");
	         
	         Task.print("Network", "Please ignore the level-name " + ignore + ", do not delete this world. It has some funcion but for the most part is to sit and look pretty.");
	         
	         properties.setProperty("level-name", ignore);
	         
	         properties.store(new FileOutputStream("server.properties"), null);
	         
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        if (!defaultWorld.equals(ignore))
        	throw new RuntimeException("FleX initialization complete, please restart the server.");
		
		File[] worlds = Fukkit.getInstance().getServer().getWorldContainer().listFiles();
		
		File backend = new File(Fukkit.getInstance().getDataFolder().getAbsolutePath() + File.separator + "network", "worlds");
		File backups = new File(ConfigHelper.world_backups_path);
		
		if (!backups.exists())
			backups.mkdirs();
		
		if (backups.listFiles().length > 10) {
			
			File unlucky = backups.listFiles()[0];
			
			Task.debug("Backup", "Backups storage space low.");
			Task.debug("Backup", "Clearing world \"" + unlucky.getName() + "\" from backups.");
			
			FileUtils.delete(unlucky);
			
		}
		
		for (File world : worlds) {
			
			String name = world.getName();
			
			if (name != null && name.contains(ignore))
				continue;
			
			if (!world.getAbsoluteFile().isDirectory() || !ArrayUtils.contains(world.list(), "level.dat"))
				continue;
			
			boolean exists = false;
			
			for (String check : backend.list())
				if (check.equals(name))
					exists = true;
			
			if (!exists) {
				
				Task.error("Backup", "The world " +  name + " does not exist in the flex worlds container.");
				Task.print("Backup", "Saving world " +  name + " and moving to " + backups.getAbsolutePath() + "...");
				
				if (Bukkit.getWorld(name) != null)
					Bukkit.unloadWorld(name, true);
				
				Task.print("Backup", "World saved and moved.");
				Task.print("Backup", "If this is an error please make sure the world is either loaded after flex or put into the flex worlds container so it can load into the engine logic properly.");
				Task.print("Backup", "Alternatively, if you need to load worlds outside of FleX, add \".ignore\" to the world folder name.");
				
				String timeStamp = NumUtils.asDateTime(System.currentTimeMillis());
				
				FileUtils.move(world, new File(backups, "[BEFORE_SERVER_START] " + timeStamp + " " + name), "session.lock", "uid.dat", "puid.dat");
				
				if (backups.listFiles().length > 5) {
					
					File unlucky = backups.listFiles()[0];
					
					Task.debug("Backup", "Backups storage space low.");
					Task.debug("Backup", "Clearing world \"" + unlucky.getName() + "\" from backups.");
					
					FileUtils.delete(unlucky);
					
				}
				
			}
			
		}
		
	}
	
	private void loadWorlds() {
		
		File folder = new File(plugin_path_absolute + separator + "network" + separator + "worlds");
		
		if (!folder.exists())
			folder.mkdirs();
		
		if (folder.list().length == 0) {
			
			File first = new File(folder, ignore);
			
			if (!first.exists())
				first.mkdirs();
			
			/**
			 * 
			 *  TODO
			 *  Make bare configs
			 *  
			 *  Put the commented ones into first.getAbsolutePath()
			 *  Put the bare ones into new worlds created
			 *  
			 */
			
			FileUtils.unzipResource(defaults + "network" + separator + "worlds" + separator + "void.zip", first.getAbsolutePath());
			
		}
		
		for (File file : folder.listFiles())
			this.add(Fukkit.createWorld(new FleXWorldCreator(file.getName())));
		
	}

}
