package org.fukkit.utils;

import org.fukkit.FukkitRunnable;

public class FukkitUtils {
	
	public static FukkitRunnable runLater(Runnable runnable) {
		return runLater(runnable, 0L);
	}
	
	public static FukkitRunnable runLater(Runnable runnable, long delay) {
		
		return new FukkitRunnable() {
			
			@Override
			public void run() {
				runnable.run();
			}
			
		}.runLater(delay);
		
	}
	
	public static FukkitRunnable runTimer(Runnable runnable, long period) {
		return runTimer(runnable, 0L, period);
	}
	
	public static FukkitRunnable runTimer(Runnable runnable, long delay, long period) {
		
		return new FukkitRunnable() {
			
			@Override
			public void run() {
				runnable.run();
			}
			
		}.runTimer(delay, period);
		
	}

}

