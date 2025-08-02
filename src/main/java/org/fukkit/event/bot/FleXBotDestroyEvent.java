package org.fukkit.event.bot;

import org.fukkit.entity.FleXBot;

public class FleXBotDestroyEvent extends FleXBotEvent {
	
	public FleXBotDestroyEvent(FleXBot bot) {
		super(bot, false);
	}

}
