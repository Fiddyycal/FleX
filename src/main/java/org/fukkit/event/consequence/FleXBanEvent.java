package org.fukkit.event.consequence;

import org.fukkit.consequence.Ban;

public class FleXBanEvent extends FleXConvictEvent {
	
	public FleXBanEvent(Ban ban, boolean async) {
		
		super(ban, async);
		
		this.conviction = ban;
		
	}
	
	@Override
	public Ban getConviction() {
		return (Ban) this.conviction;
	}

}
