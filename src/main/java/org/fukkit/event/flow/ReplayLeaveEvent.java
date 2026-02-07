package org.fukkit.event.flow;

import org.fukkit.entity.FleXPlayer;
import org.fukkit.recording.Replay;

public class ReplayLeaveEvent extends ReplayPlayerEvent {

	public ReplayLeaveEvent(Replay replay, FleXPlayer player) {
		super(replay, player, false);
	}

	@Override
	@Deprecated
	public void setCancelled(boolean cancel) {
		throw new UnsupportedOperationException("cannot cancel ReplayLeaveEvent");
	}

}
