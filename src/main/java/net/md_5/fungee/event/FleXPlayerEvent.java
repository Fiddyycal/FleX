package net.md_5.fungee.event;

import org.fukkit.entity.FleXPlayer;

public class FleXPlayerEvent extends FleXHumanEntityEvent {
	
	public FleXPlayerEvent(final FleXPlayer player, boolean async) {
		super(player, async);
	}
	
	public FleXPlayer getPlayer() {
		return (FleXPlayer) this.human;
	}

}
