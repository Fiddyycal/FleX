package org.fukkit.event.consequence;

import org.fukkit.consequence.Kick;

public class FleXKickEvent extends FleXConvictEvent {
	
	public FleXKickEvent(Kick kick, boolean async) {
		
		super(kick, async);
		
		this.conviction = kick;
		
	}
	
	@Override
	public Kick getConviction() {
		return (Kick) this.conviction;
	}

}
