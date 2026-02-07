package org.fukkit.event.flow;

import org.fukkit.recording.Replay;

public class ReplayStartEvent extends ReplayEvent {
	
	public ReplayStartEvent(Replay replay) {
		super(replay, false);
	}
	
}
