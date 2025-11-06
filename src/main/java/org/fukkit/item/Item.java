package org.fukkit.item;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.fukkit.Fukkit;
import org.fukkit.utils.BukkitUtils;
import org.fukkit.utils.FormatUtils;
import org.fukkit.utils.ItemUtils;

import io.flex.commons.Nullable;
import io.flex.commons.Overridden;
import io.flex.commons.utils.StringUtils;
import net.md_5.fungee.server.ServerVersion;

@SuppressWarnings("deprecation")
public class Item extends ItemStack implements Cloneable {
	
	private ItemTagStore store;
	
	private boolean changed = false, init = false;

	public Item(Material material) {
		this(material, null);
	}
	
	public Item(Material material, @Nullable String name, @Nullable String... lore) {
		this(material, name, 1, lore);
	}
	
	public Item(Material material, @Nullable String name, int amount, @Nullable String... lore) {
		this(material, name, amount, (short) 0, lore);
	}
	
	public Item(Material material, @Nullable String name, int amount, short damage, @Nullable String... lore) {
		this(material, name, amount, damage, null, lore);
	}
	
	public Item(Material material, @Nullable String name, int amount, short damage, @Nullable Map<Enchantment, Integer> enchantments, @Nullable String... lore) {
		
		super(material, amount, damage < 0 ? 0 : damage);
		
		this.init = true;
		
		BukkitUtils.runLater(() -> this.init = false);
		
		if (enchantments != null)
			super.addUnsafeEnchantments(enchantments);
		
		this.store = Fukkit.getImplementation().getItemTagStore(this);
		
		this.setName(name);
		
		if (lore != null)
			this.setLore(lore);
		
		this.update();
		
	}
	
	public String getName() {
		
		String name = super.hasItemMeta() ? super.getItemMeta().getDisplayName() : null;
		
		if (name != null)
			return name;
		
		try {
			
			ServerVersion version = Fukkit.getServerHandler().getServerVersion();
			
			name = version.is1_8() ? this.getType().getClass().getMethod("getKey").invoke(this.getType()).toString() : this.getType().name().toLowerCase();
			
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			
			e.printStackTrace();
			
			name = this.getType().name().toLowerCase();
			
		}
		
		return StringUtils.capitalize(name.replace("_", " "));
		
	}
	
	public ItemTagStore getTags() {
		return this.store;
	}

	public List<String> getLore() {
		return super.hasItemMeta() ? super.getItemMeta().getLore() : null;
	}

	public void setName(String name) {
		
		if (name == null)
			return;
		
		ItemMeta meta = super.getItemMeta();
		
		if (meta == null)
			return;
		
		meta.setDisplayName(!name.equals("") ? FormatUtils.format(name) : ChatColor.RESET.toString());
		
		super.setItemMeta(meta);

		this.update();
		
	}

	public void setLore(String... lore) {
		
		ItemMeta meta = super.getItemMeta();
		
		if (meta == null)
			return;
			
		List<String> lines = new ArrayList<String>();
		
		for (String line : lore)
			if (line != null)
				lines.add(FormatUtils.format(line));
		
		meta.setLore(lines);
		
		super.setItemMeta(meta);
		
		this.update();
		
	}

	public void setLoreLine(int line, String s) {
		
		ItemMeta meta = super.getItemMeta();
		
		if (meta == null)
			return;
		
		List<String> lore = new ArrayList<String>();
		
		if (!lore.isEmpty() && lore.size() > line) {
			if (s != null)
				lore.set(line, FormatUtils.format(s));
			else lore.remove(line);
		}
		
		meta.setLore(lore);
		
		super.setItemMeta(meta);

		this.update();
		
	}
	
	public void setType(Material material) {
		
		super.setType(material);

		this.update();
		
	}

	public void setAmount(int amount) {

		super.setAmount(amount);

		this.update();
		
	}

	public void setDurability(short durability) {
		
		super.setDurability(durability);

		this.update();
		
	}

	public void setData(MaterialData data) {
		
		super.setData(data);
		
		this.update();
		
	}
	
	public void setUnbreakable(boolean unbreakable) {
		
		ItemMeta meta = this.getItemMeta();
		
		if (meta == null)
			return;
		
		meta = ItemUtils.makeUnbreakable(meta, unbreakable);
		
		super.setItemMeta(meta);
		
		this.update();
		
	}
	
	@Override
	public boolean isSimilar(ItemStack item) {
		
		if (item.getType() != this.getType())
			return false;
		
		if (this.hasItemMeta()) {
			
			if (!item.hasItemMeta())
				return false;
			
			ItemMeta meta = item.getItemMeta();
			ItemMeta match = this.getItemMeta();
			
			if (!match.getDisplayName().equals(meta.getDisplayName()))
				return false;
			
			boolean lore = match.hasLore();
			boolean exists = meta.hasLore();
			
			if (lore || exists) {
				
				if (!lore)
					return false;
				
				if (!exists)
					return false;
				
				if (!match.getLore().toString().equals(meta.getLore().toString()))
					return false;
				
			}
			
		}
		
		return true;
		
	}
	
	public boolean isUnbreakable() {
		return super.hasItemMeta() ? ItemUtils.unbreakable(super.getItemMeta()) : false;
	}
	
	public boolean unchanged() {
		return !this.changed;
	}
	
	@Override
	public String toString() {
		return ItemUtils.serialize(this);
	}
    
    public void update(boolean force) {
    	
    	if (this.init)
    		return;
    	
    	this.changed = true;
		
		this.onUpdate(force);
		
		this.changed = false;
		
    }
	
	public void update() {
		this.update(false);
	}
    
	@Overridden
    public void onUpdate(boolean force) {
    	/**
    	 * Do nothing unless implementation is overridden.
    	 */
    }
	
	public ItemStack asItemStack() {
		return this;
	}
	
	public static Item asFukkitCopy(ItemStack item) {
		
		if (item == null)
			throw new NullPointerException("item must not be null");
		
		Item copy;
		
		if (item.hasItemMeta()) {
			
			ItemMeta meta = item.getItemMeta();
			List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<String>();
			
			copy = new Item(item.getType(), meta.getDisplayName(), item.getAmount(), item.getDurability(), item.getEnchantments(), lore.toArray(new String[lore.size()]));
			copy.setItemMeta(item.getItemMeta());
			
		} else {
			
			copy = new Item(item.getType());
			copy.setAmount(item.getAmount());
			copy.setDurability(item.getDurability());
			copy.addEnchantments(item.getEnchantments());
			
		}
		
		copy.getTags().setString("uid", null);
		
		return copy;
		
	}

}

