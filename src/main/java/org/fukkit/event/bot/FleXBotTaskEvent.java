package org.fukkit.event.bot;

import org.bukkit.event.Cancellable;
import org.fukkit.ai.task.FleXAITask;
import org.fukkit.entity.FleXBot;

public class FleXBotTaskEvent extends FleXBotEvent implements Cancellable {
	
	private boolean cancel = false;
	
	private FleXAITask task;
	
	public FleXBotTaskEvent(FleXBot bot, FleXAITask task) {
		super(bot, false);
		this.task = task;
	}
	
	public FleXAITask getTask() {
		return this.task;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
	
	@Override
	public boolean isCancelled() {
		return this.cancel;
	}

}
