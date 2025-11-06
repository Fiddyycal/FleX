package org.fukkit.clickable.button;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.fukkit.clickable.Clickable;

public interface Button {
	
	public static final Button SPACER = new FacelessButton(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
	public static final Button LOADING = new PointlessButton(Material.LIGHT_GRAY_STAINED_GLASS_PANE, ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Loading...");
	
	public Set<Clickable> getClickables();
	
	public ItemStack asItemStack();
	
	public void bind(Clickable clickable);
	
	public void unbind(Clickable clickable);
	
	public boolean isLinked();
	
	public boolean isLinked(Clickable clickable);
	
}
