package org.fukkit.clickable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.fukkit.Fukkit;
import org.fukkit.clickable.button.Button;
import org.fukkit.clickable.button.ExecutableButton;
import org.fukkit.clickable.button.UniqueButton;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.item.Item;
import org.fukkit.item.UniqueItem;

import io.flex.commons.cache.Cacheable;
import io.flex.commons.utils.ArrayUtils;

public abstract class Clickable implements Inventory, Cacheable, Metadatable {
	
	private Map<String, List<MetadataValue>> metadata = new HashMap<String, List<MetadataValue>>();
	private Map<Integer, Button> buttons  = new HashMap<Integer, Button>();
	
	protected Inventory inventory;
	
	private List<FleXHumanEntity> viewers = new ArrayList<FleXHumanEntity>();
	
	public Clickable(Inventory inventory, FleXHumanEntity... viewers) {
		
		this.inventory = inventory;
		
		for (FleXHumanEntity viewer : viewers)
			this.viewers.add(viewer);
		
	}
	
	/**
	 * @deprecated Use asBukkitInventory if you *need* the delegation.
	 */
	@Deprecated
	public Inventory getInventory() {
		return this.inventory;
	}
	
	@Override
	public List<HumanEntity> getViewers() {
		return this.viewers.stream().map(p -> (HumanEntity)p.getEntity()).collect(Collectors.toList());
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
		
		if (button instanceof UniqueButton)
			((UniqueButton)button).getHolders().add(this);
		
	}
	
	public void setButton(int slot, Button button) {
		
		if (slot > (this.inventory.getSize() - 1) || slot < 0)
			return;
		
		this.inventory.setItem(slot, button != null ? button.asItemStack() : null);
		
		if (button != null) {
			
			this.buttons.put(slot, button);
			
			if (button instanceof UniqueButton)
				((UniqueButton)button).getHolders().add(this);
			
		}
		
		else if (this.buttons.containsKey(slot)) {
			
			Button b = this.buttons.get(slot);
			
			this.buttons.remove(slot);
			
			if (b instanceof UniqueButton)
				((UniqueButton)b).getHolders().remove(this);
			
		}
		
	}
	
	public void removeButton(int slot) {
		
		this.inventory.setItem(slot, null);
		
		Button button = this.buttons.get(slot);
		
		if (button == null)
			return;
		
		this.buttons.remove(slot);
		
		if (button instanceof UniqueButton)
			((UniqueButton)button).getHolders().remove(this);
		
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
	
	public boolean hasButton(Button button) {
		return this.buttons.containsValue(button);
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
	
	@Override
	public void clear() {
		this.clear(-1);
	}
	
	public void clear(Integer... exclude) {
		
		for (Integer slot : new HashSet<Integer>(this.buttons.keySet())) {
			
			if (slot < 0)
				continue;
			
			if (ArrayUtils.contains(exclude, (Integer)slot))
				continue;
			
			this.removeButton(slot);
			
		}
		
	}
	
	public Inventory asBukkitInventory() {
		return this.inventory;
	}

	@Override
	public int getSize() {
		return this.inventory.getSize();
	}

	@Override
	public int getMaxStackSize() {
		return this.inventory.getMaxStackSize();
	}

	@Override
	public void setMaxStackSize(int size) {
		this.inventory.setMaxStackSize(size);
	}

	@Override
	public ItemStack getItem(int index) {
		return this.inventory.getItem(index);
	}

	@Override
	public void setItem(int index, ItemStack item) {
		this.inventory.setItem(index, item);
	}

	@Override
	public HashMap<Integer, ItemStack> addItem(ItemStack... items) throws IllegalArgumentException {
		return this.inventory.addItem(items);
	}

	@Override
	public HashMap<Integer, ItemStack> removeItem(ItemStack... items) throws IllegalArgumentException {
		return this.inventory.removeItem(items);
	}

	@Override
	public ItemStack[] getContents() {
		return this.inventory.getContents();
	}

	@Override
	public void setContents(ItemStack[] items) throws IllegalArgumentException {
		this.inventory.setContents(items);
	}

	@Override
	public ItemStack[] getStorageContents() {
		return this.inventory.getStorageContents();
	}

	@Override
	public void setStorageContents(ItemStack[] items) throws IllegalArgumentException {
		this.inventory.setStorageContents(items);
	}

	@Override
	public boolean contains(Material material) throws IllegalArgumentException {
		return this.inventory.contains(material);
	}

	@Override
	public boolean contains(ItemStack item) {
		return this.inventory.contains(item);
	}

	@Override
	public boolean contains(Material material, int amount) throws IllegalArgumentException {
		return this.inventory.contains(material, amount);
	}

	@Override
	public boolean contains(ItemStack item, int amount) {
		return this.inventory.contains(item, amount);
	}

	@Override
	public boolean containsAtLeast(ItemStack item, int amount) {
		return this.inventory.containsAtLeast(item, amount);
	}

	@Override
	public HashMap<Integer, ? extends ItemStack> all(Material material) throws IllegalArgumentException {
		return this.inventory.all(material);
	}

	@Override
	public HashMap<Integer, ? extends ItemStack> all(ItemStack item) {
		return this.inventory.all(item);
	}

	@Override
	public int first(Material material) throws IllegalArgumentException {
		return this.inventory.first(material);
	}

	@Override
	public int first(ItemStack item) {
		return this.inventory.first(item);
	}

	@Override
	public int firstEmpty() {
		return this.inventory.firstEmpty();
	}

	@Override
	public boolean isEmpty() {
		return this.inventory.isEmpty();
	}

	@Override
	public void remove(Material material) throws IllegalArgumentException {
		this.inventory.remove(material);
	}

	@Override
	public void remove(ItemStack item) {
		this.inventory.remove(item);
	}

	@Override
	public void clear(int index) {
		this.inventory.clear(index);
	}

	@Override
	public InventoryType getType() {
		return this.inventory.getType();
	}

	@Override
	public InventoryHolder getHolder() {
		return this.inventory.getHolder();
	}

	@Override
	public ListIterator<ItemStack> iterator() {
		return this.inventory.iterator();
	}

	@Override
	public ListIterator<ItemStack> iterator(int index) {
		return this.inventory.iterator(index);
	}

	@Override
	public Location getLocation() {
		return this.inventory.getLocation();
	}
	
}
