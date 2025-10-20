package org.fukkit.recording;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.fukkit.Fukkit;
import org.fukkit.entity.FleXPlayer;

public class CraftRecordable implements Recordable {

	private UUID uuid;
	
	private String name;
	
	private Map<Long, Frame> frames = new LinkedHashMap<Long, Frame>() ;

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
		return Fukkit.getPlayer(this.uuid);
	}
	
	public static CraftRecordable of(FleXPlayer player) {
		return new CraftRecordable(player.getUniqueId(), player.getName());
	}
	
}
