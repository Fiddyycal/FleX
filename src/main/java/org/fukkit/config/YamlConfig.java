package org.fukkit.config;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import io.flex.commons.Nullable;
import io.flex.commons.file.DataFile;

public class YamlConfig extends DataFile<Serializable> {
	
	private static final long serialVersionUID = 2414227865966089778L;

	// delegation
	private FileConfiguration config;
	
	public YamlConfig(String path) {
		this(path, null);
	}
	
	public YamlConfig(String path, @Nullable String name, String defaults) {
		
		this(path, name);
		
		if (this.isFresh()) {
			
			this.setContents(defaults);
		    this.config = YamlConfiguration.loadConfiguration(this);
		    this.config.options().copyDefaults(true);
		    this.save();
		    
		}
		
	}
	
	public YamlConfig(String path, @Nullable String name) {
		
		super(name != null ? path : (path.endsWith(".yml") ? path : path + ".yml"), name.endsWith(".yml") ? name : name + ".yml");
		
		this.config = YamlConfiguration.loadConfiguration(this);
		this.save();
		
	}
	
	public int getInt(String key) {
		return this.getInt(key, -1);
	}
	
	public int getInt(String key, int def) {
		return this.config.getInt(key, def);
	}
	
	public double getDouble(String key) {
		return this.getDouble(key, -1D);
	}
	
	public double getDouble(String key, double def) {
		return this.config.getDouble(key, def);
	}
	
	public long getLong(String key) {
		return this.getLong(key, -1L);
	}
	
	public long getLong(String key, long def) {
		return this.config.getLong(key, def);
	}
	
	public boolean getBoolean(String key) {
		return this.getBoolean(key, false);
	}
	
	public boolean getBoolean(String key, boolean def) {
		return this.config.getBoolean(key, def);
	}
	
	public String getString(String key) {
		return this.getString(key, null);
	}
	
	public String getString(String key, @Nullable String def) {
		return this.config.getString(key, def);
	}
	
	public List<String> getStringList(String path) {
        return this.config.getStringList(path);
    }
	
	public boolean contains(String path) {
		return this.config.contains(path);
	}
	
	public void set(String key, @Nullable Object value) {
		this.asFileConfiguration().set(key, value);
	}
	
	public FileConfiguration asFileConfiguration() {
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
