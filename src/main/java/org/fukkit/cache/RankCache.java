package org.fukkit.cache;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.api.helper.ConfigHelper;
import org.fukkit.config.Configuration;
import org.fukkit.config.YamlConfig;
import org.fukkit.reward.Rank;

import io.flex.commons.cache.LinkedCache;

public class RankCache extends LinkedCache<Rank, String> {

	private static final long serialVersionUID = -7517426384280062919L;
	
	private Rank def;
	
	public RankCache() {
		super((rank, name) -> rank.getName().equalsIgnoreCase(name));
	}
	
	public Rank getDefaultRank() {
		
		if (this.def == null) {
			
			FileConfiguration rankConf = ConfigHelper.getConfig(Configuration.RANKS);
		    
		    String def = rankConf.getString("Default", "Member");
		    
		    Rank rank = Memory.RANK_CACHE.get(def);
		    
		    this.def = rank != null ? rank : new Rank(def);
		    
		}
		
		return this.def;
		
	}

	@Override
	public boolean load() {
		
		if (!this.isEmpty())
			this.clear();
		
		YamlConfig yml = Fukkit.getResourceHandler().getYaml(Configuration.RANKS);
		FileConfiguration conf = yml.getConfig();
		
		ConfigurationSection section = conf.getConfigurationSection("Ranks");
		
		if (section == null || (section != null && section.getKeys(false).size() == 0)) {
			
			conf.set("Ranks.Probation.Display", "<pp>[&fMember<sp>(<failure>P<sp>)<pp>]<reset> <reset>");
			conf.set("Ranks.Member.Display", "<pp>[&f%rank%<pp>]<reset> <reset>");
			conf.set("Ranks.Owner.Display", "<pp>[&4%rank%<pp>]<reset> <reset>");
			
			conf.set("Ranks.Probation.Weight", 0);
			conf.set("Ranks.Member.Weight", 0);
			
			conf.set("Ranks.Probation.Inherit", new ArrayList<String>());
			conf.set("Ranks.Member.Inherit", new ArrayList<String>());
			
			List<String> worlds = new ArrayList<String>();
			
			worlds.add("*");
			
			conf.set("Ranks.Probation.Worlds", worlds);
			conf.set("Ranks.Member.Worlds", worlds);
			
			List<String> perms = new ArrayList<String>();
			
			perms.add("permission.one");
			perms.add("permission.you-guessed-it.two");
			perms.add("another.permission.etc");
			
			conf.set("Ranks.Probation.Permissions", perms);
			conf.set("Ranks.Member.Permissions", new ArrayList<String>());
			yml.save();
			
		}
		
		section.getKeys(false).stream().forEach(r -> this.add(new Rank(r)));
		return true;
		
	}

}
