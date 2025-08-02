package org.fukkit.event.player;

import org.bukkit.event.Cancellable;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.reward.Rank;

public class FleXPlayerRankChangeEvent extends FleXHumanEntityEvent implements Cancellable {

	private Rank rank;
	
	private boolean cancel = false;
	
	public FleXPlayerRankChangeEvent(FleXHumanEntity player, Rank rank, boolean async) {
		
		super(player, async);
		
		this.rank = rank;
		
	}
	
	public Rank getRank() {
		return this.rank;
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
