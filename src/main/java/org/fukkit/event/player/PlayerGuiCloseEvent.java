package org.fukkit.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.fukkit.clickable.Menu;

public class PlayerGuiCloseEvent extends PlayerGuiEvent implements Cancellable {

	private boolean cancel = false;
	
	public PlayerGuiCloseEvent(Player player, Menu gui, boolean async) {
		super(player, gui, async);
	}
	
	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
	
	@Override
	public boolean isCancelled() {
		return this.cancel;
	}

}
