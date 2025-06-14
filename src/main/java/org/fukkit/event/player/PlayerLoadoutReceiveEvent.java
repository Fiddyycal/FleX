package org.fukkit.event.player;

import org.bukkit.event.Cancellable;
import org.fukkit.clickable.Loadout;
import org.fukkit.entity.FleXHumanEntity;

public class PlayerLoadoutReceiveEvent extends PlayerLoadoutEvent implements Cancellable {

	private boolean cancel = false;
	
	public PlayerLoadoutReceiveEvent(FleXHumanEntity player, Loadout loadout, boolean async) {
		
		super(player, loadout, async);
		
		if (this.cancel == true)
			return;
		
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
