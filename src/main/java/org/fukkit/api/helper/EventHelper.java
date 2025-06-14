package org.fukkit.api.helper;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.fukkit.Fukkit;

public class EventHelper {
	
	public static <T extends Event> void unregister(Listener listener, Class<T> event) {
		Fukkit.getEventFactory().unregister(listener, event);
	}
	
	public static void register(Listener... listeners) {
		Fukkit.getEventFactory().register(listeners);
	}
	
	public static void callEvent(Event event) {
		Fukkit.getEventFactory().call(event);
	}

}
