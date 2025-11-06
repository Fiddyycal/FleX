package org.fukkit.event.bot;

import org.fukkit.ai.task.FleXAITask;
import org.fukkit.entity.FleXBot;

public class FleXBotUpdateTaskEvent extends FleXBotTaskEvent {
	
	public FleXBotUpdateTaskEvent(FleXBot bot, FleXAITask task) {
		super(bot, task);
	}

}
