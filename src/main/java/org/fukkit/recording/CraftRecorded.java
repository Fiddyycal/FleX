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
	
	private Map<Long, Frame> frames = new LinkedHashMap<Long, Frame>();
	
	private FleXBot bot;

	private CraftRecorded(UUID uuid, @Nullable String name, @Nullable FleXSkin skin, LinkedHashMap<Long, Frame> frames) {
		
		this.uuid = uuid;
		
		this.frames = frames;
		
		name = name != null ? name : Memory.NAME_CACHE.getRandom();
		skin = skin != null ? skin : Memory.SKIN_CACHE.getRandom();
		
		this.bot = Fukkit.getPlayerFactory().createFukkitBot(name, skin);
		
		this.bot.getAI().setGravity(false);
		
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
		return Fukkit.getPlayer(this.uuid);
	}
	
	public FleXBot getActor() {
		return this.bot;
	}
	
	public static CraftRecorded of(@Nullable FleXPlayer player, LinkedHashMap<Long, Frame> frames) {
		return new CraftRecorded(player != null ? player.getUniqueId() : UUID.randomUUID(), player != null ? player.getName() : null, player != null ? player.getSkin() : null, frames);
	}
	
}
