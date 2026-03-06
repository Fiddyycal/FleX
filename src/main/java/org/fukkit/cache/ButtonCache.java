package org.fukkit.cache;

import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.clickable.button.UniqueButton;

import io.flex.commons.cache.ConcurrentPerformanceCache;

public class ButtonCache extends ConcurrentPerformanceCache<UniqueButton, UUID> {

	private static final long serialVersionUID = -71037563061932004L;

	public ButtonCache() {
		super(button -> button.getUniqueId());
	}
	
	public UniqueButton getByItem(ItemStack itemStack) {
		
		removeNull();
		
		return this.stream().filter(button -> button.getUniqueId().equals(Fukkit.getImplementation().getItemStackUniqueId(itemStack))).findFirst().orElse(null);
		
	}
	
	private static void removeNull() {
		
		Iterator<UniqueButton> iterator = Memory.BUTTON_CACHE.iterator();
		
		while(iterator.hasNext()) {
			
			UniqueButton button = iterator.next();
			
			if (button.asItemStack() == null || button.asItemStack().getType() == Material.AIR)
				iterator.remove();
				
		}
		
	}

}
