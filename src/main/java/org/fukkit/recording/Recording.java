package org.fukkit.recording;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.fukkit.Fukkit;
import org.fukkit.entity.FleXPlayer;

import io.flex.FleX.Task;
import io.flex.commons.Nullable;
import io.flex.commons.file.DataFile;
import io.flex.commons.utils.FileUtils;

public abstract class Recording extends BukkitRunnable {
	
	protected UUID uuid;
	
	private DataFile<HashMap<UUID, String[]>> file;
	
	private World world;
	
	private boolean pause = false, complete = false;
	
	private long tick = 0, length = -1;
	
	private Map<UUID, Recordable> recorded = new HashMap<UUID, Recordable>();
	
	private RecordingListeners listener;
	
	public Recording(String path) {
		
		if (path == null)
			throw new UnsupportedOperationException("path cannot be null");
		
		this.file = new DataFile<HashMap<UUID, String[]>>(path, new LinkedHashMap<UUID, String[]>(), false);
		
		if (this.file.isZip())
			this.file = this.file.unzip();
		
		String uid = this.file.getTag("UniqueId", UUID.randomUUID().toString());
		String length = this.file.getTag("Length", "-1");
		
		this.uuid = UUID.fromString(uid);
		
		this.length = Long.parseLong(length);
		
		System.out.println("LENGTH:::::::::::::::::::::::::::::::::::::: " + length);
		
		this.file.setTag("UniqueId", this.uuid.toString());
		this.file.setTag("Length", length);
		
		for (Entry<String, Serializable> iterable_element : this.file.asTags().entrySet()) {
			System.out.println("v: " + iterable_element.getKey() + ": " + iterable_element.getValue());
		}
		
		if (this.length > 0) {
			
			LinkedHashMap<UUID, String[]> recorded = (LinkedHashMap<UUID, String[]>) this.file.read();
			
			if (recorded == null || recorded.isEmpty())
				throw new UnsupportedOperationException("recorded data is empty");
			
			for (Entry<UUID, String[]> entry : recorded.entrySet()) {
				
				UUID uuid = entry.getKey();
				
				String[] actions = entry.getValue();
				
				LinkedList<Frame> frames = new LinkedList<Frame>();
				
				for (String action : actions)
					frames.add(Frame.from(action));
				
				System.out.println("TEST 2: " + uuid + " / " + frames.size());
				
				this.recorded.put(uuid, CraftRecorded.of(Fukkit.getPlayer(uuid), frames));
				
			}
			
			if (this.recorded.isEmpty())
				throw new UnsupportedOperationException("recorded data is empty");
			
		}
		
		this.file.delete();
		
	}
	
	public UUID getUniqueId() {
		return this.uuid;
	}
	
	public long getLength() {
		return this.length;
	}
	
	public static Recording download(String path) {
		return new Recording(path) {
			
			@Override
			public void onComplete() {}
			
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
		return !this.complete;
	}

	@Override
	public void run() {
		
		if (this.pause)
			return;
		
		if (Bukkit.getWorld(this.world.getUID()) == null) {
			
			this.end("world cannot be null.");
			return;
			
		}
		
		if (this.listener == null)
			this.listener = new RecordingListeners(this);
		
		for (Recordable recorded : this.recorded.values()) {
			
			FleXPlayer player = recorded.toPlayer();
			
			if (player == null) {
				
				// Player (or Bot) existed before and is now invalid.
				continue;
				
			}
			
			List<Frame> frames = recorded.getFrames();
			
			if (player != null && !player.isOnline()) {
				
				// Player has disconnected but may come back...
				frames.add(null);
				
				this.onPlayerDisconnect(player);
				continue;
				
			}
			
			if (player != null) {
				
				Player pl = player.getPlayer();

				// Player (or Bot) is invalid but online
				if (pl == null || pl.isDead() || !pl.isValid())
					continue;
				
			}
			
			if (!player.getPlayer().getWorld().getUID().equals(this.world.getUID())) {
				
				// Player has moved worlds but may come back...
				frames.add(null);
				continue;
				
			}
			
			frames.add(new Frame(RecordedAction.MOVE, player.getLocation()));
			
		}
		
		if (this.tick == this.length) {
			
			this.end();
			return;
			
		}
		
		this.tick++;
		
	}
	
	public void start(World world, long length, FleXPlayer... players) {

		if (world == null)
			throw new UnsupportedOperationException("world must not be null");
		
		this.world = world;
		
		if (length < 0)
			throw new UnsupportedOperationException("length must be more than 0");
		
		this.length = length;
		
		if (players == null || players.length == 0)
			throw new UnsupportedOperationException("players cannot be null");
		
		for (FleXPlayer fp : players) {
			
			if (fp == null)
				continue;
			
			CraftRecordable rec = CraftRecordable.of(fp);
			
			this.recorded.put(fp.getUniqueId(), rec);
			
		}
		
		this.runTaskTimerAsynchronously(Fukkit.getInstance(), 0L, 2L);
		
	}
	
	public void end() {
		this.end(null);
	}
	
	public void end(@Nullable String reason) {
		
		this.cancel();
		
		// isCancelled doesn't exist in Java 7 (Minecraft 1.8)
		this.complete = true;
		
		if (reason == null)
			reason = "End of recording.";
		
		boolean end = reason.equalsIgnoreCase("End of recording.");
		
		if (end)
			Task.debug("Stopping recording: " + reason);
		
		else Task.error("Cancelled recording: " + reason);

		try {
			
			if (end) {
				
				try {
					FileUtils.copy(this.world.getWorldFolder(), this.file.getParentFile());
				} catch (Exception ignore) {}
				
				this.file.setTag("Path", this.world.getWorldFolder().getAbsolutePath());
				this.file.setTag("Length", this.tick);
				
				LinkedHashMap<UUID, String[]> recorded = new LinkedHashMap<UUID, String[]>();
				
				for (Recordable recordable : this.recorded.values()) {
					
					String[] frames = recordable.getFrames().stream().map(f -> f.toString()).toArray(i -> new String[i]);
					
					recorded.put(recordable.getUniqueId(), frames);
				
				}
				
				this.file.write(recorded);
				
				this.onComplete();
				
			}
			
		} catch(Exception e) {

			e.printStackTrace();
			
		} finally {
			
			// Cleanup
			if (this.listener != null)
				this.listener.unregister();
			
			if (!this.recorded.isEmpty())
				this.recorded.clear();
			
			this.world = null;
			this.recorded = null;
			
			this.file = null;
			
		}
		
	}
	
	public abstract void onPlayerDisconnect(FleXPlayer player);

	public abstract void onComplete();
	
}
