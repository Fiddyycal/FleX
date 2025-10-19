package org.fukkit.event.flow;

import org.fukkit.recording.Recording;

public class AsyncRecordingStartEvent extends RecordingEvent {
	
	public AsyncRecordingStartEvent(Recording recording) {
		super(recording, true);
	}

}
