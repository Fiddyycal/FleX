package org.fukkit.event.player;

import org.fukkit.clickable.Loadout;
import org.fukkit.clickable.button.ExecutableButton;
import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.entity.FleXPlayer;

public class PlayerLoadoutClickEvent extends PlayerClickableEvent {
	
	public PlayerLoadoutClickEvent(FleXPlayer player, Loadout loadout, ExecutableButton button, ButtonAction action, boolean async) {
		super(player, loadout, button, action, async);
	}
	
	@Override
	public Loadout getClickable() {
		return (Loadout) this.clickable;
	}

}
