package org.fukkit.config;

import static org.fukkit.api.helper.ConfigHelper.exe_path_absolute;

import java.io.Serializable;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import io.flex.commons.file.DataFile;

public class YamlConfig extends DataFile<Serializable> {
	
	private static final long serialVersionUID = 2414227865966089778L;

	private FileConfiguration config;
	
	public YamlConfig(Plugin plugin, String path, String name, String defaults) {
		
		this(plugin, path, name);
		
		if (this.isFresh()) {
			
			this.setContents(defaults);
		    this.config = YamlConfiguration.loadConfiguration(this);
		    this.config.options().copyDefaults(true);
		    this.save();
		    
		}
		
	}
	
	public YamlConfig(Plugin plugin, String path, String name) {
		
		super((path != null && path.contains(exe_path_absolute) ? "" : exe_path_absolute) + separator +
				
				(plugin != null ? "plugins" + separator + plugin.getName() + separator : "") +
				(path != null ? path : ""),
				
				name != null ? (name.endsWith(".yml") ? name : name + ".yml") : null);
		
		this.config = YamlConfiguration.loadConfiguration(this);
		this.save();
		
	}
	
	public YamlConfig(String path, String name) {
		this(null, path, name);
	}
	
	public FileConfiguration getConfig() {
		return this.config;
	}
	
	public void save() {
		try {
			this.config.save(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	@Deprecated
	public <V extends Serializable> V getTag(String tag) {
		throw new UnsupportedOperationException("Tags unavailable for Yaml files.");
	}
	
	@Override
	@Deprecated
	public <V extends Serializable> void setTag(String tag, V value) {
		throw new UnsupportedOperationException("Tags unavailable for Yaml files.");
	}
	
	@Override
	@Deprecated
	public Map<String, Serializable> asTags() {
		throw new UnsupportedOperationException("Tags unavailable for Yaml files.");
	}
	
}
