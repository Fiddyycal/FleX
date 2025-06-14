package org.fukkit.entity;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.fukkit.ai.BotBehaviour;
import org.fukkit.ai.FleXAI;
import org.fukkit.disguise.FleXSkin;
import org.fukkit.recording.RecordedAction;

public interface FleXBot extends FleXPlayer {

	public UUID getUniqueId();
	
	public String getName();
	
	public FleXSkin getSkin();
	
	public Location getLocation();
	
	public FleXAI getAI();
	
	public Inventory getInventory();
	
	public long getLastDamageTime();
	
	public BotBehaviour getBehaviour();
	
	public void setName(String name);
	
	public void setSkin(FleXSkin skin);
	
	public void setLastDamage(EntityDamageEvent event);

	public void setBehaviour(BotBehaviour behaviour);
	
	public boolean teleport(Location location, TeleportCause cause);
	
	public boolean teleport(Location location);
	
	public void equip(EquipmentSlot slot, ItemStack item);
	
	public void playAnimation(RecordedAction action);
	
	public void delete();
	
}
