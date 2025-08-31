package org.fukkit.event.player;

import org.fukkit.entity.FleXPlayer;

public class FleXPlayerUpdateEvent extends FleXPlayerEvent {
	
	public FleXPlayerUpdateEvent(FleXPlayer player) {
		super(player, false);
	}

}
