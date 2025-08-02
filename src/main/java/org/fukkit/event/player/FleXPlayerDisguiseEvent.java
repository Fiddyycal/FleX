package org.fukkit.event.player;

import org.bukkit.event.Cancellable;
import org.fukkit.disguise.Disguise;
import org.fukkit.entity.FleXPlayer;

import io.flex.commons.Nullable;

public class FleXPlayerDisguiseEvent extends FleXPlayerEvent implements Cancellable {

	private boolean cancel = false;
	
	private Disguise disguise;
	
	public FleXPlayerDisguiseEvent(FleXPlayer player, @Nullable Disguise disguise) {
		
		super(player, false);
		
		this.disguise = disguise;
		
	}
	
	public Disguise getDisguise() {
		return this.disguise;
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
