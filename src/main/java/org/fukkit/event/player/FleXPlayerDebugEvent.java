package org.fukkit.event.player;

import org.bukkit.event.Cancellable;
import org.fukkit.entity.FleXPlayer;

public class FleXPlayerDebugEvent extends FleXPlayerEvent implements Cancellable {

	private boolean cancel = false;
	
	public FleXPlayerDebugEvent(FleXPlayer player) {
		super(player, false);
	}

	@Override
	public boolean isCancelled() {
		return this.cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

}
