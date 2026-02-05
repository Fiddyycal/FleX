package org.fukkit.recording;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;
import org.fukkit.Fukkit;
import org.fukkit.entity.FleXBot;
import org.fukkit.entity.FleXPlayer;

public class CraftRecordable implements Recordable {

	private UUID uuid;
	
	private String name;
	
	private Map<Long, Frame> frames = new LinkedHashMap<Long, Frame>();
	
	private boolean bot = false;

	private CraftRecordable(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public UUID getUniqueId() {
		return this.uuid;
	}

	@Override
	public Map<Long, Frame> getFrames() {
		return this.frames;
	}
	
	@Override
	public FleXPlayer toPlayer() {
		return Fukkit.getCachedPlayer(this.uuid);
	}
	
	public static CraftRecordable of(FleXPlayer player) {
		
		CraftRecordable rec = new CraftRecordable(player.getUniqueId(), player.getName());
		
		rec.bot = player instanceof FleXBot;
		
		return rec;
		
	}

	@Override
	public boolean isBot() {
		return this.bot;
	}
	
	private ItemStack cahcedHelmet = null, cahcedChestplate = null, cahcedLeggings = null, cahcedBoots = null;

	@Override
	public ItemStack getHelmet() {
		return this.cahcedHelmet;
	}

	@Override
	public ItemStack getChestplate() {
		return this.cahcedChestplate;
	}

	@Override
	public ItemStack getLeggings() {
		return this.cahcedLeggings;
	}

	@Override
	public ItemStack getBoots() {
		return this.cahcedBoots;
	}

	@Override
	public void setHelmet(ItemStack helmet) {
		this.cahcedHelmet = helmet;
	}

	@Override
	public void setChestplate(ItemStack chestplate) {
		this.cahcedChestplate = chestplate;
	}

	@Override
	public void setLeggings(ItemStack leggings) {
		this.cahcedLeggings = leggings;
	}

	@Override
	public void setBoots(ItemStack boots) {
		this.cahcedBoots = boots;
	}
	
}
