package org.fukkit.event.flow;

import org.fukkit.entity.FleXPlayer;
import org.fukkit.recording.Replay;

public class ReplayWatchEvent extends ReplayEvent {
	
	private FleXPlayer player;
	
	public ReplayWatchEvent(Replay replay, FleXPlayer player) {
		
		super(replay);
		
		this.player = player;
		
	}
	
	public FleXPlayer getPlayer() {
		return this.player;
	}
	
}
