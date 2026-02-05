package org.fukkit.craftfukkit;

import java.awt.image.BufferedImage;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.fukkit.disguise.FleXSkin;
import org.fukkit.disguise.FleXSkinType;
import org.fukkit.disguise.FleXSkin.SkinSource;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.item.Item;
import org.fukkit.item.ItemTagStore;
import org.fukkit.recording.Recordable;
import org.fukkit.scoreboard.playerlist.tab.FleXImageSkin;

public interface Implementation {
	
	public Recordable createRecordable(FleXPlayer player);
	
	public FleXImageSkin createImageSkin(BufferedImage image);

	public FleXImageSkin createImageSkin(FleXPlayer player);
	
	public FleXSkin createSkin(String name, String value, String signature, SkinSource source);
	
	public FleXSkin getSystemSkin(FleXSkinType type);
	
	public UUID getItemStackUniqueId(ItemStack item);
	
	public ItemTagStore getItemTagStore(Item item);
	
	public ItemMeta setItemUnbreakable(ItemMeta meta, boolean unbreakable);
	
	public boolean isItemUnbreakable(ItemMeta meta);
	
	public void hideEntity(Player player, Entity hide);

	public void showEntity(Player player, Entity show);
	
	public void dumbify(LivingEntity entity, boolean invincible);
	
}
