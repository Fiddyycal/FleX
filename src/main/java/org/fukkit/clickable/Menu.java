package org.fukkit.clickable;

import org.bukkit.event.inventory.InventoryType;
import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.utils.FormatUtils;

import io.flex.commons.Nullable;
import io.flex.commons.utils.StringUtils;

public class Menu extends Clickable {
	
	private String title;
	
	private boolean open = false;
	
	public Menu(String title, int rows, @Nullable FleXHumanEntity... viewers) {
		
		super(Fukkit.getInstance().getServer().createInventory(null, rows * 9, FormatUtils.format(title.length() > 32 ? StringUtils.shorten(title, 0, 31) + "-" : title)), viewers);
		
		this.setup(title);
		
	}
	
	public Menu(String title, InventoryType inventoryType, @Nullable FleXHumanEntity... viewers) {
		
		super(Fukkit.getInstance().getServer().createInventory(null, inventoryType, FormatUtils.format(title)), viewers);
		
		this.setup(title);
		
	}
	
	private void setup(String title) {
		
		this.title = title;
		
		Memory.GUI_CACHE.add(this);
		
	}
	
	public String getTitle() {
		return this.title;
	}
	
	/** @deprecated Probably will never use these, and they are very hacky, so phasing them out.
	public void setTitle(String title) {
		
		this.title = title;
		
		if (this.inventory.getType() == InventoryType.CHEST)
			this.inventory = Fukkit.getInstance().getServer().createInventory(null, this.inventory.getSize(), FormatUtils.format(title));
		
		else this.inventory = Fukkit.getInstance().getServer().createInventory(null, this.inventory.getType(), FormatUtils.format(title));
		
		this.getButtons().forEach((i, b) -> {
			this.inventory.setItem(i, b.asItemStack());
		});
		
		Fukkit.getServerHandler().getOnlinePlayersUnsafe().stream().filter(p -> p.getPlayer().getOpenInventory() != null).forEach(p -> {
			
			Menu menu = Memory.GUI_CACHE.getByPlayer(p);
			
			if (menu == this) {
				
				boolean del = this.deleteOnExit();
				
				this.deleteOnExit(false);
				
				p.closeMenu();
				p.openMenu(this, false);

				this.deleteOnExit(del);
				
			}
			
		});

	}
	
	public void setRows(int rows) {
		
		if (this.inventory.getType() != InventoryType.CHEST)
			throw new UnsupportedOperationException("Menu must be of the type CHEST.");
		
		this.inventory = Fukkit.getInstance().getServer().createInventory(null, rows * 9, this.title);
		
		this.getButtons().forEach((i, b) -> {
			this.inventory.setItem(i, b.asItemStack());
		});
		
		Fukkit.getServerHandler().getOnlinePlayersUnsafe().stream().filter(p -> p.get.getOpenInventory() != null).forEach(p -> {
			
			Menu menu = Memory.GUI_CACHE.getByPlayer(p);
			
			if (menu == this) {
				
				boolean del = this.deleteOnExit();
				
				this.deleteOnExit(false);
				
				p.closeInventory();
				p.openMenu(this, false);

				this.deleteOnExit(del);
				
			}
			
		});

	}
	*/
	
	public void setOpen(boolean open) {
		this.open = open;
	}
	
	public boolean isOpen() {
		return this.open;
	}
	
	/**
	 * 
	 * Changes orientation of the menu, putting all items on the top to the bottom.
	 * 
	 * @deprecated
	 * TODO Might do, might not, idk, don't really need it right now.
	 * 
	 */
	@Deprecated
	public void flip() {
		// TODO
	}
	
}
