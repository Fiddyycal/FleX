package org.fukkit.event.player;

import org.bukkit.event.Cancellable;
import org.fukkit.entity.FleXPlayer;

import net.md_5.fungee.event.FleXPlayerEvent;

public class FleXPlayerPreLoginEvent extends FleXPlayerEvent implements Cancellable {
	
	private boolean cancel = false;
	
	public String message;
	
	public FleXPlayerPreLoginEvent(FleXPlayer player) {
		super(player, false);
	}
	
	public String getKickMessage() {
		return message;
	}
	
	public void setKickMessage(String message) {
		this.message = message;
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
