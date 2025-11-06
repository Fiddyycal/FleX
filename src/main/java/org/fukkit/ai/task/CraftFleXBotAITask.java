package org.fukkit.ai.task;

import org.fukkit.ai.FleXBotAI;

public abstract class CraftFleXBotAITask implements FleXAITask {

	private FleXBotAI ai;
	private FleXAITask next;
	
	public CraftFleXBotAITask(FleXBotAI ai) {
		this.ai = ai;
	}
	
	@Override
	public FleXAITaskBuilder then(FleXAITask task) {
		this.next = task;
		return this.next;
	}
	
	@Override
	public void onComplete() {
		this.ai.setTask(this.next);
	}

}
