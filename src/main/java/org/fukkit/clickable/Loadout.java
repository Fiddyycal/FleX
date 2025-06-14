package org.fukkit.clickable;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.fukkit.Fukkit;
import org.fukkit.clickable.button.Button;
import org.fukkit.clickable.button.PointlessButton;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.utils.ItemUtils;
import org.fukkit.utils.VersionUtils;

import io.flex.commons.Nullable;

@SuppressWarnings("deprecation")
public class Loadout extends Clickable {
	
	private Button helmet;
	private Button chestplate;
	private Button leggings;
	private Button boots;
	
	public Loadout(@Nullable FleXHumanEntity... viewers) {
		super(Fukkit.getInstance().getServer().createInventory(null, 36, UUID.randomUUID().toString()), viewers);
	}
	
	public Button getHelmet() {
		return this.helmet;
	}
	
	public Button getChestplate() {
		return this.chestplate;
	}
	
	public Button getLeggings() {
		return this.leggings;
	}
	
	public Button getBoots() {
		return this.boots;
	}
	
	public void setHelmet(Button helmet) {
		this.helmet = helmet;
	}
	
	public void setChestplate(Button chestplate) {
		this.chestplate = chestplate;
	}
	
	public void setLeggings(Button leggings) {
		this.leggings = leggings;
	}
	
	public void setBoots(Button boots) {
		this.boots = boots;
	}
	
	public boolean isBeingUsed() {
		return Fukkit.getServerHandler().getOnlinePlayersUnsafe().stream().anyMatch(p -> p.getLoadout() == this);
	}
	
	public Menu asMenu() {
		return this.asMenu(null);
	}
	
	public Menu asMenu(@Nullable String name) {
		return this.asMenu(name, this.getInventory().getSize() / 9);
	}
	
	public Menu asMenu(@Nullable String name, int rows) {
		
		Menu menu = new Menu(name, rows, this.getViewers().toArray(new FleXPlayer[this.getViewers().size()]));
		
		this.getButtons().entrySet().forEach(e -> {
			menu.setButton(e.getKey(), e.getValue());
		});
		
		return menu;
		
	}
	
	public Menu asPreview() {
		return this.asPreview(null);
	}
	
	public Menu asPreview(@Nullable String name) {
		
		Menu menu = new Menu(name, 5, this.getViewers().toArray(new FleXPlayer[this.getViewers().size()]));
		
		this.getButtons().entrySet().forEach(e -> {
			
			ItemStack item = e.getValue().asItemStack();
			
			Material type = item.getType();
			
			int amount = item.getAmount();
			
			short damage = item.getDurability();
			
			Map<Enchantment, Integer> enchants = item.getEnchantments();
			
			ItemMeta meta = item.getItemMeta();
			
			String[] lore = meta != null && meta.hasLore() ? meta.getLore().toArray(new String[meta.getLore().size()]) : null;
			
			PointlessButton button = new PointlessButton(type, meta != null && meta.hasDisplayName() ? meta.getDisplayName() : "", amount, damage, enchants, lore);
			
			menu.setButton(e.getKey() >= 0 && e.getKey() <= 8 ? e.getKey() + 36 : e.getKey() - 9, button);
			
		});
		
		Material material = VersionUtils.material("STAINED_GLASS_PANE", "WHITE_STAINED_GLASS_PANE");
		
		for (int i = 27; i < 36; i++)
			menu.setButton(i, new PointlessButton(material, null, 1, (short) 15));
		
		return menu;
		
	}
	
	@Override
	public String toString() {
		return ItemUtils.itemStackArrayToBase64(this.getInventory().getContents());
	}

}
