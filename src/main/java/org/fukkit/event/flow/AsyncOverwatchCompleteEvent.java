package org.fukkit.event.flow;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.fukkit.flow.Overwatch;

public class AsyncOverwatchCompleteEvent extends Event {

	private static HandlerList handlers = new HandlerList();
	
	private Overwatch recording;
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {       
		return handlers;
	}
	
	public AsyncOverwatchCompleteEvent(Overwatch recording) {
		this.recording = recording;
	}
	
	public Overwatch getRecording() {
		return this.recording;
	}

}
