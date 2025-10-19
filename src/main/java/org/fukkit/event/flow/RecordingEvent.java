package org.fukkit.event.flow;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.fukkit.recording.Recording;

public class RecordingEvent extends Event implements Cancellable {

	private boolean cancel = false;
	
	private static HandlerList handlers = new HandlerList();
	
	private Recording recording;
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {       
		return handlers;
	}
	
	public RecordingEvent(Recording recording, boolean async) {
		super(async);
		this.recording = recording;
	}
	
	public Recording getRecording() {
		return this.recording;
	}

	@Override
	public boolean isCancelled() {
		return this.cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

}
