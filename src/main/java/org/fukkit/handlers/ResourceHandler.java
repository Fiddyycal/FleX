package org.fukkit.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.fukkit.Fukkit;
import org.fukkit.config.Configuration;
import org.fukkit.config.YamlConfig;

import io.flex.FleX;

import static java.io.File.separator;

import static org.fukkit.api.helper.ConfigHelper.*;

public class ResourceHandler {
	
	private static boolean registered = false;

	private FileConfiguration plugyml, spigyml, bukyml;
	
	private YamlConfig aflex, config, data, network, ranks, themes;
	
	public ResourceHandler() {

		if (!Fukkit.getPlugin().getDataFolder().exists())
			Fukkit.getPlugin().getDataFolder().mkdir();
		
		InputStream input = FleX.getResourceAsStream("plugin.yml");
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		
		this.plugyml = YamlConfiguration.loadConfiguration(reader);
		
		File spigot = new File(world_container_path_absolute, "spigot.yml");
		File bukkit = new File(world_container_path_absolute, "bukkit.yml");
		
		if (spigot.exists()) this.spigyml = YamlConfiguration.loadConfiguration(spigot);
		if (bukkit.exists()) this.bukyml = YamlConfiguration.loadConfiguration(bukkit);
		
		String path = this.plugyml.getString("flex.config.path", "plugins" + separator + "aFleX");
		String name = this.plugyml.getString("flex.config.name", "flex");
		
		this.aflex = new YamlConfig(path, name, defaults + "flex.yml");
		this.config = new YamlConfig(plugin_path_absolute, "config", defaults + "config.yml");
		
		File local = new File("flex" + separator + "data" + separator + "local");
		File sqlite = new File("flex" + separator + "data" + separator + "sqlite");
		
		if (!local.exists())
			local.mkdirs();
		
		if (!sqlite.exists())
			sqlite.mkdirs();
			
		File disguises = new File("flex" + separator + "data" + separator + "local" + separator + "disguises");
		
		if (!disguises.exists())
			disguises.mkdirs();
		
		this.data = new YamlConfig(plugin_path_absolute + separator + "data", "config", defaults + "data" + separator + "config.yml");
		this.network = new YamlConfig(plugin_path_absolute + separator + "network", "config", defaults + "network" + separator + "config.yml");
		
		this.ranks = new YamlConfig(plugin_path_absolute + separator + "ranks", "config", defaults + "ranks" + separator + "config.yml");
		this.themes = new YamlConfig(plugin_path_absolute + separator + "themes", "config", defaults + "themes" + separator + "config.yml");
		
		registered = true;
		
	}
	
	public FileConfiguration getSpigotYaml() {
		return this.spigyml;
	}
	
	public FileConfiguration getBukkitYaml() {
		return this.bukyml;
	}
	
	public FileConfiguration getPluginYaml() {
		return this.plugyml;
	}
	
	@SuppressWarnings("deprecation")
	public YamlConfig getYaml(Configuration configType) {
		
		switch (configType) {
		case ENGINE:
			return this.aflex;
		case FLEX:
			return this.config;
		case SQL:
			return this.data;
		case DATA:
			return this.data;
		case NETWORK:
			return this.network;
		case RANKS:
			return this.ranks;
		case THEMES:
			return this.themes;
		default:
			return null;
		}
		
	}
	
	public void getPlayerData() {
		
	}
	
	public static boolean isRegistered() {
		return registered;
	}
	
}
