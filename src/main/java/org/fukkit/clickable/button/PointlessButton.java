package org.fukkit.clickable.button;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.fukkit.entity.FleXPlayer;

import io.flex.commons.Nullable;

public class PointlessButton extends ExecutableButton {
	
	private static final long serialVersionUID = 7684359694291283253L;
	
	public PointlessButton(Material material) {
		super(material, null);
	}
	
	public PointlessButton(Material material, @Nullable String name, @Nullable String... lore) {
		super(material, name, 1, lore);
	}
	
	public PointlessButton(Material material, @Nullable String name, int amount, @Nullable String... lore) {
		super(material, name, amount, (short) 0, lore);
	}
	
	public PointlessButton(Material material, @Nullable String name, int amount, short damage, @Nullable String... lore) {
		super(material, name, amount, damage, null, lore);
	}
	
	public PointlessButton(Material material, @Nullable String name, int amount, short damage, @Nullable Map<Enchantment, Integer> enchantments, @Nullable String... lore) {
		super(material, name, amount, damage, enchantments, lore);
	}
	
	@Override
	public boolean onExecute(FleXPlayer player, ButtonAction action) {
		return true;
	}

}
