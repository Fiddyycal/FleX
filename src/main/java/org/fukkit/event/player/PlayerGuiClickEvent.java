package org.fukkit.event.player;

import org.fukkit.clickable.Menu;
import org.fukkit.clickable.button.ExecutableButton;
import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.entity.FleXPlayer;

import io.flex.commons.Nullable;

public class PlayerGuiClickEvent extends PlayerClickableEvent {
	
	public PlayerGuiClickEvent(FleXPlayer player, Menu gui, @Nullable ExecutableButton button, ButtonAction action, boolean async) {
		super(player, gui, button, action, async);
	}
	
	public Menu getClickable() {
		return (Menu) this.clickable;
	}
	
}
