package org.fukkit.item;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionType;

import io.flex.commons.Nullable;

@SuppressWarnings("deprecation")
public class Potion extends UniqueItem {
	
	private org.bukkit.potion.Potion potion;
	
	public Potion() {
		this(null);
	}
	
	public Potion(@Nullable String name, @Nullable String... lore) {
		this(name, 1, lore);
	}
	
	public Potion(@Nullable String name, int amount, @Nullable String... lore) {
		this(name, amount, null, lore);
	}
	
	public Potion(@Nullable String name, int amount, @Nullable Map<Enchantment, Integer> enchantments, @Nullable String... lore) {
		
		super(Material.POTION, name, amount, (short) 3, enchantments, lore);
		
		this.potion = new org.bukkit.potion.Potion(PotionType.WATER);
		
	}
	
	public void setPotionType(PotionType type) {
		this.potion.setType(type);
		this.potion.apply(this);
	}
	
	public void setLevel(int level) {
		this.potion.setLevel(level);
		this.potion.apply(this);
	}
	
	public void setSplash() {
		this.potion.isSplash();
		this.potion.apply(this);
	}
	
	public void setExtended(boolean isExtended) {
		this.potion.setHasExtendedDuration(isExtended);
		this.potion.apply(this);
	}

	public boolean isSplash() {
		return this.potion.isSplash();
	}
	
}
