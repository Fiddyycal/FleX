package org.fukkit;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public abstract class FukkitRunnable extends BukkitRunnable {
	
	private static final Set<FukkitRunnable> runnables = new HashSet<FukkitRunnable>();
	
	public static Set<FukkitRunnable> getTasks() {
		return runnables;
	}
	
	private boolean later = false;
	
	@Override
	public synchronized void cancel() throws IllegalStateException {
		
		super.cancel();
		
		runnables.remove(this);
		
	}
	
	/**
	 * @deprecated Please use {@link #execute()} insead.
	 */
	@Override
	@Deprecated
	public void run() {
		
		this.execute();
		
		if (this.later)
			runnables.remove(this);
		
	}
	
	public abstract void execute();
	
	public synchronized BukkitTask runTask() throws IllegalArgumentException, IllegalStateException {
		return this.runTask(Fukkit.getInstance());
	}

	public synchronized BukkitTask runTaskAsynchronously() throws IllegalArgumentException, IllegalStateException {
		return this.runTaskAsynchronously(Fukkit.getInstance());
	}
	
	public synchronized BukkitTask runTaskLater(long delay) throws IllegalArgumentException, IllegalStateException {
		return this.runTaskLater(Fukkit.getInstance(), delay);
	}
	
	public synchronized BukkitTask runTaskLaterAsynchronously(long delay) throws IllegalArgumentException, IllegalStateException {
		return this.runTaskLaterAsynchronously(Fukkit.getInstance(), delay);
	}
	
	public synchronized BukkitTask runTaskTimer(long delay, long period) throws IllegalArgumentException, IllegalStateException {
		
		runnables.add(this);
		
		return this.runTaskTimer(Fukkit.getInstance(), delay, period);
		
	}
	
	public synchronized BukkitTask runTaskTimerAsynchronously(long delay, long period) throws IllegalArgumentException, IllegalStateException {
		
		runnables.add(this);
		
		return this.runTaskTimerAsynchronously(Fukkit.getInstance(), delay, period);
		
	}
	
	@Override
	public synchronized BukkitTask runTask(Plugin plugin) throws IllegalArgumentException, IllegalStateException {
		
		this.later = true;
		
		return super.runTask(plugin);
		
	}
	
	@Override
	public synchronized BukkitTask runTaskAsynchronously(Plugin plugin) throws IllegalArgumentException, IllegalStateException {
		
		this.later = true;
		
		return super.runTaskAsynchronously(plugin);
		
	}
	
	@Override
	public synchronized BukkitTask runTaskLater(Plugin plugin, long delay) throws IllegalArgumentException, IllegalStateException {
		
		this.later = true;
		
		return super.runTaskLater(plugin, delay);
		
	}
	
	@Override
	public synchronized BukkitTask runTaskLaterAsynchronously(Plugin plugin, long delay) throws IllegalArgumentException, IllegalStateException {
		
		this.later = true;
		
		return super.runTaskLaterAsynchronously(plugin, delay);
	}
	
	@Override
	public synchronized BukkitTask runTaskTimer(Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
		return super.runTaskTimer(plugin, delay, period);
	}
	
	@Override
	public synchronized BukkitTask runTaskTimerAsynchronously(Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
		return super.runTaskTimerAsynchronously(plugin, delay, period);
	}
	
}
			    	