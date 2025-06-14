package org.fukkit.event.player;

import org.fukkit.entity.FleXPlayer;

import net.md_5.fungee.event.FleXPlayerEvent;

public class FleXPlayerLoadEvent extends FleXPlayerEvent {
	
	public FleXPlayerLoadEvent(FleXPlayer player) {
		super(player, false);
	}
	
}
