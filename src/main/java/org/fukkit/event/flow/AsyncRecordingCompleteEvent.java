package org.fukkit.event.flow;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.fukkit.recording.Recording;

public class AsyncRecordingCompleteEvent extends Event {

	private static HandlerList handlers = new HandlerList();
	
	private Recording recording;
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {       
		return handlers;
	}
	
	public AsyncRecordingCompleteEvent(Recording recording) {
		super(true);
		this.recording = recording;
	}
	
	public Recording getRecording() {
		return this.recording;
	}

}
