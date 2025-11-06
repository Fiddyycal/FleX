package org.fukkit.clickable.button;

import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import io.flex.commons.cache.Cacheable;

public interface UniqueButton extends Button, Cacheable {
	
	public static final UniqueButton SPACER = new FacelessButton(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
	public static final UniqueButton LOADING = new PointlessButton(Material.LIGHT_GRAY_STAINED_GLASS_PANE, ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Loading...");
	
	public UUID getUniqueId();
	
	public Set<Inventory> getHolders();
	
}
