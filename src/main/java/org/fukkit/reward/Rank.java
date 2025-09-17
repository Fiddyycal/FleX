package org.fukkit.reward;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
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
		
		FileConfiguration conf = yml.getConfig();
		
		this.name = name;
		
		this.abbreviation = conf.getString("Ranks."  + name + ".Abbreviation", name.length() >= 3 ? name.substring(0, 3) : name);
		
		this.weight = conf.getLong("Ranks." + name + ".Weight", 1);
	    
		conf.getStringList("Ranks." + this.name + ".Inherit").stream().forEach(i -> {
	    	
			Rank inherit = Memory.RANK_CACHE.get(i);
			
	    	if (inherit != null)
		    	this.inherited.add(inherit);
	    	
	    	else Task.error("Rank", "Unable to inherit the permissions from " + i + " for " + this.name + ": no further information.");
	    	
	    });
		
		this.permissions.addAll(conf.getStringList("Ranks." + this.name + ".Permissions"));
		
		this.staff = this.weight >= conf.getLong("Staff.Weight", 10);
		
		BukkitUtils.runLater(() -> {
			this.loadDisplays();
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
		
		if (present && !entry.getValue().contains(this.name))
			display = display + this.name;
		
		if (present)
			return theme.format(present && display.contains(" ") ? display.substring(0, display.lastIndexOf(' ')) : display);
		
		return display;
		
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
	
	public Set<String> asNodes() {
		Set<String> permissions = new HashSet<String>(this.permissions);
		this.inherited.stream().forEach(i -> permissions.addAll(i.asNodes()));
		return permissions;
	}
	
	private void loadDisplays() {
		
		Task.debug("Theme", "Loading displays for rank " + this.getName() + ".");
		
		if (!this.displays.isEmpty())
			this.displays.clear();
		
		YamlConfig rankYml = Fukkit.getResourceHandler().getYaml(Configuration.RANKS);
		
		FileConfiguration rankConf = rankYml.getConfig();
		
		for (Theme theme : Memory.THEME_CACHE) {
			
			String writeToPath = "themes" + File.separator + theme.getName();
			String defaultFromPath = ConfigHelper.assets + "themes" + File.separator;
			String def = "<pp>[" + (this.name.equalsIgnoreCase("probation") ? "&fMember<sp>(<failure>P<sp>)" : this.name.equalsIgnoreCase("owner") ? "&4%rank%" : "&f%rank%") + "<pp>]<reset> <reset>";
			
			boolean perTheme = rankConf.getBoolean("Theme-Specific", true);
			
			YamlConfig themeYml = perTheme ? new YamlConfig(Fukkit.getInstance(), writeToPath, "ranks", defaultFromPath + "ranks.yml") : null;
			
			if (perTheme) {
				
				if (themeYml.getConfig().getString("Ranks." + this.name) == null) {
					themeYml.getConfig().set("Ranks." + this.name, def);
					themeYml.save();
				}
				
			}
			
			String rank = perTheme ? themeYml.getConfig().getString("Ranks." + this.name, def) : rankConf.getString("Ranks." + this.name + ".Display", def);
			
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
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public String of(Theme theme, String s) {
		return theme.format(this.getDisplay(theme, false) + s);
	}
	
}
