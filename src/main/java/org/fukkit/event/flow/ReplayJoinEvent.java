package org.fukkit.event.flow;

import org.fukkit.entity.FleXPlayer;
import org.fukkit.recording.Replay;

public class ReplayJoinEvent extends ReplayPlayerEvent {

	public ReplayJoinEvent(Replay replay, FleXPlayer player) {
		super(replay, player, false);
	}

}
