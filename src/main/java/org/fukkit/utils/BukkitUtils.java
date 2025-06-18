package org.fukkit.utils;

import org.bukkit.scheduler.BukkitRunnable;
import org.fukkit.Fukkit;

public class BukkitUtils {
	
	public static BukkitRunnable mainThread(Runnable runnable) {
		return runLater(runnable, false);
	}
	
	public static BukkitRunnable asyncThread(Runnable runnable) {
		return runLater(runnable, -1, true);
	}
	
	public static BukkitRunnable runLater(Runnable runnable) {
		return runLater(runnable, false);
	}
	
	public static BukkitRunnable runLater(Runnable runnable, long delay) {
		return runLater(runnable, delay, false);
	}
	
	public static BukkitRunnable runLater(Runnable runnable, boolean async) {
		return runLater(runnable, -1, false);
	}
	
	public static BukkitRunnable runLater(Runnable runnable, long delay, boolean async) {
		
		BukkitRunnable task = new BukkitRunnable() {
			
			@Override
			public void run() {
				runnable.run();
			}
			
		};
		
		if (async) {
			
			if (delay <= 0)
				task.runTaskAsynchronously(Fukkit.getInstance());
			
			else task.runTaskLaterAsynchronously(Fukkit.getInstance(), delay);
			
		}
		
		else {
			
			if (delay <= 0)
				task.runTask(Fukkit.getInstance());
			
			else task.runTaskLater(Fukkit.getInstance(), delay);
			
		}
		
		return task;
		
	}
	
	public static BukkitRunnable runTimer(Runnable runnable, long delay, long period) {
		return runTimer(runnable, delay, period, false);
	}
	
	public static BukkitRunnable runTimer(Runnable runnable, long delay, long period, boolean async) {
		if (async) {
			
			BukkitRunnable task = new BukkitRunnable() {
				
				@Override
				public void run() {
					runnable.run();
				}
				
			};
			
			task.runTaskTimerAsynchronously(
					Fukkit.getInstance(), delay, period);
			
			return task;
			
		} else {

			BukkitRunnable task = new BukkitRunnable() {
				
				@Override
				public void run() {
					runnable.run();
				}
				
			};
			
			task.runTaskTimer(
					Fukkit.getInstance(), delay, period);
			
			return task;
			
		}
	}

}