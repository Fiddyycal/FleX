package org.fukkit.item;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.fukkit.clickable.Clickable;
import org.fukkit.clickable.Loadout;
import org.fukkit.clickable.button.Button;

import io.flex.commons.Nullable;

public class LoadoutUniqueItem extends UniqueItem implements Button {
	
	private Set<Loadout> loadouts = new HashSet<Loadout>();
	
	public LoadoutUniqueItem(Material material) {
		this(material, null);
	}
	
	public LoadoutUniqueItem(Material material, @Nullable String name, @Nullable String... lore) {
		this(material, name, 1, lore);
	}
	
	public LoadoutUniqueItem(Material material, @Nullable String name, int amount, @Nullable String... lore) {
		this(material, name, amount, (short) 0, lore);
	}
	
	public LoadoutUniqueItem(Material material, @Nullable String name, int amount, short damage, @Nullable String... lore) {
		this(material, name, amount, damage, null, lore);
	}
	
	public LoadoutUniqueItem(Material material, @Nullable String name, int amount, short damage, @Nullable Map<Enchantment, Integer> enchantments, @Nullable String... lore) {
		super(material, name, amount, damage, enchantments, lore);
	}
	
	private LoadoutUniqueItem(@Nullable UUID uuid, Material material, @Nullable String name, int amount, short damage, @Nullable Map<Enchantment, Integer> enchantments, @Nullable String... lore) {
		super(uuid, material, name, amount, damage, enchantments, lore);
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
	
	@SuppressWarnings("deprecation")
	public static LoadoutUniqueItem of(ItemStack item, @Nullable UUID uuid) {
		
		if (item == null)
			throw new NullPointerException("item must not be null");
		
		return new LoadoutUniqueItem(
				
				uuid,
				item.getType(),
				item.hasItemMeta() ? item.getItemMeta().getDisplayName() : null,
				item.getAmount(),
				item.getDurability(),
				item.getEnchantments(),
				item.hasItemMeta() && item.getItemMeta().hasLore() ? item.getItemMeta().getLore().toArray(new String[item.getItemMeta().getLore().size()]) : null);
		
	}

}
