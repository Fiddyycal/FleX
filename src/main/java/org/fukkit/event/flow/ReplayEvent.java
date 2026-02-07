package org.fukkit.event.flow;

import org.fukkit.recording.Replay;

public class ReplayEvent extends RecordingEvent {
	
	private static Replay confirm(Replay replay) {
		
		if (replay instanceof Replay)
			return replay;
		
		throw new ClassCastException("recording must be instance of Replay");
		
	}
	
	public ReplayEvent(Replay replay, boolean async) {
		super(confirm(replay), async);
	}
	
	@Override
	public Replay getRecording() {
		return (Replay) super.getRecording();
	}

}
