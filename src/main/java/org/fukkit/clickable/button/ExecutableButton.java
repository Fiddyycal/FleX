package org.fukkit.clickable.button;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.clickable.Menu;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.item.UniqueItem;

import io.flex.FleX.Task;
import io.flex.commons.Nullable;
import io.flex.commons.cache.Cacheable;
import io.flex.commons.utils.ClassUtils;

public abstract class ExecutableButton extends UniqueItem implements UniqueButton, Serializable, Cacheable {
	
	private static final long serialVersionUID = 8721156523715764987L;
	
	private Set<Inventory> holders = new HashSet<Inventory>();
	
	private boolean intractable, droppable;
	
	public ExecutableButton(Material material) {
		this(material, null);
	}
	
	public ExecutableButton(Material material, @Nullable String name, @Nullable String... lore) {
		this(material, name, 1, lore);
	}
	
	public ExecutableButton(Material material, @Nullable String name, int amount, @Nullable String... lore) {
		this(material, name, amount, (short) 0, lore);
	}
	
	public ExecutableButton(Material material, @Nullable String name, int amount, short damage, @Nullable String... lore) {
		this(material, name, amount, damage, null, lore);
	}
	
	public ExecutableButton(Material material, @Nullable String name, int amount, short damage, @Nullable Map<Enchantment, Integer> enchantments, @Nullable String... lore) {
		
		super(material, name, amount, damage, enchantments, lore);
		
		this.intractable = ClassUtils.getSuperAnnotation(this.getClass(), Intractable.class) != null;
		this.droppable = ClassUtils.getSuperAnnotation(this.getClass(), Droppable.class) != null;
		
		if (this instanceof PointlessButton || this instanceof FacelessButton)
			return;
		
		Memory.BUTTON_CACHE.add(this);
		
	}
	
	public boolean isIntractable() {
		return this.intractable;
	}
	
	public boolean isDroppable() {
		return this.droppable;
	}
	
	@Override
	public Set<Inventory> getHolders() {
		return this.holders;
	}
	
	@Override
	public void onUpdate(boolean force) {
		
		if (this instanceof PointlessButton || this instanceof FacelessButton)
			return;
		
		if (!force && this.unchanged())
			return;
		
		Set<Inventory> holders = this.holders;
		
		if (holders == null || holders.isEmpty())
			return;
		
		holders.forEach(inv -> {
			
			UUID uuid = this.getUniqueId();
			
			if (inv instanceof Menu) {
				
				Entry<Integer, Button> button = ((Menu)inv).getButtons().entrySet().stream().filter(e -> e.getValue() == this).findFirst().orElse(null);
				
				if (button == null)
					return;
				
				int slot = button.getKey();
				
				ItemStack item = inv.getContents()[slot];
				
				if (item == null)
					return;
				
				if (!similar(item, this))
					return;
				
				UUID uid = Fukkit.getImplementation().getItemStackUniqueId(item);
				
				if (uid == null)
					return;
				
				if (uid.equals(uuid)) {
					
					copy(item, this);
			    	
					try {
						inv.setItem(slot, item);
					} catch (NullPointerException e) {}
					
				}
				
				inv.getViewers().stream().filter(p -> p instanceof FleXPlayer).forEach(p -> {
					((FleXPlayer)p).getPlayer().updateInventory();
				});
				
			} else {
				
				Bukkit.getOnlinePlayers().forEach(p -> {
					
				    InventoryView view = p.getOpenInventory();
				    
				    int slotLimit = view.countSlots();

				    for (int i = 0; i < slotLimit; i++) {
				    	
				        try {
				        	
				            ItemStack item = view.getItem(i);
				            
				            if (item == null || item.getType() == Material.AIR)
				                continue;

				            if (!similar(item, this))
				                continue;
				            
				            UUID uid = Fukkit.getImplementation().getItemStackUniqueId(item);
				            
				            if (uid != null && uid.equals(uuid)) {
				            	
				                copy(item, this);
				                
				                view.setItem(i, item);
				                
				            }
				            
				        } catch (IndexOutOfBoundsException ignored) {
				            break;
				        }
				        
				    }
				});
				
			}
			
		});
		
    }

	public boolean exec(FleXPlayer player, ButtonAction action, Inventory inventory) {
		return this.onExecute(player, action, inventory);
	}
	
	public abstract boolean onExecute(FleXPlayer player, ButtonAction action, Inventory inventory);
	
	private static boolean similar(ItemStack item, ItemStack similar) {
		
		if (item.getType() != similar.getType())
			return false;
		
		if (similar.hasItemMeta()) {
			
			if (!item.hasItemMeta())
				return false;
			
			ItemMeta meta = item.getItemMeta();
			ItemMeta match = similar.getItemMeta();
			
			if (!match.getDisplayName().equals(meta.getDisplayName()))
				return false;

			boolean lore = meta.hasLore();
			boolean exists = match.hasLore();
			
			if (lore && !exists || !lore && exists)
				return false;
			
		}
		
		return true;
		
	}
	
	@SuppressWarnings("deprecation")
	private static void copy(ItemStack item, ItemStack with) {
		
		item.setType(with.getType());
		item.setDurability(with.getDurability());
		item.setAmount(with.getAmount());
	    item.setData(with.getData());
			
	    if (with.hasItemMeta())
		    item.setItemMeta(with.getItemMeta());
	    	
	    try {
    		
	    	 if (with.getEnchantments() != null)
	 	    	item.addUnsafeEnchantments(with.getEnchantments());
    		
		} catch (NullPointerException ignore) {
			
			Task.debug("Try/Catch", "CraftItemStack threw an NPE adding custom enchantment: " + ignore.getMessage());
			Task.debug("Try/Catch", "Is the enchantment registered after JVM runtime?");
			
		}
	    
	}
	
}
