package org.fukkit.item;

import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.SkullMeta;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.utils.VersionUtils;

import io.flex.commons.Nullable;

@SuppressWarnings("deprecation")
public class Head extends UniqueItem {
	
	public Head(String player) {
		this(player, null);
	}
	
	public Head(FleXPlayer player, @Nullable String name, @Nullable String... lore) {
		this(player, name, 1, lore);
	}
	
	public Head(FleXPlayer player, @Nullable String name, int amount, @Nullable String... lore) {
		this(player, name, amount, null, lore);
	}
	
	public Head(FleXPlayer player, @Nullable String name, int amount, @Nullable Map<Enchantment, Integer> enchantments, @Nullable String... lore) {
		
		super(VersionUtils.material("SKULL_ITEM", "PLAYER_HEAD"), name, amount, (short) 3, enchantments, lore);
		
		this.setOwner(player);
		
	}
	
	public Head(String player, @Nullable String name, @Nullable String... lore) {
		this(player, name, 1, lore);
	}
	
	public Head(String player, @Nullable String name, int amount, @Nullable String... lore) {
		this(player, name, amount, null, lore);
	}
	
	public Head(String player, @Nullable String name, int amount, @Nullable Map<Enchantment, Integer> enchantments, @Nullable String... lore) {
		
		super(VersionUtils.material("SKULL_ITEM", "PLAYER_HEAD"), name, amount, (short) 3, enchantments, lore);
		
		this.setOwner(player);
		
	}
	
	public void setOwner(FleXPlayer player) {
		
		SkullMeta meta = (SkullMeta) this.getItemMeta();
		
		try {
			meta.setOwnerProfile(player.getPlayer().getPlayerProfile());
		} catch (NoSuchMethodError | Exception e) {
			this.setOwner(player.getName());
		}
		
		this.setItemMeta(meta);
		
	}
	
	public void setOwner(String player) {
		
		SkullMeta meta = (SkullMeta) this.getItemMeta();
		meta.setOwner(player);
		this.setItemMeta(meta);
		
	}

}
