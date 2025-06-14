package org.fukkit.event.player;

import org.bukkit.entity.Player;
import org.fukkit.clickable.Menu;
import org.fukkit.event.clickable.ClickableEvent;

public class PlayerGuiEvent extends ClickableEvent {

	private Player player;
	
	public PlayerGuiEvent(Player player, Menu gui, boolean async) {
		
		super(gui, async);
		
		this.player = player;
		
	}

	public Player getPlayer() {
		return this.player;
	}
	
	@Override
	public Menu getClickable() {
		return (Menu) this.clickable;
	}

}
