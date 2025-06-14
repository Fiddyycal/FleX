package org.fukkit.event;

import java.lang.reflect.Method;
import java.util.function.Consumer;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredListener;
import org.fukkit.Fukkit;

public abstract class FleXQuickEventListener<T extends Event> implements Listener {
	
	private Class<T> clazz;
	
	public static <T extends Event> FleXQuickEventListener<T> listen(Class<T> clazz, Consumer<T> consumer) {
		return listen(clazz, EventPriority.NORMAL, false, consumer);
	}
	
	public static <T extends Event> FleXQuickEventListener<T> listen(Class<T> clazz, EventPriority priority, Consumer<T> consumer) {
		return listen(clazz, priority, false, consumer);
	}
	
	public static <T extends Event> FleXQuickEventListener<T> listen(Class<T> clazz, EventPriority priority, boolean ignoreCancelled, Consumer<T> consumer) {
		return new FleXQuickEventListener<T>(clazz) {
			
			@Override
			@EventHandler(priority = EventPriority.NORMAL/**can't use @param priority here maven doesn't like it.*/)
			public void event(T event) {
				consumer.accept(event);
			}
			
		};
	}
	
	/**
	 * Keep this here for reference sake.
	 */
	@Deprecated
	private FleXQuickEventListener(Class<T> clazz) {
	
		this.clazz = clazz;
		
		EventHandler ann = null;
		
		try {
			
			Method event = this.getClass().getMethod("on", Event.class);
			ann = event.getAnnotation(EventHandler.class);
			
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
		EventPriority priority = ann != null ? ann.priority() : EventPriority.NORMAL;
		boolean ignoreCancelled = ann != null ? ann.ignoreCancelled() : false;
		
		RegisteredListener listener = new RegisteredListener(this, (l, e) -> on(e), priority, Fukkit.getInstance(), ignoreCancelled);
		
        for (HandlerList handler : HandlerList.getHandlerLists())
        	handler.register(listener);
        
	}

	@Deprecated
    @SuppressWarnings("unchecked")
	public void on(Event event) {
    	
    	if (!event.getClass().isAssignableFrom(this.clazz))
    		return;
    	
    	this.event((T) event);
    	
    }
	
    public abstract void event(T event);

}
