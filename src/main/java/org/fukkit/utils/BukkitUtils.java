package org.fukkit.utils;

import java.util.function.Consumer;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
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
	
	public static BukkitTask runTimer(Consumer<BukkitTask> runnable, long period, boolean async) {
		return runTimer(runnable, 0L, period, async);
	}
	
	public static BukkitTask runTimer(Consumer<BukkitTask> runnable, long delay, long period) {
		return runTimer(runnable, delay, period, false);
	}
	
	public static BukkitTask runTimer(Consumer<BukkitTask> runnable, long delay, long period, boolean async) {
		
		BukkitTask[] ref = new BukkitTask[1];
		
		BukkitRunnable task = new BukkitRunnable() {
			
			@Override
			public void run() {
				runnable.accept(ref[0]);
			}
			
		};
		
		return ref[0] = (async ? task.runTaskTimerAsynchronously(Fukkit.getInstance(), delay, period) : task.runTaskTimer(Fukkit.getInstance(), delay, period));
		
	}

}