package org.fukkit.event.player;

import org.fukkit.entity.FleXPlayer;

import net.md_5.fungee.event.FleXPlayerEvent;

public class FleXPlayerLoginEvent extends FleXPlayerEvent {
	
	public FleXPlayerLoginEvent(FleXPlayer player) {
		super(player, false);
	}
	
}
