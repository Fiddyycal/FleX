package org.fukkit.event.consequence;

import org.bukkit.event.HandlerList;
import org.fukkit.consequence.Punishment;
import org.fukkit.entity.FleXPlayer;

public class FleXConvictEvent extends FleXPreConsequenceEvent {

	private static HandlerList handlers = new HandlerList();
	
	protected Punishment conviction;
	
	public FleXConvictEvent(Punishment conviction, boolean async) {
		
		super(conviction, async);
		
		this.conviction = conviction;
		
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public FleXPlayer getBy() {
		return this.conviction.getBy();
	}
	
	public FleXPlayer getPlayer() {
		return this.conviction.getPlayer();
	}
	
	public Punishment getConviction() {
		return this.conviction;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
