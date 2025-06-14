package org.fukkit.task;

import java.util.function.Supplier;

import org.bukkit.scheduler.BukkitRunnable;
import org.fukkit.Fukkit;
import org.fukkit.utils.BukkitUtils;

public abstract class CallbackCheckTask extends BukkitRunnable {

	private boolean async;
	
	private Supplier<Boolean> checkFirst;
	
	public CallbackCheckTask(Supplier<Boolean> checkFirst) {
		this(checkFirst, false);
	}
	
	public CallbackCheckTask(Supplier<Boolean> checkFirst, boolean completeAsync) {
		
		this.async = completeAsync;
		
		this.checkFirst = checkFirst;
		
		this.runTaskTimerAsynchronously(Fukkit.getInstance(), 1L, 10L);
		
	}
	
	public void setCompleteAsync(boolean async) {
		this.async = async;
	}
	
	@Override
	public void run() {
		
		if (this.checkFirst == null || this.checkFirst.get()) {
			
			if (this.async)
				this.onComplete();
			
			else BukkitUtils.runLater(() -> this.onComplete());
			
			this.cancel();
			return;
			
		}
		
	}
	
	public abstract void onComplete();
	
	public boolean isCompleteAsync() {
		return this.async;
	}
	
	public boolean isComplete() {
		return this.isCancelled();
	}

}
