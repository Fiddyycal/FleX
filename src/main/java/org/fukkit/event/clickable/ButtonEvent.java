package org.fukkit.event.clickable;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.fukkit.clickable.button.Button;

public class ButtonEvent extends Event {
	
	protected Button button;
	
	private static HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {       
		return handlers;
	}
	
	public ButtonEvent(Button button, boolean async) {
		
		super(async);
		
		this.button = button;
		
	}
	
	public Button getButton() {
		return this.button;
	}

}
