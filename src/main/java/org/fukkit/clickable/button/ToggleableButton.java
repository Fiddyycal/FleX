package org.fukkit.clickable.button;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.fukkit.entity.FleXPlayer;

import io.flex.commons.Nullable;

public abstract class ToggleableButton extends ExecutableButton {
	
	private static final long serialVersionUID = 1L;

	private boolean toggled = false;
	
	public ToggleableButton(Material material) {
		super(material, null);
	}
	
	public ToggleableButton(Material material, @Nullable String name, @Nullable String... lore) {
		super(material, name, 1, lore);
	}
	
	public ToggleableButton(Material material, @Nullable String name, int amount, @Nullable String... lore) {
		super(material, name, amount, (short) 0, lore);
	}
	
	public ToggleableButton(Material material, @Nullable String name, int amount, short damage, @Nullable String... lore) {
		super(material, name, amount, damage, null, lore);
	}
	
	public ToggleableButton(Material material, @Nullable String name, int amount, short damage, @Nullable Map<Enchantment, Integer> enchantments, @Nullable String... lore) {
		super(material, name, amount, damage, enchantments, lore);
	}
	
	public void toggle() {
		this.toggled = !this.toggled;
	}
	
	@Override
	public boolean onExecute(FleXPlayer player, ButtonAction action, Inventory inventory) {
		
		if (this.onToggle(player, action)) {
			this.toggle();
			return true;
		}
		
		return false;
		
	}
	
	public boolean isToggled() {
		return this.toggled;
	}
	
	public abstract boolean onToggle(FleXPlayer player, ButtonAction action);

}
