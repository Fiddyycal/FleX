package org.fukkit.item;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.fukkit.clickable.button.Button;

import io.flex.commons.Nullable;

public class LoadoutItem extends Item implements Button {
	
	public LoadoutItem(Material material) {
		this(material, null);
	}
	
	public LoadoutItem(Material material, @Nullable String name, @Nullable String... lore) {
		this(material, name, 1, lore);
	}
	
	public LoadoutItem(Material material, @Nullable String name, int amount, @Nullable String... lore) {
		this(material, name, amount, (short) 0, lore);
	}
	
	public LoadoutItem(Material material, @Nullable String name, int amount, short damage, @Nullable String... lore) {
		this(material, name, amount, damage, null, lore);
	}
	
	public LoadoutItem(Material material, @Nullable String name, int amount, short damage, @Nullable Map<Enchantment, Integer> enchantments, @Nullable String... lore) {
		super(material, name, amount, damage, enchantments, lore);
	}
	
}
