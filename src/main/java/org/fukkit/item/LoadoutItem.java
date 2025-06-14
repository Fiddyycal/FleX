package org.fukkit.item;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.fukkit.clickable.Clickable;
import org.fukkit.clickable.Loadout;
import org.fukkit.clickable.button.Button;

import io.flex.commons.Nullable;

public class LoadoutItem extends Item implements Button {
	
	private Set<Loadout> loadouts = new HashSet<Loadout>();
	
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
	
	@Override
	public ItemStack asItemStack() {
		return this;
	}

	@Override
	public Loadout getClickable() {

		if (this.loadouts.isEmpty())
			throw new NullPointerException("Loadouts must be linked with LoadoutItem#linkTo");
		
		return this.loadouts.stream().findAny().get();
		
	}

	@Override
	public void linkTo(Clickable clickable) {
		
		if (clickable instanceof Loadout == false)
			throw new IllegalArgumentException("clickable must be Loadout");
		
		this.loadouts.add((Loadout)clickable);
		
	}

	@Override
	public void unlink(Clickable clickable) {
		
		if (clickable instanceof Loadout == false)
			throw new IllegalArgumentException("clickable must be Loadout");
		
		this.loadouts.remove((Loadout)clickable);
		
	}
	
	@Override
	public boolean isLinked() {
		return !this.loadouts.isEmpty();
	}

}
