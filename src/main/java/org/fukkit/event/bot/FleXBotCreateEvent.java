package org.fukkit.event.bot;

import org.fukkit.entity.FleXBot;

public class FleXBotCreateEvent extends FleXBotEvent {
	
	public FleXBotCreateEvent(FleXBot bot) {
		super(bot, false);
	}

}
