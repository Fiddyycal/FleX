package org.fukkit.event.player;

import org.bukkit.Bukkit;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.reward.Rank;

import io.flex.commons.Nullable;

public class FleXPlayerMaskEvent extends FleXHumanEntityEvent {
	
	private Rank mask;
	
	private Result result;
	
	public FleXPlayerMaskEvent(FleXHumanEntity player, @Nullable Rank mask, Result result) {
		
		super(player, !Bukkit.isPrimaryThread());
		
		this.mask = mask;
		
		this.result = result;
		
	}
	
	public Rank getMask() {
		return this.mask;
	}
	
	public Result getResult() {
		return this.result;
	}
	
	public enum Result {
		SUCCESS, FAILURE, UNMASK;
	}
	
}
