package org.fukkit.event.flow;

import org.fukkit.entity.FleXPlayer;
import org.fukkit.recording.Replay;

public class ReplayPlayerEvent extends ReplayEvent {

	private FleXPlayer player;
	
	public ReplayPlayerEvent(Replay replay, FleXPlayer player, boolean async) {
		
		super(replay, async);
		
		this.player = player;
		
	}
	
	public FleXPlayer getPlayer() {
		return this.player;
	}

}
