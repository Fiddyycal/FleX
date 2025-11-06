package org.fukkit.clickable.button;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface Button {
	
	public static final Button SPACER = new FacelessButton(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
	public static final Button LOADING = new PointlessButton(Material.LIGHT_GRAY_STAINED_GLASS_PANE, ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Loading...");
	
	public ItemStack asItemStack();
	
}
