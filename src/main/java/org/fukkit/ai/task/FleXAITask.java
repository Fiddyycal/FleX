package org.fukkit.ai.task;

public interface FleXAITask extends FleXAITaskBuilder {
	public void performTask();
	public void onComplete();
}
