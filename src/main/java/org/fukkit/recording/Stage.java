package org.fukkit.recording;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.scheduler.BukkitRunnable;
import org.fukkit.Fukkit;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.flow.Overwatch;
import org.fukkit.utils.BukkitUtils;
import org.fukkit.utils.WorldUtils;

import io.flex.commons.Nullable;
import io.flex.commons.file.DataFile;
import io.flex.commons.utils.ArrayUtils;
import io.flex.commons.utils.FileUtils;

public class Stage extends BukkitRunnable {
	
	private UUID uuid;
	
	private Set<FleXPlayer> watchers;
	
	private Recording recording;
	
	private World world;
	
	private long tick = 0;
	
	private boolean pause, anonymous;
	
	public Stage(Recording recording, boolean anonymous, @Nullable FleXPlayer... watchers) throws FileAlreadyExistsException {
		
		this.uuid = recording.getUniqueId();
		
		this.recording = recording;
		
		this.anonymous = anonymous;
		
		this.watchers = new HashSet<FleXPlayer>();
		
		for (FleXPlayer watcher : watchers)
			this.watchers.add(watcher);
		
		File data = recording.getData();
		File directory = data.getParentFile();
		
		if (data instanceof DataFile == false)
			throw new UnsupportedOperationException("data must be DataFile");
		
		boolean worldContents = ArrayUtils.contains(directory.list(), "region");
		
		World world = Bukkit.getWorld(data.getName());
		
		if (world != null)
			throw new FileAlreadyExistsException("That file is being reviewed, please try again later");
		
		if (worldContents)
			world = WorldUtils.copyWorld(recording.getData().getParentFile().getAbsolutePath(), Bukkit.getWorldContainer().getPath() + File.separator + data.getName());
		
		else {
			
			String path = ((DataFile<?>)data).getTag("Path");
			
			if (path != null)
				world = WorldUtils.copyWorld(path, Bukkit.getWorldContainer().getPath() + File.separator + "flow-" + this.uuid);
				
		}
		
		if (world == null) {
			
			WorldCreator creator = new WorldCreator(data.getName());
			
		    creator.type(WorldType.FLAT);
		    creator.generateStructures(false);
		    
		    world = Bukkit.createWorld(creator);
			
		}
		
		if (world == null)
			throw new UnsupportedOperationException("world must not be null");
		
		this.world = world;
			
		Location tp = null;
		
		for (Recordable recordable : this.recording.getRecorded().values()) {
			for (Frame frame : recordable.getFrames()) {
				
				Location loc = frame.getLocation();
				
				System.out.println(loc);
				
				loc.setWorld(world);
				
				if (tp == null && loc != null) {
					
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
			throw new UnsupportedOperationException("Could not find appropriate spawn location for watchers");
		
		for (FleXPlayer watcher : watchers)
			watcher.teleport(tp);
		
		this.runTaskTimerAsynchronously(Fukkit.getInstance(), 120L, 2L);
			
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
		
		for (Recordable recordable : this.recording.getRecorded().values()) {
			
			Frame frame = recordable.getFrames().get((int)this.tick);
			
			Location location = frame.getLocation();
			
			RecordedAction action = frame.getAction();
			
			if (location != null)
				((CraftRecorded)recordable).getActor().teleport(location);
			
			if (action != null)
				((CraftRecorded)recordable).getActor().playAnimation(action);
			
		}
		
	}
	
	public void end(String... reason) {
		
		if (reason == null || reason.length == 0)
			reason = new String[]{ "No further information." };
		
		System.out.println("Stopping stage: " + reason[0]);
		
		this.cancel();
		
		String kick = reason[0];
		
		BukkitUtils.mainThread(() -> {
			
			for (FleXPlayer watcher : this.watchers) {
				
				if (watcher.isOnline())
					watcher.kick("The stage has closed: " + kick);
					
			}
			
			WorldUtils.unloadWorld(this.world, false);
			FileUtils.delete(this.world.getWorldFolder());
			
		});
		
	}
	
	public boolean isWatching() {
		return !this.watchers.isEmpty();
	}
	
}
