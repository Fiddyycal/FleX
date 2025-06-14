package org.fukkit.event.theme;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.fukkit.theme.Theme;

public class ThemeEvent extends Event {
	
	protected Theme theme;
	
	private static HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {       
		return handlers;
	}
	
	public ThemeEvent(Theme theme, boolean async) {
		
		super(async);
		
		this.theme = theme;
		
	}
	
	public Theme getTheme() {
		return this.theme;
	}

}
