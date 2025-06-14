package org.fukkit.utils;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.fukkit.Fukkit;

import io.flex.commons.utils.FileUtils;

public class WorldUtils {
	
	public static final String[] INFORMATIONAL_DIRECTORIES = { "functions", "session.lock", "uid.dat", "scoreboard.dat", "##MCEDIT.TEMP##" };
	
	public static World copyWorld(String sourcePath, String destinationPath) {
		
		destinationPath = destinationPath.replace("/",
				File.separator);
		
		FileUtils.copy(new File(sourcePath), new File(destinationPath), INFORMATIONAL_DIRECTORIES);
		
		return Fukkit.getInstance().getServer().createWorld(
				new WorldCreator(destinationPath));
		
	}
	
	public static boolean unloadWorld(World world) {
		
		if (world == null)
			return true;
		
		boolean unload = Bukkit.getServer().unloadWorld(world, true);
		return !unload ? Bukkit.getServer().unloadWorld(world, false) : unload;
		
	}
	
	public static Location locationFromString(String s) {
		
		try {
			
			String x = s.split(",x=")[1].split(",")[0];
			String y = s.split(",y=")[1].split(",")[0];
			String z = s.split(",z=")[1].split(",")[0];
			String pitch = s.split(",pitch=")[1].split(",")[0];
			String yaw = s.split(",yaw=")[1].split("\\}")[0];
			
			String input = s.split("Location\\{world=(.*?)")[1].split(",x=" + x)[0];
			String world = !input.equals("null") ? input.split("name=")[1].substring(0, input.split("name=")[1].length()-1) : null;
			
			//Task.debug("Location{world=CraftWorld{name=" + world + "},x=" + x + ",y=" + y + ",z=" + z + ",pitch=" + pitch + ",yaw=" + yaw + "}");
			
			return new Location(world != null ? Bukkit.getWorld(world) : null, Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z), Float.parseFloat(yaw), Float.parseFloat(pitch));
			
		} catch (NullPointerException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
			throw new UnsupportedOperationException(s + " is not a valid Location string.");
		}
		
	}
	
}
