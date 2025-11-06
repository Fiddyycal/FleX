package org.fukkit.api.helper;

import static java.io.File.separator;

import org.bukkit.configuration.file.FileConfiguration;
import org.fukkit.Fukkit;
import org.fukkit.config.Configuration;
import org.fukkit.config.YamlConfig;

import io.flex.FleX;

public class ConfigHelper {

	/*
	 * 
	 * Server Files:
	 * .
	 * .\
	 * ./
	 * 
	 * Local Machine:
	 * 
	 * \
	 * /
	 * 
	 * 
	 */
	
	public static final String defaults = (FleX.COMPILED_USING_MAVEN ? "" : "resources" + separator) + "defaults" + separator;
	public static final String assets = (FleX.COMPILED_USING_MAVEN ? "" : "resources" + separator) + "assets" + separator;
	
	public static final String exe_path_absolute = FleX.EXE_PATH;
	
	public static final String world_container_path = Fukkit.getPlugin().getServer().getWorldContainer().getPath();
	public static final String world_container_path_absolute = Fukkit.getPlugin().getServer().getWorldContainer().getAbsolutePath();
	
	public static final String plugin_path = Fukkit.getPlugin().getDataFolder().getPath();
	public static final String plugin_path_absolute = Fukkit.getPlugin().getDataFolder().getAbsolutePath();
	
	public static final String flex_path = world_container_path_absolute + separator + "flex";
	public static final String world_backups_path = flex_path + separator + "backups";
	
	public static final String flow_path = flex_path + separator + "data" + separator + "flow";
	
	public static FileConfiguration getConfig(Configuration configType) {
		return ((YamlConfig)Fukkit.getResourceHandler().getYaml(configType)).asFileConfiguration();
	}

}
