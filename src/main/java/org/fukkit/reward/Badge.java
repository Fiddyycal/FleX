package org.fukkit.reward;

import static java.util.Objects.requireNonNull;

import org.bukkit.ChatColor;
import org.fukkit.Memory;
import org.fukkit.event.FleXEventListener;
import org.fukkit.theme.Theme;

import io.flex.commons.Nullable;
import io.flex.commons.Tier;
import io.flex.commons.cache.Cacheable;

public class Badge extends FleXEventListener implements Cacheable {
	
	private Character icon;
	
	private String key, name, description;
	
	private Tier tier;
	
	public Badge(Character icon, String key, String name, Tier tier) {
		this(icon, key, name, tier, null);
	}
	
	public Badge(Character icon, String key, String name, Tier tier, @Nullable String description) {

		requireNonNull(icon, "icon must not be null");
		requireNonNull(key, "name must not be null");
		requireNonNull(name, "name must not be null");
		requireNonNull(tier, "tier must not be null");
		
		this.icon = icon;
		
		this.key = key.toUpperCase();
		this.name = name;
		this.description = description != null ? description : "Cannot be achieved";
		
		this.tier = tier;
		
	}
	
	public Character getIcon() {
		return this.icon;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDescription(Theme theme) {
		
		if (this.description.contains("%rank%"))
			this.description = this.description.replace("%rank%", Memory.RANK_CACHE.get("Owner").getDisplay(theme, false));
		
		return theme.format(this.description);
		
	}
	
	public Tier getTier() {
		return this.tier;
	}
	
	public String getDisplay(Theme theme, boolean present) {
		
		String prefix = (
				
				this.tier == Tier.TIER_ONE ? "<spc>" : 
				this.tier == Tier.TIER_TWO ? "<qc>" : "<sc>"
					
		);
		
		return theme.format(prefix + this.icon + (present ? (ChatColor.RESET + " <sc>" + (this.name.contains(" ") ? this.name.substring(0, this.name.lastIndexOf(' ')) : this.name)) : ""));
		
	}
	
	public String name() {
		return this.key;
	}
	
	/**
	 * Changing this to print getName() instead of key, when all warnings are removed change this to return this.name;
	 */
	@Deprecated
	@Override
	public String toString() {
		return this.key;
	}
	
}
