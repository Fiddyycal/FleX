package org.fukkit.entity;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.fukkit.PlayerState;
import org.fukkit.clickable.Loadout;
import org.fukkit.disguise.FleXSkin;
import org.fukkit.reward.Badge;
import org.fukkit.reward.Rank;

import io.flex.commons.Nullable;

public interface FleXHumanEntity extends FleXLivingEntity, CommandSender {
	
	public long getPlayTime();
	
	public long getLastSeen();
	
	public Rank getRank();
	
	public Rank getMask();
	
	public Badge getBadge();
	
	public FleXSkin getSkin();
	
	public PlayerState getState();
	
	public Loadout getLoadout();
	
	public ItemStack getHelmet();
	
	public ItemStack getChestplate();
	
	public ItemStack getLeggings();
	
	public ItemStack getBoots();
	
	public ItemStack getItem(int slot);
	
	public ItemStack getItemInHand();
	
	public ItemStack getItemInOffHand();
	
	public void setItemInHand(ItemStack item);
	
	public void setItemInOffHand(ItemStack item);
	
	public void addLoadout(Loadout loadout);
	
	public void setRank(Rank rank);
	
	public void setMask(Rank mask);
	
	public void setBadge(Badge badge);
	
	public void setState(PlayerState state) throws FleXPlayerNotLoadedException;
	
	public void setLoadout(Loadout loadout, boolean overwrite);
	
	public void setHelmet(ItemStack item);
	
	public void setChestplate(ItemStack item);
	
	public void setLeggings(ItemStack item);
	
	public void setBoots(ItemStack item);
	
	public void setItem(int slot, ItemStack item);
	
	public void sendMessage(String message);
	
	public void sendMessage(String... message);
	
	public boolean isInventoryFull();
	
	public boolean isOnline();
	
	public boolean isMasked();
	
	public boolean isStaff();
	
	public boolean isUploaded();
	
	public boolean hasPermission(@Nullable String perm);

}
