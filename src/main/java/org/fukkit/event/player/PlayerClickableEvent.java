package org.fukkit.event.player;

import org.fukkit.clickable.Clickable;
import org.fukkit.clickable.button.ExecutableButton;
import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.entity.FleXPlayer;

import io.flex.commons.Nullable;

public class PlayerClickableEvent extends PlayerButtonExecuteEvent {
	
	protected Clickable clickable;
	
	public PlayerClickableEvent(FleXPlayer player, Clickable clickable, @Nullable ExecutableButton button, ButtonAction action, boolean async) {
		super(player, button, action, async);
		
		this.clickable = clickable;
		
	}
	
	public Clickable getClickable() {
		return this.clickable;
	}
	
}
