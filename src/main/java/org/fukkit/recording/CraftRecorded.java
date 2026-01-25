package org.fukkit.recording;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.disguise.FleXSkin;
import org.fukkit.entity.FleXBot;
import org.fukkit.entity.FleXPlayer;

import io.flex.commons.Nullable;

public class CraftRecorded implements Recordable {

	private UUID uuid;
	private String name;
	
	private Map<Long, Frame> frames = new LinkedHashMap<Long, Frame>();
	
	private FleXBot actor;
	
	private boolean bot = false;
	
	private CraftRecorded(UUID uuid, @Nullable String name, @Nullable FleXSkin skin, LinkedHashMap<Long, Frame> frames) {
		
		name = name != null ? name : Memory.SKIN_CACHE.getRandomName();
		skin = skin != null ? skin : Memory.SKIN_CACHE.getRandom();
		
		this.uuid = uuid;
		
		this.frames = frames;
		
		this.name = name;
		
		this.actor = Fukkit.getPlayerFactory().createFukkitBot(name, skin);
		
		this.actor.getAI().setGravity(false);
		
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
		return this.actor != null ? this.actor : Fukkit.getPlayer(this.uuid);
	}
	
	public FleXBot getActor() {
		return this.actor;
	}
	
	@Override
	public boolean isBot() {
		return this.bot;
	}
	
	public static CraftRecorded of(@Nullable FleXPlayer player, LinkedHashMap<Long, Frame> frames) {
		
		CraftRecorded rec = new CraftRecorded(player != null ? player.getUniqueId() : UUID.randomUUID(), player != null ? player.getName() : null, player != null ? player.getSkin() : null, frames);
		
		rec.bot = player == null;
		
		return rec;
		
	}
	
}
