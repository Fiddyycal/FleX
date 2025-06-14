package org.fukkit.task;

import org.bukkit.scheduler.BukkitRunnable;
import org.fukkit.Fukkit;
import org.fukkit.utils.BukkitUtils;

public abstract class CallbackResultTask<V> extends BukkitRunnable {
	
	private boolean async;
	
	public CallbackResultTask() {
		this(false);
	}
	
	public CallbackResultTask(boolean completeAsync) {
		
		this.async = completeAsync;
		
		this.runTaskAsynchronously(Fukkit.getInstance());
		
	}
	
	public void setCompleteAsync(boolean async) {
		this.async = async;
	}
	
	@Override
	public void run() {
		
		V result = this.runAsync();
		
		if (this.async)
			this.onComplete(result);
		
		else BukkitUtils.runLater(() -> this.onComplete(result));
		
	}
	
	public abstract V runAsync();
	
	public abstract void onComplete(V result);
	
	public boolean isCompleteAsync() {
		return this.async;
	}
	
	public boolean isComplete() {
		return this.isCancelled();
	}

}
