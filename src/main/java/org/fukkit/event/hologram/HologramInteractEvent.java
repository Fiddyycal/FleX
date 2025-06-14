package org.fukkit.event.hologram;

import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.hologram.Hologram;

public class HologramInteractEvent extends HologramEvent {

	private FleXPlayer player;
	
	private ButtonAction action;
	
	public HologramInteractEvent(Hologram hologram, FleXPlayer player, ButtonAction action) {
		
		super(hologram);
		
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
