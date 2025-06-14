package org.fukkit;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.scheduler.BukkitRunnable;
import org.fukkit.entity.FleXPlayer;

import io.flex.FleX.Task;

public abstract class FukkitRunnable implements Runnable {
	
	private static final Set<FukkitRunnable> runnables = new HashSet<FukkitRunnable>();
	
	private static BukkitRunnable tasks;
	
	private BukkitRunnable delegate;
	
	public FukkitRunnable() {
		
		if (Task.isDebugEnabled() && tasks == null) {
			tasks = new BukkitRunnable() {
				
			    @Override
			    public void run() {
			    	
			    	Task.debug("Task Manager", "Loading tasks...");
		    		
		    		int o = 0;
		    		
		    		for (FleXPlayer player : Fukkit.getServerHandler().getOnlinePlayersUnsafe())
						o = o + player.getPlayer().getScoreboard().getObjectives().size();
		    		
		    		int t = 0;
		    		
		    		for (FleXPlayer player : Fukkit.getServerHandler().getOnlinePlayersUnsafe())
						t = t + player.getPlayer().getScoreboard().getTeams().size();
		    		
		    		Task.debug("Task Manager", "====================================");
		    		Task.debug("Task Manager",
		    				
		    				"Tasks: " + runnables.size(),
		    				"Teams: " + t,
		    				"Objectives: " + o,
		    				"Loadouts: " + Fukkit.getServerHandler().getOnlinePlayersUnsafe().stream().filter(p -> p.getLoadout() != null).count(),
		    		        "Channels: " + net.md_5.fungee.Memory.CHANNEL_CACHE.size(),
		    		        "Commands: " + Memory.COMMAND_CACHE.size(),
		    		        "Buttons: " + Memory.BUTTON_CACHE.size(),
		    		        "Themes: " + Memory.THEME_CACHE.size(),
		    		        "Skins: " + Memory.SKIN_CACHE.size(),
		    		        "Badges: " + Memory.BADGE_CACHE.size(),
		    		        "Ranks: " + Memory.RANK_CACHE.size(),
		    		        "Menus: " + Memory.GUI_CACHE.size());
		    		
		    		Task.debug("Task Manager", "====================================");
				    
			    }
			
		    };
			
		    tasks.runTaskTimer(Fukkit.getInstance(), 0L, 1200L);
		
	    }
		
		this.init();
		
	}
	
	private BukkitRunnable init() {
		
		this.cancel();
		
		if (this.delegate == null)
			this.delegate = new BukkitRunnable() {
			
			    @Override
			    public void run() {
			    	FukkitRunnable.this.run();
			    }
			
		    };
		    
		return this.delegate;
		
	}
	
	public FukkitRunnable runTask() throws IllegalArgumentException, IllegalStateException {
		return this.runLater(0);
	}
	
	public FukkitRunnable runTimer(long period) throws IllegalArgumentException, IllegalStateException {
		return this.runTimer(0, period);
	}
	
	public FukkitRunnable runLater(long delay) throws IllegalArgumentException, IllegalStateException {
		
		this.init().runTaskLater(Fukkit.getInstance(), delay < 0 ? 0 : delay);
		
		runnables.add(this);
		
		return this;
		
	}
	
	public FukkitRunnable runTimer(long delay, long period) throws IllegalArgumentException, IllegalStateException {
		
		this.init().runTaskTimer(Fukkit.getInstance(), delay < 0 ? 0 : delay, period);
		
		runnables.add(this);
		
		return this;
		
	}
	
	public void cancel() throws IllegalStateException {
		
		if (runnables.contains(this)) {
			
			this.delegate.cancel();
			this.delegate = null;
			
		}
		
		runnables.remove(this);
		
	}
	
	public boolean isCancelled() {
		return !runnables.contains(this);
	}
	
}
			    	