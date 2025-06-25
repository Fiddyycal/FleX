package org.fukkit.cache;

import static java.io.File.separator;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.fukkit.Fukkit;
import org.fukkit.api.helper.ConfigHelper;
import org.fukkit.config.Configuration;
import org.fukkit.config.YamlConfig;
import org.fukkit.theme.Theme;

import io.flex.FleX.Task;
import io.flex.commons.cache.LinkedCache;
import io.flex.commons.file.Language;
import io.flex.commons.utils.NumUtils;

public class ThemeCache extends LinkedCache<Theme, String> {
	
	private static final long serialVersionUID = 9160995157213751252L;
	
	private Map<Block, Boolean> blocks = new HashMap<Block, Boolean>();
	
	private Theme defaultTheme;
	
	public ThemeCache() {
		super((theme, name) -> theme.getName().equalsIgnoreCase(name));
	}
	
	public Map<Block, Boolean> getBlocks() {
		return this.blocks;
	}
			
	@Override
	public boolean load() {

		long guestimatedLoadTime = NumUtils.getRng().getInt(500, 1500);
		
		Task.print("Themes",
				
				"Loading themes...",
				"Average load time: " + NumUtils.asString(guestimatedLoadTime).toLowerCase() + ". (" + guestimatedLoadTime + "ms)");
		
		YamlConfig conf = Fukkit.getResourceHandler().getYaml(Configuration.THEMES);
		File themes = conf.getParentFile();
		
		FileConfiguration config = conf.getConfig();
		
		if (!config.contains("Default")) {
			config.set("Default", "");
			conf.save();
		}
		
		String[] list = themes.list();
		
		boolean fresh = list.length == 1;
		
		if (fresh) {
			this.preload("Default");
			this.preload("FleX");
		}
		
		for (String theme : list) {
			
			if (theme.endsWith(".yml"))
				continue;
			
			this.add(new Theme(theme));
			
		}
		
		Task.print("Themes", "Done!");
		return true;
		
	}
	
	public Theme getDefaultTheme() {
		
		if (this.defaultTheme != null)
			return this.defaultTheme;
		
		YamlConfig conf = Fukkit.getResourceHandler().getYaml(Configuration.THEMES);
		
		FileConfiguration config = conf.getConfig();
		
		String theme = config.getString("Default", "Default");
		
		if (this.defaultTheme == null)
			this.defaultTheme = this.get(theme);
		
		if (this.defaultTheme == null)
			throw new UnsupportedOperationException("Theme '" + theme + "' is set as default and does not exist, please make sure the spelling is correct in themes/config.yml.");
		
		return this.defaultTheme;
		
	}
	
	private void preload(String name) {
		
		String writeTo = "themes" + separator + name;
		String defaultFrom = ConfigHelper.defaults + writeTo + separator;
		
		new YamlConfig(Fukkit.getInstance(), writeTo, "theme", defaultFrom + "theme.yml");
		
		Arrays.stream(Language.values()).forEach(l -> {
			
			new YamlConfig(Fukkit.getInstance(), writeTo + separator + "lang", l + ".yml",
					ConfigHelper.assets + "themes" + separator + "lang" + separator + l + ".yml");
			
		});
		
	}
	
}
