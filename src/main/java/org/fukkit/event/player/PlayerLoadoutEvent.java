package org.fukkit.event.player;

import org.fukkit.clickable.Loadout;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.event.clickable.ClickableEvent;

public class PlayerLoadoutEvent extends ClickableEvent {

	private FleXHumanEntity player;
	
	public PlayerLoadoutEvent(FleXHumanEntity player, Loadout loadout, boolean async) {
		
		super(loadout, async);
		
		this.player = player;
		
	}
	
	public FleXHumanEntity getPlayer() {
		return this.player;
	}
	
	@Override
	public Loadout getClickable() {
		return (Loadout) this.clickable;
	}

}
