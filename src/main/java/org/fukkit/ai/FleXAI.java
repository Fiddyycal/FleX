package org.fukkit.ai;

import org.fukkit.ai.task.FleXAITask;

import io.flex.commons.Nullable;

public interface FleXAI extends Runnable {
	
	public FleXAITask getTask();
	
	public void setTask(@Nullable FleXAITask task);
	
}
