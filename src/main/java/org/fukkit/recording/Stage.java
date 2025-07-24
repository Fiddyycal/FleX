package org.fukkit.recording;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.fukkit.Fukkit;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.flow.Overwatch;
import org.fukkit.utils.WorldUtils;

import io.flex.commons.Nullable;

public class Stage extends BukkitRunnable {
	
	private UUID uuid;
	
	private Set<FleXPlayer> watchers;
	
	private Recording recording;
	
	private World world;
	
	private long tick = 0;
	
	private boolean pause, anonymous;
	
	public Stage(Recording recording, boolean anonymous, @Nullable FleXPlayer... watchers) {
		
		this.uuid = recording.getUniqueId();
		
		this.recording = recording;
		
		this.anonymous = anonymous;
		
		this.watchers = new HashSet<FleXPlayer>();
		
		for (FleXPlayer watcher : watchers)
			this.watchers.add(watcher);
		
		World world = WorldUtils.copyWorld(recording.getData().getParentFile().getAbsolutePath(), Bukkit.getWorldContainer().getPath() + File.separator + "flow-" + this.uuid);
		
		this.world = world;
			
		Location tp = null;
		
		for (Recordable recordable : this.recording.getRecorded().values()) {
			for (Frame frame : recordable.getFrames()) {
				
				Location loc = frame.getLocation();
				
				loc.setWorld(world);
				
				if (tp == null) {
					
					if (recording instanceof Overwatch) {
						
						UUID suspect = ((Overwatch)recording).getUniqueId();
						
						if (recordable.getUniqueId().equals(suspect))
							tp = loc;
						
					} else {
						
						// Random player in recording
						tp = loc;
						
					}
						
				}
			}
		}
		
		if (tp == null)
			throw new UnsupportedOperationException("Could not find appropriate spawn location for watchers.");
		
		for (FleXPlayer watcher : watchers)
			watcher.teleport(tp);
		
		this.runTaskTimerAsynchronously(Fukkit.getInstance(), 10L, 2L);
			
	}
	
	public Set<FleXPlayer> getWatchers() {
		return this.watchers;
	}
	
	public Recording getRecording() {
		return this.recording;
	}
	
	public World getWorld() {
		return this.world;
	}
	
	public boolean isAnonymous() {
		return this.anonymous;
	}

	@Override
	public void run() {
		
		for (FleXPlayer watcher : this.watchers) {
			if (!watcher.isOnline() || !watcher.getPlayer().getWorld().getUID().equals(this.getWorld().getUID())) {
				this.end("Watcher has disconnected.");
				return;
			}
		}
		
		if (this.pause)
			return;
		
		if (this.tick == this.recording.getLength()) {
			
			System.out.println("Replaying stage.");
			
			this.tick = 0;
			return;
			
		}
		
		// TODO
		/*
		for (PreRecorded r : frames.entrySet())
			if (r instanceof Recorded)
				((Recorded)r).teleport(r.getFrames().get(this.tick));
			*/
	}
	
	public void end(String... reason) {
		
		if (reason == null || reason.length == 0)
			reason = new String[]{ "No further information." };
		
		System.out.println("Stopping stage: " + reason[0]);
		
		this.cancel();
		
		for (FleXPlayer watcher : this.watchers) {
			
			if (watcher.isOnline())
				watcher.kick("The stage has closed: " + reason[0]);
				
		}
		
	}
	
	public boolean isWatching() {
		return !this.watchers.isEmpty();
	}
	
}
