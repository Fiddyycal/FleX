package org.fukkit.event.bot;

import org.fukkit.entity.FleXBot;
import org.fukkit.event.player.FleXPlayerEvent;

public class FleXBotEvent extends FleXPlayerEvent {

	public FleXBotEvent(FleXBot bot, boolean async) {
		super(bot, async);
	}

	public FleXBot getBot() {
		return (FleXBot) super.getPlayer();
	}
	
}
