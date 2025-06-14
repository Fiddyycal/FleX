package org.fukkit.event;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;

public interface EventFactory {

	public void register(Listener... listeners);
	
	public <T extends Event> void unregister(Listener listener);
	
	public <T extends Event> void unregister(Listener listener, Class<T> event);
	
	public <T extends Event> T call(T event);
	
}
