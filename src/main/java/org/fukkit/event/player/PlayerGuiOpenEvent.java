package org.fukkit.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.fukkit.clickable.Menu;

public class PlayerGuiOpenEvent extends PlayerGuiEvent implements Cancellable {

	private boolean cancel = false;
	
	public PlayerGuiOpenEvent(Player player, Menu gui, boolean async) {
		
		super(player, gui, async);
		
		if (this.cancel == true)
			return;
		
	}
	
	@Override
	public void setCancelled(boolean arg0) {
		this.cancel = arg0;
	}
	
	@Override
	public boolean isCancelled() {
		return this.cancel;
	}

}
