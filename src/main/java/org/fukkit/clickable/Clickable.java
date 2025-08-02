package org.fukkit.clickable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.fukkit.Fukkit;
import org.fukkit.clickable.button.Button;
import org.fukkit.clickable.button.ExecutableButton;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.item.Item;
import org.fukkit.item.UniqueItem;

import io.flex.commons.cache.Cacheable;

public abstract class Clickable implements Cacheable, Metadatable {
	
	private Map<String, List<MetadataValue>> metadata = new HashMap<String, List<MetadataValue>>();
	private Map<Integer, Button> buttons  = new HashMap<Integer, Button>();
	
	protected Inventory inventory;
	
	private List<FleXHumanEntity> viewers = new ArrayList<FleXHumanEntity>();
	
	public Clickable(Inventory inventory, FleXHumanEntity... viewers) {
		
		this.inventory = inventory;
		
		for (FleXHumanEntity viewer : viewers)
			this.viewers.add(viewer);
		
	}
	
	public Inventory getInventory() {
		return this.inventory;
	}
	
	public List<FleXHumanEntity> getViewers() {
		return this.viewers;
	}
	
	@Deprecated
	public ExecutableButton getButton(ItemStack item) {
		
		for (Button button : this.buttons.values()) {
			
			if (button == null || item == null || item.getType() == Material.AIR)
				continue;
			
			UUID uid = Fukkit.getImplementation().getItemStackUniqueId(item);
			
			if (button instanceof ExecutableButton == false || uid == null)
				continue;
			
			if (uid.equals(((ExecutableButton)button).getUniqueId()))
				return (ExecutableButton) button;
			
		}
		
		return null;
		
	}
	
	public Button getButton(int slot) {
		return this.buttons.get(slot);
	}
	
	public Map<Integer, Button> getButtons() {
		return this.buttons;
	}
	
	public void addButton(Button button) {
		
		int i = this.inventory.firstEmpty();
		
		if (i == -1)
			return;
		
		this.inventory.setItem(i, button.asItemStack());
		this.buttons.put(i, button);
		
		button.linkTo(this);
		
	}
	
	public void setButton(int slot, Button button) {
		
		if (slot > (this.inventory.getSize() - 1) || slot < 0)
			return;
		
		this.inventory.setItem(slot, button != null ? button.asItemStack() : new ItemStack(Material.AIR));
		
		if (button != null) {
			
			this.buttons.put(slot, button);
			
			button.linkTo(this);
			
		}
		
		else if (this.buttons.containsKey(slot))
			this.buttons.remove(slot);
		
	}
	
	public void removeButton(int slot) {
		
		this.inventory.setItem(slot, null);
		
		Button button = this.buttons.get(slot);
		
		if (button == null)
			return;
		
		this.buttons.remove(slot);
		
		button.unlink(this);
		
	}
	
	public void removeButton(Button button) {
		
		ItemStack[] items = this.inventory.getContents();
		
		ItemStack item = button.asItemStack();
		
		for (int i = 0; i < items.length; i++) {
			
			ItemStack check = items[i];
			
			if (check == null)
				continue;
			
			UUID uid = Fukkit.getImplementation().getItemStackUniqueId(check);
			
			if (uid == null)
				continue;
			
			if (item instanceof UniqueItem) {
				
				if (((UniqueItem)item).getUniqueId().equals(uid))
					this.removeButton(i);
				
			} else if (item instanceof Item) {
				
				if (((Item)item).isSimilar(check))
					this.removeButton(i);
				
			} else {
				
				if (item.isSimilar(check))
					this.removeButton(i);
				
			}
			
		}
		
	}
	
	@Override
	public List<MetadataValue> getMetadata(String metadataKey) {
		return this.metadata.get(metadataKey);
	}
	
	@Override
	public void setMetadata(String metadataKey, MetadataValue metadataValue) {
		
		List<MetadataValue> values = this.metadata.get(metadataKey);
		
		if (values == null)
			values = new ArrayList<MetadataValue>();
		
		values.add(metadataValue);
		
		this.metadata.put(metadataKey, values);
		
	}
	
	@Override
	public void removeMetadata(String metadataKey, Plugin owningPlugin) {
		this.metadata.entrySet().removeIf(e -> {
			
			MetadataValue value = e.getValue().stream().findFirst().orElse(null);
			
			return e.getKey().equals(metadataKey) && value != null && value.getOwningPlugin() == owningPlugin;
			
		});
	}
	
	@Override
	public boolean hasMetadata(String metadataKey) {
		return this.metadata.containsKey(metadataKey);
	}
	
}
