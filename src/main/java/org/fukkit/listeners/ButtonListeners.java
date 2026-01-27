package org.fukkit.listeners;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.api.helper.EventHelper;
import org.fukkit.clickable.Loadout;
import org.fukkit.clickable.Menu;
import org.fukkit.clickable.button.ExecutableButton;
import org.fukkit.clickable.button.UniqueButton;
import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.FleXEventListener;
import org.fukkit.event.player.PlayerButtonExecuteEvent;
import org.fukkit.event.player.PlayerGuiClickEvent;
import org.fukkit.event.player.PlayerGuiCloseEvent;
import org.fukkit.event.player.PlayerGuiOpenEvent;
import org.fukkit.event.player.PlayerLoadoutClickEvent;
import org.fukkit.utils.BukkitUtils;

public class ButtonListeners extends FleXEventListener {
	
	@EventHandler
	public void event(InventoryCloseEvent event) {
		
		if (event.getPlayer() instanceof Player == false)
			return;
		
		Player player = (Player) event.getPlayer();
		
		if (Memory.GUI_CACHE.get(event.getInventory()) != null) {
			
			Menu gui = Memory.GUI_CACHE.get(event.getInventory());
			
			PlayerGuiCloseEvent closeEvent = new PlayerGuiCloseEvent(player, gui, false);
			
			EventHelper.callEvent(closeEvent);
			
			if (closeEvent.isCancelled()) {
				
				BukkitUtils.runLater(() -> {
					
					if (player == null || !player.isOnline())
						return;
					
					Fukkit.getPlayerExact(event.getPlayer()).openMenu(gui, true);
					
				});
				
				return;
				
			}
			
			gui.setOpen(false);
			
			Memory.GUI_CACHE.remove(gui);
			
		}
	}
	
	@EventHandler
	public void event(InventoryOpenEvent event) {
		
		if (Memory.GUI_CACHE.get(event.getInventory()) != null) {
			
			Menu gui = Memory.GUI_CACHE.get(event.getInventory());
			
			PlayerGuiOpenEvent openEvent = new PlayerGuiOpenEvent((Player) event.getPlayer(), gui, false);
			
			EventHelper.callEvent(openEvent);
			
			if (openEvent.isCancelled()) {
				event.setCancelled(true);
				return;
			}
			
			gui.setOpen(true);
			
		}
		
	}
	
	@EventHandler
	public void event(InventoryClickEvent event) {
		
		if (event.getClick() == ClickType.CREATIVE)
		    return;
		
		HumanEntity entity = event.getWhoClicked();
		
		if (entity instanceof Player == false)
			return;
		
		Inventory clicked = event.getClickedInventory();
		
		if (clicked == null) {
			
			Inventory inv = event.getInventory();
			
			if (!(inv.getClass().getName().contains("Custom") || (inv.getType() == InventoryType.CHEST && inv.getHolder() == null)))
				return;
			
			entity.closeInventory();
			return;
			
		} else clicked = entity.getOpenInventory().getTopInventory();
		
		ClickType click = event.getClick();
		
		ItemStack item = event.getCurrentItem();
		
		if ((item == null || item.getType() == Material.AIR) && click == ClickType.NUMBER_KEY && event.getAction() == InventoryAction.HOTBAR_SWAP)
			item = clicked.getItem(event.getHotbarButton());
		
		/**
		 * @bandaid
		 * What ever is on the curser is not guaranteed,
		 * this is simply a last stitch effort to stop menu griefing.
		 */
		if (item == null || item.getType() == Material.AIR)
			item = event.getCursor();
		
		if (item == null || item.getType() == Material.AIR)
			return;
		
		UniqueButton butt = Memory.BUTTON_CACHE.getByItem(item);
		
		if (butt instanceof ExecutableButton == false)
			return;
		
		ExecutableButton button = (ExecutableButton) butt;
		
		FleXPlayer player = Fukkit.getPlayerExact((Player)entity);
		
		ButtonAction action = this.change(click);
		
		PlayerButtonExecuteEvent clickEvent = null;
		
		Menu menu = Memory.GUI_CACHE.get(clicked);
		
		if (menu != null)
			clickEvent = new PlayerGuiClickEvent(player, menu, button, action, false);
		
		else {
			
			if (button != null) {
				
				Loadout lo = player.getLoadout();
				
				if (lo != null && lo.hasButton(button))
					clickEvent = new PlayerLoadoutClickEvent(player, lo, button, action, false);
				
				else clickEvent = new PlayerButtonExecuteEvent(player, button, action, false);
				
			}
			
		}
		
		if (clickEvent != null) {
			
			EventHelper.callEvent(clickEvent);
			
			if (clickEvent.isCancelled())
				return;
			
		}
		
		if (button != null) {
			
			clickEvent.setExecuted(button.exec(player, action, menu != null ? menu : clicked));
			
			if (button.isDroppable())
				return;
			
		}
		
		if (menu == null)
			return;
		
		event.setCancelled(true);
		
	}
	
	@EventHandler
    public void event(PlayerInteractEvent event) {
		
		if (event.getItem() == null)
			return;
		
		ItemStack item = event.getItem();
		
		UniqueButton butt = Memory.BUTTON_CACHE.getByItem(item);
		
		if (butt instanceof ExecutableButton == false)
			return;
		
		ExecutableButton button = (ExecutableButton) butt;
		
		if (button != null) {
			
			Player pl = event.getPlayer();
			FleXPlayer player = Fukkit.getPlayer(pl.getUniqueId());
			
			ButtonAction action = this.toButtonAction(event.getAction(), player.getPlayer().isSneaking());
			
			PlayerButtonExecuteEvent clickEvent;

			Loadout lo = player.getLoadout();
			/*
			
			if (lo != null) {
				
				clickEvent = new PlayerLoadoutClickEvent(player, lo, button, action, false);
				
			}
			*/
			
			clickEvent = new PlayerButtonExecuteEvent(player, button, action, false);
			
			EventHelper.callEvent(clickEvent);
			
			if (clickEvent.isCancelled())
				return;
			
			clickEvent.setExecuted(button.exec(player, action, lo != null ? lo : pl.getInventory()));
			
			if (button.isIntractable())
				return;
			
			event.setCancelled(true);
			
			player.updateMenu();
			return;
			
		}
		
	}
	
	@EventHandler
    public void event(PlayerDropItemEvent event) {
		
		if (event.isCancelled())
			return;
		
		ItemStack item = event.getItemDrop().getItemStack();
		
		UniqueButton butt = Memory.BUTTON_CACHE.getByItem(item);
		
		if (butt instanceof ExecutableButton == false)
			return;
		
		ExecutableButton button = (ExecutableButton) butt;
		
		if (button != null) {

			Player pl = event.getPlayer();
			FleXPlayer player = Fukkit.getPlayer(pl.getUniqueId());
			
			PlayerButtonExecuteEvent clickEvent;

			Loadout lo = player.getLoadout();
			/*
			
			if (lo != null) {
				
				clickEvent = new PlayerLoadoutClickEvent(player, lo, button, action, false);
				
			}
			*/
			
			clickEvent = new PlayerButtonExecuteEvent(player, button, ButtonAction.INTERACT_DROP, false);
			
			EventHelper.callEvent(clickEvent);
			
			if (clickEvent.isCancelled())
				return;
			
			clickEvent.setExecuted(button.exec(player, ButtonAction.INTERACT_DROP, lo != null ? lo : pl.getInventory()));
			
			if (button.isDroppable())
				return;
			
			event.setCancelled(true);
			return;
			
		}
		
	}
	
	private ButtonAction change(ClickType clickType) {
		switch (clickType) {
		case LEFT:
			return ButtonAction.GUI_LEFT_CLICK;
		case SHIFT_LEFT:
			return ButtonAction.GUI_SHIFT_LEFT_CLICK;
		case RIGHT:
			return ButtonAction.GUI_RIGHT_CLICK;
		case SHIFT_RIGHT:
			return ButtonAction.GUI_SHIFT_RIGHT_CLICK;
		case WINDOW_BORDER_LEFT:
			return ButtonAction.GUI_WINDOW_BORDER_LEFT_CLICK;
		case WINDOW_BORDER_RIGHT:
			return ButtonAction.GUI_WINDOW_BORDER_RIGHT_CLICK;
		case MIDDLE:
			return ButtonAction.GUI_MIDDLE_CLICK;
		case NUMBER_KEY:
			return ButtonAction.GUI_NUMBER_PRESS;
		case DOUBLE_CLICK:
			return ButtonAction.GUI_DOUBLE_LEFT_CLICK;
		case DROP:
			return ButtonAction.GUI_DROP;
		case CONTROL_DROP:
			return ButtonAction.GUI_CONTROL_DROP;
		case CREATIVE:
			return ButtonAction.GUI_CREATIVE_CLICK;
		default:
			return ButtonAction.UNKNOWN;
		}
	}
	
	private ButtonAction toButtonAction(Action action, boolean sneaking) {
		switch (action) {
		case LEFT_CLICK_BLOCK:
			return sneaking ? ButtonAction.INTERACT_SHIFT_LEFT_CLICK_BLOCK : ButtonAction.INTERACT_LEFT_CLICK_BLOCK;
		case RIGHT_CLICK_BLOCK:
			return sneaking ? ButtonAction.INTERACT_SHIFT_RIGHT_CLICK_BLOCK : ButtonAction.INTERACT_RIGHT_CLICK_BLOCK;
		case LEFT_CLICK_AIR:
			return sneaking ? ButtonAction.INTERACT_SHIFT_LEFT_CLICK_AIR : ButtonAction.INTERACT_LEFT_CLICK_AIR;
		case RIGHT_CLICK_AIR:
			return sneaking ? ButtonAction.INTERACT_SHIFT_RIGHT_CLICK_AIR : ButtonAction.INTERACT_RIGHT_CLICK_AIR;
		case PHYSICAL:
			return ButtonAction.INTERACT_PHYSICAL;
		default:
			return ButtonAction.NONE;
		}
	}

}
