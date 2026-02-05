package org.fukkit.recording;

import java.util.Map;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;
import org.fukkit.entity.FleXPlayer;

public interface Recordable {
	
	UUID SYSTEM_UID = new UUID(0L, 0L);
	
	public String getName();
	
	public UUID getUniqueId();
	
	public Map<Long, Frame> getFrames();
	
	public ItemStack getHelmet();
	
	public ItemStack getChestplate();
	
	public ItemStack getLeggings();
	
	public ItemStack getBoots();
	
	public void setHelmet(ItemStack helmet);
	
	public void setChestplate(ItemStack chestplate);
	
	public void setLeggings(ItemStack leggings);
	
	public void setBoots(ItemStack boots);
	
	public FleXPlayer toPlayer();
	
	public boolean isBot();
	
}
