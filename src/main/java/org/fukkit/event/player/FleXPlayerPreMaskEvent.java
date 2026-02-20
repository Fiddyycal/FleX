package org.fukkit.event.player;

import org.bukkit.event.Cancellable;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.reward.Rank;

import io.flex.commons.Nullable;

public class FleXPlayerPreMaskEvent extends FleXPlayerMaskEvent implements Cancellable {
	
	private boolean cancel = false;
	
	public FleXPlayerPreMaskEvent(FleXHumanEntity player, @Nullable Rank mask, Result result) {
		super(player, mask, result);
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
