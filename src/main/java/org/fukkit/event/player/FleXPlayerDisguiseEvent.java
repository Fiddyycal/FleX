package org.fukkit.event.player;

import org.fukkit.disguise.Disguise;
import org.fukkit.entity.FleXPlayer;

import io.flex.commons.Nullable;

public class FleXPlayerDisguiseEvent extends FleXPlayerEvent {
	
	private Disguise disguise;
	
	private Result result;
	
	public FleXPlayerDisguiseEvent(FleXPlayer player, @Nullable Disguise disguise, Result result) {
		
		super(player, false);
		
		this.disguise = disguise;
		
		this.result = result;
		
	}
	
	public Disguise getDisguise() {
		return this.disguise;
	}
	
	public Result getResult() {
		return this.result;
	}
	
	public enum Result {
		SUCCESS, FAILURE, UNDISGUISE;
	}
	
	public boolean isUnDisguise() {
		return this.result == Result.UNDISGUISE;
	}
	
}
