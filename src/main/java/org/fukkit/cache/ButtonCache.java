package org.fukkit.cache;

import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.clickable.button.ExecutableButton;

import io.flex.commons.cache.LinkedCache;

public class ButtonCache extends LinkedCache<ExecutableButton, UUID> {

	private static final long serialVersionUID = -71037563061932004L;

	public ButtonCache() {
		super((button, uid) -> button.getUniqueId().equals(uid));
	}
	
	public ExecutableButton getByItem(ItemStack itemStack) {
		
		removeNull();
		
		return this.stream().filter(button -> button.getUniqueId().equals(Fukkit.getImplementation().getItemStackUniqueId(itemStack))).findFirst().orElse(null);
		
	}
	
	private static void removeNull() {
		
		Iterator<ExecutableButton> iterator = Memory.BUTTON_CACHE.iterator();
		
		while(iterator.hasNext()) {
			
			ExecutableButton button = iterator.next();
			
			if (button.asItemStack() == null || button.getType() == Material.AIR)
				iterator.remove();
				
		}
		
	}

}
