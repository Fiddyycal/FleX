package org.fukkit.event.player;

import org.bukkit.event.Cancellable;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.reward.Rank;

import io.flex.commons.Nullable;

import net.md_5.fungee.event.FleXHumanEntityEvent;

public class FleXPlayerMaskEvent extends FleXHumanEntityEvent implements Cancellable {
	
	private boolean cancel = false;
	
	private Rank mask;
	
	private Result result;
	
	public FleXPlayerMaskEvent(FleXHumanEntity player, @Nullable Rank mask, Result result) {
		
		super(player, false);
		
		this.mask = mask;
		
		this.result = result;
		
	}
	
	public Rank getMask() {
		return this.mask;
	}
	
	@Override
	public boolean isCancelled() {
		return this.cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
	
	public Result getResult() {
		return this.result;
	}
	
	public enum Result {
		SUCCESS, FAILURE, UNMASK;
	}
	
}
