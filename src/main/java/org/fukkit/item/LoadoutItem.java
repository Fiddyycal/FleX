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
import org.fukkit.utils.BukkitUtils;

import io.flex.FleX.Task;
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
	public Set<? extends Clickable> getClickables() {
		
		// Some sub classes may access this method before the class is fully instantiated.
		if (this.loadouts == null)
			this.loadouts = new HashSet<Loadout>();
		
		if (this.loadouts.isEmpty())
			BukkitUtils.runLater(() -> {
				
				// If still empty after initially creating the items for loadouts.
				if (this.loadouts.isEmpty()) {
					
					Task.error("Loadout", "Potential Memory leak, please review:");
					Task.error("Loadout", "This LoadoutItem is not bound to any loadouts.");
					Task.error("Loadout", "Item: " + this.getType());
					Task.error("Loadout", "Meta: " + (this.hasItemMeta() ? (this.getItemMeta().hasDisplayName() ? this.getItemMeta().getDisplayName() : "NULL_DISPLAY") : null));
					
				}
				
			});
		
		return this.loadouts;
		
	}
	
	@Override
	public void bind(Clickable clickable) {
		
		if (clickable instanceof Loadout == false)
			throw new UnsupportedOperationException("clickable must bbe a loadout");
		
		this.loadouts.add((Loadout)clickable);
		
	}
	
	@Override
	public void unbind(Clickable clickable) {
		this.loadouts.remove(clickable);
	}
	
	@Override
	public boolean isLinked() {
		return !this.loadouts.isEmpty();
	}
	
	@Override
	public boolean isLinked(Clickable clickable) {
		return this.loadouts.contains(clickable);
	}
	
	@Override
	public ItemStack asItemStack() {
		return this;
	}

}
