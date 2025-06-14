package org.fukkit.item;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.fukkit.Fukkit;
import org.fukkit.utils.ItemUtils;

import io.flex.commons.Nullable;

@SuppressWarnings("deprecation")
public class UniqueItem extends Item {
	
	protected UUID uuid;

	public UniqueItem(Material material) {
		this(material, null);
	}
	
	public UniqueItem(Material material, @Nullable String name, @Nullable String... lore) {
		this(material, name, 1, lore);
	}
	
	public UniqueItem(Material material, @Nullable String name, int amount, @Nullable String... lore) {
		this(material, name, amount, (short) 0, lore);
	}
	
	public UniqueItem(Material material, @Nullable String name, int amount, short damage, @Nullable String... lore) {
		this(material, name, amount, damage, null, lore);
	}
	
	public UniqueItem(Material material, @Nullable String name, int amount, short damage, @Nullable Map<Enchantment, Integer> enchantments, @Nullable String... lore) {
		this(null, material, name, amount, damage, enchantments, lore);
	}
	
	protected UniqueItem(@Nullable UUID uuid, Material material, @Nullable String name, int amount, short damage, @Nullable Map<Enchantment, Integer> enchantments, @Nullable String... lore) {
		
		super(material, name, amount, damage, enchantments, lore);
		
		if (enchantments != null)
			super.addUnsafeEnchantments(enchantments);
		
		UUID uid = uuid != null ? uuid : this.getUniqueId();
		
		if (uid == null)
			uid = Fukkit.getImplementation().getItemStackUniqueId(this);
		
		this.getTags().setString("uid", (this.uuid = (uid != null ? uid : UUID.randomUUID())).toString());
		
		this.setName(name);
		
		if (lore != null)
			this.setLore(lore);
		
		this.update();
		
	}
	
	public UUID getUniqueId() {
		return this.uuid;
	}
	
	@Override
	public String toString() {
		return ItemUtils.serialize(this);
	}
	
	public static UniqueItem of(ItemStack item, @Nullable UUID uuid) {
		
		if (item == null)
			throw new NullPointerException("item must not be null");
		
		return new UniqueItem(
				
				uuid,
				item.getType(),
				item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : null,
				item.getAmount(),
				item.getDurability(),
				item.getEnchantments(),
				item.hasItemMeta() && item.getItemMeta().hasLore() ? item.getItemMeta().getLore().toArray(new String[item.getItemMeta().getLore().size()]) : null);
		
	}

}
