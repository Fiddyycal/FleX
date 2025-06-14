package org.fukkit.event.player;

import org.fukkit.clickable.button.ExecutableButton;

import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.clickable.ButtonExecuteEvent;

import io.flex.commons.Nullable;

public class PlayerButtonExecuteEvent extends ButtonExecuteEvent {
	
	private FleXPlayer player;
	
	private ButtonAction action;
	
	private boolean executed = false;
	
	public PlayerButtonExecuteEvent(FleXPlayer player, @Nullable ExecutableButton button, ButtonAction action, boolean async) {
		
		super(button, async);
		
		this.player = player;
		this.action = action;
		
	}
	
	public ButtonAction getAction() {
		return this.action;
	}
	
	public FleXPlayer getPlayer() {
		return this.player;
	}
	
	public void setExecuted(boolean executed) {
		this.executed = executed;
	}
	
	public boolean isExecuted() {
		return this.executed;
	}

}
