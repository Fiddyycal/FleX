package org.fukkit.backup;

import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.fukkit.event.FleXEventListener;
import org.fukkit.utils.VersionUtils;

public class EnchantmentListeners extends FleXEventListener {
	
	public EnchantmentListeners() {
		super();
	}

	@EventHandler
	@SuppressWarnings("deprecation")
	public void event(InventoryOpenEvent event) {
	    if (event.getInventory().getType() == InventoryType.ENCHANTING)
	    	event.getInventory().setItem(1, new ItemStack(VersionUtils.material("INK_SACK", "INK_SAC"), 64, (short)4));
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent event) {
	    if (event.getInventory().getType() == InventoryType.ENCHANTING)
	    	event.getInventory().setItem(1, null);
	}
	
}
