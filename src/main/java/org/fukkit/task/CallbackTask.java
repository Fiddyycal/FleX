package org.fukkit.task;

import org.bukkit.scheduler.BukkitRunnable;
import org.fukkit.Fukkit;
import org.fukkit.utils.BukkitUtils;

public abstract class CallbackTask extends BukkitRunnable {
	
	private boolean async;
	
	public CallbackTask() {
		this(false);
	}
	
	public CallbackTask(boolean completeAsync) {
		
		this.async = completeAsync;
		
		this.runTaskAsynchronously(Fukkit.getInstance());
		
	}
	
	public void setCompleteAsync(boolean async) {
		this.async = async;
	}
	
	@Override
	public void run() {
		
		this.runAsync();
		
		if (this.async)
			this.onComplete();
		
		else BukkitUtils.runLater(() -> this.onComplete());
		
	}
	
	public abstract void runAsync();
	
	public abstract void onComplete();
	
	public boolean isCompleteAsync() {
		return this.async;
	}
	
	public boolean isComplete() {
		return this.isCancelled();
	}

}
