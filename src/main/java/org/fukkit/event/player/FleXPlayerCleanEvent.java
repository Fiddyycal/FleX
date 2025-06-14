package org.fukkit.event.player;

import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.entity.EntityCleanEvent;

@Deprecated
public class FleXPlayerCleanEvent extends EntityCleanEvent {
	
	public FleXPlayerCleanEvent(FleXPlayer player, CleanType type, boolean async) {
		super(player.getPlayer(), type);
	}
	
}
