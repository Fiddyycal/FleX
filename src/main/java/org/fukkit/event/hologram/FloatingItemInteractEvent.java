package org.fukkit.event.hologram;

import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.hologram.FloatingItem;

public class FloatingItemInteractEvent extends FloatingItemEvent {

	private FleXPlayer player;
	
	private ButtonAction action;
	
	public FloatingItemInteractEvent(FloatingItem item, FleXPlayer player, ButtonAction action) {
		
		super(item);
		
		this.player = player;
		
		this.action = action;
		
	}
	
	public FleXPlayer getPlayer() {
		return this.player;
	}
	
	public ButtonAction getAction() {
		return  this.action;
	}

}
