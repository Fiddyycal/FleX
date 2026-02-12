package org.fukkit.reward;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.api.helper.ConfigHelper;
import org.fukkit.config.Configuration;
import org.fukkit.config.YamlConfig;
import org.fukkit.event.FleXEventListener;
import org.fukkit.theme.Theme;
import org.fukkit.utils.BukkitUtils;

import io.flex.FleX.Task;
import io.flex.commons.cache.Cacheable;
import io.flex.commons.cache.cell.BiCell;

import static java.util.Objects.requireNonNull;

import java.io.File;

public class Rank extends FleXEventListener implements Cacheable {
	
	private long weight;
	
	private String name, abbreviation;
	
	private Map<BiCell<String, String>, String> displays = new HashMap<BiCell<String, String>, String>();
	
	private Set<String> permissions = new HashSet<String>();
	
	private Set<Rank> inherited = new HashSet<Rank>();
	
	private boolean staff;
	
	public Rank(String name) {
		
		requireNonNull(name, "name must not be null");
		
		YamlConfig yml = Fukkit.getResourceHandler().getYaml(Configuration.RANKS);
		
		this.name = name;
		
		this.abbreviation = yml.getString("ranks."  + name + ".abbreviation", name.length() >= 3 ? name.substring(0, 3) : name);
		
		this.weight = yml.getLong("ranks." + name + ".weight", 1);
		
		this.permissions.addAll(yml.getStringList("ranks." + this.name + ".permissions"));
		
		this.staff = this.weight >= yml.getLong("staff-weight", 10);
		
		BukkitUtils.runLater(() -> {
			this.loadDisplays();
			this.inheritPermissions();
		});
		
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getAbbreviation() {
		return this.abbreviation;
	}
	
	public String getDisplay(Theme theme, boolean present) {
		
	    if (theme == null)
	    	throw new NullPointerException("theme must not be null");
	    
	    Entry<BiCell<String, String>, String> entry = this.displays.entrySet().stream().filter(e -> e.getKey().a().equals(theme.getName()) && e.getKey().b().equals(this.name)).findFirst().orElse(null);
	    
	    if (entry == null)
	    	return null;
	    
	    String display = entry.getValue();
	    
	    if (present && !display.contains(this.name))
	        display += this.name;
	    
	    return present ? theme.format(display) : display;
	    
	}
	
	public Set<String> getPermissions() {
		return this.permissions;
	}
	
	public Set<Rank> getInheritedRanks() {
		return this.inherited;
	}
	
	public long getWeight() {
		return this.weight;
	}
	
	public boolean isStaff() {
		return this.staff;
	}
	
	private Set<String> cached;
	private int size = -1;

	public Set<String> asNodes() {
		
	    int size = this.permissions.size();
	    
	    if (this.cached == null || this.size != size) {
	    	
	        Set<String> result = new HashSet<String>(this.permissions);
	        
	        for (Rank i : this.inherited)
	            result.addAll(i.asNodes());
	        
	        this.cached = Collections.unmodifiableSet(result);
	        this.size = size;
	        
	    }
	    
	    return this.cached;
	    
	}
	
	private void loadDisplays() {
		
		Task.debug("Theme", "Loading displays for rank " + this.getName() + ".");
		
		if (!this.displays.isEmpty())
			this.displays.clear();
		
		YamlConfig rankYml = Fukkit.getResourceHandler().getYaml(Configuration.RANKS);
		
		for (Theme theme : Memory.THEME_CACHE) {
			
			String writeToPath = "themes" + File.separator + theme.getName();
			String defaultFromPath = ConfigHelper.assets + "themes" + File.separator;
			String def = "&f<reset> <reset>";
			
			boolean perTheme = rankYml.getBoolean("theme-specific-displays", true);
			
			YamlConfig themeYml = perTheme ? new YamlConfig(ConfigHelper.plugin_path_absolute + File.separator + writeToPath, "ranks", defaultFromPath + "ranks.yml") : null;
			
			if (perTheme) {
				
				if (!themeYml.contains(this.name)) {
					themeYml.set(this.name, def);
					themeYml.save();
				}
				
			}
			
			String rank = perTheme ? themeYml.getString(this.name, def) : rankYml.getString("ranks." + this.name + ".display", def);
			
			this.displays.put(new BiCell<String, String>() {

				private static final long serialVersionUID = 2818779362181280875L;

				@Override
				public String a() {
					return theme.getName();
				}

				@Override
				public String b() {
					return Rank.this.name;
				}
				
			}, rank.replace("%rank%", this.name));
			
		}
		
	}
	
	private void inheritPermissions() {

		YamlConfig yml = Fukkit.getResourceHandler().getYaml(Configuration.RANKS);
		
		yml.getStringList("ranks." + this.name + ".inherit").stream().forEach(i -> {
	    	
			Rank inherit = Memory.RANK_CACHE.get(i);
			
	    	if (inherit != null)
		    	this.inherited.add(inherit);
	    	
	    	else Task.error("Rank", "Unable to inherit the permissions from " + i + " for " + this.name + ": no further information.");
	    	
	    });
		
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public String of(Theme theme, String s) {
		return theme.format(this.getDisplay(theme, false) + s);
	}
	
}
