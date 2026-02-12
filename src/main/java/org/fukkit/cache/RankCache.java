package org.fukkit.cache;

import java.util.ArrayList;
import java.util.Comparator;
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
		    
		    String def = rankConf.getString("default", "Member");
		    
		    Rank rank = Memory.RANK_CACHE.get(def);
		    
		    this.def = rank != null ? rank : new Rank(def);
		    
		}
		
		return this.def;
		
	}
	
	/**
	 * @param weight
	 * @return Rank with weight, if no rank is found, returns the next rank below that.
	 */
	public Rank getByWeight(int weight) {
	    return this.stream().filter(r -> r.getWeight() <= weight).max(Comparator.comparingLong(r -> r.getWeight())).orElse(null);
	}

	@Override
	public boolean load() {
		
		if (!this.isEmpty())
			this.clear();
		
		YamlConfig yml = Fukkit.getResourceHandler().getYaml(Configuration.RANKS);
		FileConfiguration conf = yml.asFileConfiguration();
		
		ConfigurationSection section = conf.getConfigurationSection("ranks");
		
		if (section == null || (section != null && section.getKeys(false).size() == 0)) {
			
			conf.set("ranks.Probation.display", "&4&lP&r &7");
			conf.set("ranks.Member.display", "&7");
			conf.set("ranks.Owner.display", "&4&lOwner&r &f");
			
			conf.set("ranks.Probation.weight", 0);
			conf.set("ranks.Member.weight", 0);
			
			conf.set("ranks.Probation.inherit", new ArrayList<String>());
			conf.set("ranks.Member.inherit", new ArrayList<String>());
			
			List<String> worlds = new ArrayList<String>();
			
			worlds.add("*");
			
			conf.set("ranks.Probation.worlds", worlds);
			conf.set("ranks.Member.worlds", worlds);
			
			List<String> perms = new ArrayList<String>();
			
			perms.add("permission.one");
			perms.add("permission.you-guessed-it.two");
			perms.add("another.permission.etc");
			
			conf.set("ranks.Probation.permissions", perms);
			conf.set("ranks.Member.permissions", new ArrayList<String>());
			yml.save();
			
		}
		
		section.getKeys(false).stream().forEach(r -> this.add(new Rank(r)));
		return true;
		
	}

}
