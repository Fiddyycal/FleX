package org.fukkit.cache;

import java.util.Arrays;
import java.util.ConcurrentModificationException;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.fukkit.Memory;
import org.fukkit.clickable.Menu;
import org.fukkit.clickable.button.ExecutableButton;
import org.fukkit.entity.FleXPlayer;

import io.flex.FleX.Task;
import io.flex.commons.cache.ConcurrentPerformanceCache;

@SuppressWarnings("deprecation")
public class GuiCache extends ConcurrentPerformanceCache<Menu, Inventory> {

	private static final long serialVersionUID = -9198738498209231101L;

	// This was the old way
	//Arrays.asList(gui.asBukkitInventory().getContents()).equals(Arrays.asList(inv.getContents()))
	
	public GuiCache() {
		super((gui) -> gui.asBukkitInventory());
	}
	
	@Override
	public Menu get(Inventory arg0) {
		return this.stream().filter(g -> g.asBukkitInventory() == arg0 || g.asBukkitInventory().equals(arg0)).findFirst().orElse(null);
	}

	/**
	 * @bandaid
	 * @eprecated Bandaid approach.
	 * If players can rename items, this could be an exploit.
	 * Although very rare, if no other NBT is present, similar items will return a false positive.
	 *
	@eprecated
	public Menu getByInventory(Inventory inventory) {
		return this.stream().filter(g -> {
			
			String uid = "uid=........-....-....-....-............", match = "uid={removed_to_match}";
			String guiM = Arrays.asList(g.asBukkitInventory().getContents()).toString().replaceAll(uid, match);
			String invM = Arrays.asList(inventory.getContents()).toString().replaceAll(uid, match);
			
			return guiM.equals(invM);
			
		}).findFirst().orElse(null);
	}
	*
	*/
	
	@Override
	public void onRemove(Menu e) {
		e.getButtons().values().removeIf(b -> {
			
			if (b instanceof ExecutableButton)
				Memory.BUTTON_CACHE.remove((ExecutableButton)b);
				
			return true;
			
		});
	}
	
	public Menu getByItemExact(ItemStack itemStack) {
		
		Menu[] menus = this.toArray(new Menu[this.size()]);
		
		for (int i = 0; i < this.size(); i++) {

			Menu gui = null;
			
			try {
				
				gui = menus[i];
				
			} catch (ConcurrentModificationException e) {
				
				Task.debug("ConcurrentModificationException", "A CME occurred in " + this.getClass().getCanonicalName() + ", FleX has taken measures to take care of it.");
				
				gui = null;
				
			}
			
			if (gui != null && gui.getButton(itemStack) != null)
				return gui;
			
		}
		
		return null;
		
	}
	
	public Menu getByItem(ItemStack itemStack) {
		return this.stream().filter(gui -> Arrays.stream(gui.getInventory().getContents()).anyMatch(i -> i != null && i.isSimilar(itemStack))).findFirst().orElse(null);
	}
	
	public Menu getByPlayerExact(FleXPlayer player) {
		return this.stream().filter(gui -> {
			
			if (gui.getViewers().contains(player.getEntity()))
				return true;
			
			InventoryView view = player.getPlayer().getOpenInventory();
			
			if (view == null)
				return false;
			
			return gui.getButtons().keySet().stream().anyMatch(i -> gui.getButton(view.getItem(i)) != null);
			
		}).findFirst().orElse(null);
	}
	
	public Menu getByPlayer(FleXPlayer player) {
		return this.stream().filter(gui -> player.getPlayer().getOpenInventory() == gui.asBukkitInventory()).findFirst().orElse(null);
	}
	/*
	public Menu getByPlayer(FleXPlayer player) {
		return this.stream().filter(gui -> player.getPlayer().getOpenInventory().getTitle().equals(gui.getTitle())).findFirst().orElse(null);
	}
*/
}
