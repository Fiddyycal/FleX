package org.fukkit.event.flow;

import org.fukkit.recording.Replay;

public class ReplayEvent extends RecordingEvent {
	
	private static Replay confirm(Replay recording) {
		
		if (recording instanceof Replay)
			return recording;
		
		throw new ClassCastException("recording must be instance of Replay");
		
	}
	
	public ReplayEvent(Replay recording) {
		super(confirm(recording), false);
	}
	
	@Override
	public Replay getRecording() {
		return (Replay) this.getRecording();
	}

}
