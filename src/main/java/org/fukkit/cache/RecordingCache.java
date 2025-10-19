package org.fukkit.cache;

import org.fukkit.entity.FleXPlayer;
import org.fukkit.recording.Recording;
import org.fukkit.recording.Replay;

import io.flex.commons.cache.LinkedCache;

public class RecordingCache extends LinkedCache<Recording, String> {
	
	private static final long serialVersionUID = 664033333000112820L;
	
	public RecordingCache() {
		super((recording, uid) -> recording.getUniqueId().equalsIgnoreCase(uid));
	}
	
	public Replay getByWatcher(FleXPlayer player) {
		return this.stream().filter(r -> r instanceof Replay).map(Replay.class::cast).filter(r -> r.getWatchersUnsafe().stream().anyMatch(p -> p.getUniqueId().equals(player.getUniqueId()))).findFirst().orElse(null);
	}
	
	public Recording getByPlayer(FleXPlayer player) {
		return this.stream().filter(r -> r instanceof Replay == false).filter(r -> r.isRecording(player)).findFirst().orElse(null);
	}
	
	@Override
	public boolean load() {
		// TODO local cache so reports don't have to load recordings from database every time.
		return true;
	}
	
}
