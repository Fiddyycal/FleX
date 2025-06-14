package org.fukkit.event.player;

import org.fukkit.disguise.Disguise;
import org.fukkit.entity.FleXPlayer;

import io.flex.commons.Nullable;

public class FleXPlayerDisguisedEvent extends FleXPlayerDisguiseEvent {
	
	private Result result;
	
	public FleXPlayerDisguisedEvent(FleXPlayer player, @Nullable Disguise disguise, Result result) {
		
		super(player, disguise);
		
		this.result = result;
		
	}
	
	public Result getResult() {
		return this.result;
	}
	
	public enum Result {
		SUCCESS, FAILURE, UNDISGUISE;
	}
	
}
