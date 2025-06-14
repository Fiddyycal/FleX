package org.fukkit.clickable.button;

import org.bukkit.inventory.ItemStack;
import org.fukkit.clickable.Clickable;

public interface Button {
	
	public Clickable getClickable();
	
	public ItemStack asItemStack();
	
	public void linkTo(Clickable clickable);
	
	public void unlink(Clickable clickable);
	
	public boolean isLinked();
	
}
