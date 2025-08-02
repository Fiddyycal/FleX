package org.fukkit.event.player;

import org.bukkit.event.Cancellable;
import org.fukkit.entity.FleXPlayer;

@Deprecated
public class FleXPlayerUpdateEvent extends FleXPlayerEvent implements Cancellable {
	
	private boolean cancel = false, update = false;

	public FleXPlayerUpdateEvent(FleXPlayer player) {
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
	
	public void setUploadCancelled(boolean cancel) {
		this.update = cancel;
	}
	
	public boolean isUploadCancelled() {
		return this.update;
	}

}
