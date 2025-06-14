package org.fukkit.event.consequence;

import org.fukkit.consequence.Mute;

public class FleXMuteEvent extends FleXConvictEvent {
	
	public FleXMuteEvent(Mute mute, boolean async) {
		
		super(mute, async);
		
		this.conviction = mute;
		
	}
	
	@Override
	public Mute getConviction() {
		return (Mute) this.conviction;
	}

}
