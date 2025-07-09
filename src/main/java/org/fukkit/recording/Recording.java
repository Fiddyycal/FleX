package org.fukkit.recording;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.fukkit.Fukkit;
import org.fukkit.entity.FleXPlayer;
import io.flex.FleX.Task;
import io.flex.commons.Nullable;
import io.flex.commons.file.DataFile;
import io.flex.commons.utils.FileUtils;

public abstract class Recording extends BukkitRunnable {
	
	public static final int DEFAULT_RECORDING_LENGTH = 300;
	
	private DataFile<HashMap<UUID, String[]>> file;
	
	private World world;
	
	private boolean pause = false;
	
	private long tick = 0, length;
	
	private Map<UUID, Recordable> recorded = new HashMap<UUID, Recordable>();
	
	private RecordingListeners listener;
	
	public Recording(@Nullable String path, String name, World world, long length, FleXPlayer... players) {
		
		this(path, name);
		
		this.world = world;
		
		this.length = length < 0 ? DEFAULT_RECORDING_LENGTH : length;
		
		if (!this.file.isFresh())
			throw new UnsupportedOperationException("recording data file already exists");
		
		if (players == null || players.length == 0)
			throw new UnsupportedOperationException("players cannot be null");
		
		for (FleXPlayer fp : players)
			this.recorded.put(fp.getUniqueId(), CraftRecordable.of(fp));
		
		this.runTaskTimerAsynchronously(Fukkit.getInstance(), 0L, 2L);
		
	}
	
	private Recording(@Nullable String path, String name) {
		
		this.file = new DataFile<HashMap<UUID, String[]>>(path != null ? path : "", name + ".rec", new LinkedHashMap<UUID, String[]>(), false);
		
		this.length = this.file.getTag("Length", -1);
		
		if (this.length < 1)
			throw new UnsupportedOperationException("length must be more than 1");
		
	}
	
	public static Recording download(String path, String name) {
		return new Recording(path, name) {
			
			@Override
			public void onPlayerDisconnect(FleXPlayer player) {}
			
		};
	}
	/*
	public Recording(@Nullable String path, String name, FleXPlayer... watchers) {
		
		this.file = new DataFile<HashMap<UUID, String[]>>(path != null ? path : "", name + ".rec", new LinkedHashMap<UUID, String[]>(), false);
		
		if (this.file.isFresh()) {
			
			boolean deleted = this.file.delete();
			
			if (!deleted)
				this.file.deleteOnExit();
			
			throw new UnsupportedOperationException("Recording data file does not exist, make sure that an Admin has moved the file to the correct directory for reviewal");
			
		}
		
		if (watchers == null || watchers.length == 0)
			throw new UnsupportedOperationException("watchers cannot be null");
		
		this.watchers = new HashSet<FleXPlayer>(Arrays.asList(watchers));
		
		FlowLineEnforcementHandler fle = Fukkit.getFlowLineEnforcementHandler();
		
		if (!fle.isFlowEnabled())
			throw new UnsupportedOperationException("FleX Overwatch replay system is not enabled on this server.");
		
		HashMap<UUID, String[]> frames = null;
		
		try {
			
			frames = this.file.read();
			
			if (frames == null || frames.isEmpty())
				throw new UnsupportedOperationException("Frames map cannot be empty");
			
		} catch (Exception e) {
			throw new UnsupportedOperationException("Frames map cannot be read");
		}
		
		this.world = WorldUtils.copyWorld(this.file.getParentFile().getAbsolutePath(), Bukkit.getWorldContainer().getAbsolutePath() + File.separator + "stage-" + this.file.getName());
		
		Location tp = null;
		
		for (Entry<UUID, String[]> entry : frames.entrySet()) {
			
			UUID uuid = entry.getKey();
			
			FleXPlayer fp = Fukkit.getPlayer(uuid);
			
			if (fp == null)
				continue;
			
			if (fle.getAIDriver() == AIDriver.FLEX)
				throw new UnsupportedOperationException(
						
						"The FleX AI driver is undergoing heavy maintenance, "
						+ "please do not use this driver: "
						+ "Citizens plugin could not be found, "
						+ "please correct this error before continuing startup.");
			
			Recordable recordable = Fukkit.getImplementation().createRecordable(fp);
			
			if (recordable == null)
				throw new UnsupportedOperationException("AI Driver " + fle.getAIDriver().name() + " not found");
			
			this.recorded.add(recordable);
			
			String[] serialized = entry.getValue();
			
			for (String frame : serialized) {
				
				Frame f = Frame.from(frame);
				
				Location loc = f.getLocation();
				
				if (loc != null) {
					
					loc.setWorld(this.world);
					
					if (tp == null)
						tp = loc;
					
				}
				
				if (f.getObject() != null)
					f.getObject().setWorld(this.world);
				
				recordable.getFrames().add(f);
				
			}
			
		}
		
		if (tp == null)
			throw new UnsupportedOperationException("Could not find appropriate spawn location for watchers");
		
		for (FleXPlayer watcher : this.watchers)
			watcher.teleport(tp);
		
		this.runTaskTimer(Fukkit.getInstance(), 20L, 2L);
		
	}
	*/
	public DataFile<HashMap<UUID, String[]>> getData() {
		return this.file;
	}
	
	public Map<UUID, Recordable> getRecorded() {
		return this.recorded;
	}
	
	public long getTick() {
		return this.tick;
	}
	
	public boolean isRecording(Entity entity) {
		return this.isRecording() && this.recorded.containsKey(entity.getUniqueId());
	}
	
	public boolean isRecording(FleXPlayer player) {
		return this.isRecording(player.getPlayer());
	}
	
	public boolean isRecording() {
		return !this.isCancelled();
	}

	@Override
	public void run() {
		
		if (this.pause)
			return;
		
		if (this.listener == null)
			this.listener = new RecordingListeners(this);
		
		if (Bukkit.getWorld(this.world.getUID()) == null) {
			
			this.end("world cannot be null.");
			return;
			
		}
		
		for (Recordable recorded : this.recorded.values()) {
			
			FleXPlayer player = recorded.toPlayer();
			
			if (player == null) {
				
				this.end("player cannot be null.");
				return;
				
			}
			
			List<Frame> frames = recorded.getFrames();
			
			if (!player.isOnline()) {
				
				// Player has disconnected but may come back...
				frames.add(null);
				return;
				
			}
			
			if (!player.getPlayer().getWorld().getUID().equals(this.world.getUID())) {
				
				// Player has moved worlds but may come back...
				frames.add(null);
				return;
				
			}
			
			frames.add(new Frame(RecordedAction.MOVE, player.getLocation()));
			
		}
		
		if (this.tick == this.length) {
			
			this.end();
			return;
			
		}
		
		this.tick++;
		
	}
	
	public void end(String... reason) {
		
		if (reason == null || reason.length == 0)
			reason = new String[]{ "End of recording." };
		
		boolean end = reason[0].equals("End of recording.");
		
		if (end)
			Task.debug("Stopping recording: " + reason[0]);
		
		else Task.error("Cancelled recording: " + reason[0]);
		
		this.cancel();
		
		if (this.listener != null)
			this.listener.unregister();
			
		if (end) {
			
			FileUtils.copy(this.world.getWorldFolder(), this.file.getParentFile());
			
			this.file.setTag("Length", this.tick);
			
			LinkedHashMap<UUID, String[]> recorded = new LinkedHashMap<UUID, String[]>();
			
			for (Recordable recordable : this.recorded.values()) {
				
				String[] frames = recordable.getFrames().stream().map(f -> f.toString()).toArray(i -> new String[i]);
				
				recorded.put(recordable.getUniqueId(), frames);
				
			}
			
			this.file.write(recorded);
			
		}
		
	}
	
	public abstract void onPlayerDisconnect(FleXPlayer player);

}
