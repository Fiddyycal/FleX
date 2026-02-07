package org.fukkit.event.flow;

import org.fukkit.recording.Replay;

public class ReplayEndEvent extends ReplayEvent {
	
	private String reason;
	
	public ReplayEndEvent(Replay replay, String reason) {
		
		super(replay, false);
		
		this.reason = reason;
		
	}
	
	public String getReason() {
		return this.reason;
	}
	
}
