package org.fukkit.recording;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.fukkit.Fukkit;
import org.fukkit.entity.FleXPlayer;

public class CraftRecordable implements Recordable {

	private UUID uuid;
	
	private List<Frame> frames;

	public CraftRecordable(UUID uuid) {
		
		this.uuid = uuid;
		
		this.frames = new LinkedList<Frame>();
		
	}

	@Override
	public UUID getUniqueId() {
		return this.uuid;
	}

	@Override
	public List<Frame> getFrames() {
		return this.frames;
	}
	
	@Override
	public FleXPlayer toPlayer() {
		return Fukkit.getPlayer(this.uuid);
	}
	
	public static CraftRecordable of(FleXPlayer player) {
		return new CraftRecordable(player.getUniqueId());
	}
	
}
