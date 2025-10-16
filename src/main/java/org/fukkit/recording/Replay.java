package org.fukkit.recording;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.fukkit.Fukkit;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.utils.BukkitUtils;
import org.fukkit.utils.WorldUtils;

import io.flex.commons.Nullable;
import io.flex.commons.file.DataFile;
import io.flex.commons.utils.ArrayUtils;
import io.flex.commons.utils.FileUtils;

public class Replay extends Recording {
	
	private Set<FleXPlayer> watchers = new HashSet<FleXPlayer>();
	
	public Replay(File container) {
		
		super(container, null);
		
		if (this.length < 1)
			throw new UnsupportedOperationException("invalid recording length");
			
		LinkedHashMap<UUID, String[]> recorded = (LinkedHashMap<UUID, String[]>) this.getData().read();
		
		if (recorded == null || recorded.isEmpty())
			throw new UnsupportedOperationException("recorded data is empty");
		
		for (Entry<UUID, String[]> entry : recorded.entrySet()) {
			
			UUID uuid = entry.getKey();
			
			String[] actions = entry.getValue();
			
			LinkedList<Frame> frames = new LinkedList<Frame>();
			
			for (String action : actions)
				frames.add(Frame.from(action));
			
			System.out.println("TEST 2: " + uuid + " / " + frames.size());
			
			this.getRecorded().put(uuid, CraftRecorded.of(Fukkit.getPlayer(uuid), frames));
			
		}
		
		if (this.getRecorded().isEmpty())
			throw new UnsupportedOperationException("recorded data is empty");
		
	}
	
	public Set<FleXPlayer> getWatchers() {
		return this.watchers;
	}

	@Override
	public void run() {
		
		for (FleXPlayer watcher : this.watchers) {
			if (!watcher.isOnline() || !watcher.getPlayer().getWorld().getUID().equals(this.world.getUID())) {
				this.onPlayerDisconnect(watcher);
				return;
			}
		}
		
		if (this.pause)
			return;
		
		if (this.tick == this.getLength()) {
			
			this.onComplete();
			return;
			
		}
		
		for (Recordable recordable : this.getRecorded().values()) {
			
			Frame frame = recordable.getFrames().get((int)this.tick);
			
			Location location = frame.getLocation();
			
			RecordedAction action = frame.getAction();
			
			if (location != null)
				((CraftRecorded)recordable).getActor().teleport(location);
			
			if (action != null)
				((CraftRecorded)recordable).getActor().playAnimation(action);
			
		}
		
		this.tick++;
		
	}
	
	@Override
	public void start(World world, long length, FleXPlayer... watchers) {
		
		for (FleXPlayer watcher : watchers)
			this.watchers.add(watcher);

		File data = this.getData();
		File parent = data.getParentFile();
		String name = parent.getName();
		
		boolean worldContents = ArrayUtils.contains(parent.list(), "region");
		
		this.world = Bukkit.getWorld(name);
		
		if (world != null)
			throw new UnsupportedOperationException("That file is being reviewed, please try again later");
		
		if (worldContents)
			world = WorldUtils.copyWorld(parent.getAbsolutePath(), Bukkit.getWorldContainer().getPath() + File.separator + name);
		
		else {
			
			String path = ((DataFile<?>)data).getTag("Path");
			
			if (path != null)
				world = WorldUtils.copyWorld(path, Bukkit.getWorldContainer().getPath() + File.separator + name);
				
		}
		
		if (world == null) {
			
			WorldCreator creator = new WorldCreator(name);
			
		    creator.type(WorldType.FLAT);
		    creator.generateStructures(false);
		    
		    world = Bukkit.createWorld(creator);
			
		}
		
		if (world == null)
			throw new UnsupportedOperationException("world must not be null");
		
		this.world = world;
			
		Location tp = null;
		
		for (Recordable recordable : this.getRecorded().values()) {
			for (Frame frame : recordable.getFrames()) {
				
				Location loc = frame.getLocation();
				
				loc.setWorld(world);
				
				if (tp == null && loc != null)
					tp = loc;
				
			}
		}
		
		if (tp == null)
			throw new UnsupportedOperationException("Could not find appropriate spawn location for watchers");
		
		for (FleXPlayer watcher : watchers)
			watcher.teleport(tp);
		
		this.pause = false;
		
		this.runTaskTimerAsynchronously(Fukkit.getInstance(), 120L, 2L);
		
	}

	@Override
	public void end(@Nullable String reason) {
		
		if (reason == null)
			reason = "No further information.";
		
		System.out.println("Stopping stage: " + reason);
		
		this.cancel();
		
		String kick = reason;
		
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

	@Override
	public void onPlayerDisconnect(FleXPlayer player) {
		this.end("Watcher has disconnected.");
	}

	@Override
	public void onComplete() {
		
		System.out.println("Replaying stage...");
		
		this.tick = 0;
		
		this.pause = false;
		
	}
	
}
