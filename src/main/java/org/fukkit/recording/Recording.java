package org.fukkit.recording;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

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
import io.flex.commons.sql.SQLMap;
import io.flex.commons.utils.ArrayUtils;
import io.flex.commons.utils.FileUtils;

public abstract class Recording extends BukkitRunnable {
	
	protected String uid;
	
	protected World world;
	
	private DataFile<HashMap<UUID, String[]>> data;
	
	protected boolean pause = true;
	
	protected long tick = 0, length = -1;
	
	private RecordingListeners listener;
	
	private Map<UUID, Recordable> recorded = new HashMap<UUID, Recordable>();
	
	private RecordingContext context;
	
	public Recording(File container, @Nullable RecordingContext context) {
		
		Objects.requireNonNull(container, "container cannot be null");

		if (this instanceof Replay) {
			
			if (!container.isDirectory())
				throw new UnsupportedOperationException("container must be a directory");
			
			if (ArrayUtils.contains(container.list(), "data.rec"))
				throw new UnsupportedOperationException("data.rec not found at " + container.getAbsolutePath());
			
		}
		
		this.context = context;
		
		this.data = new DataFile<HashMap<UUID, String[]>>(container.getAbsolutePath(), "data.rec", new LinkedHashMap<UUID, String[]>(), false);
		
		String uid = this.data.getTag("UniqueId", UUID.randomUUID().toString().substring(0, 8));
		String length = this.data.getTag("Length", "-1");
		
		this.data.setTag("UniqueId", this.uid = uid);
		this.data.setTag("Length", this.length = Long.parseLong(length));
		
	}
	
	public String getUniqueId() {
		return this.uid;
	}
	
	public long getLength() {
		return this.length;
	}
	
	public DataFile<HashMap<UUID, String[]>> getData() {
		return this.data;
	}
	
	public Map<UUID, Recordable> getRecorded() {
		return this.recorded;
	}
	
	public long getTick() {
		return this.tick;
	}
	
	public boolean isRecording(Entity entity) {
		return !this.pause && this.recorded.containsKey(entity.getUniqueId());
	}
	
	public boolean isRecording(FleXPlayer player) {
		return this.isRecording(player.getPlayer());
	}
	
	public boolean isPaused() {
		return this.pause;
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
		
		this.pause = false;
		
		this.runTaskTimerAsynchronously(Fukkit.getInstance(), 0L, 2L);
		
	}
	
	public void end() {
		this.end(null);
	}
	
	public void end(@Nullable String reason) {
		
		this.cancel();
		
		this.pause = true;
		
		if (reason == null)
			reason = "End of recording.";
		
		boolean end = reason.equalsIgnoreCase("End of recording.");
		
		if (end)
			Task.debug("Stopping recording: " + reason);
		
		else Task.error("Cancelled recording: " + reason);

		try {
			
			if (end) {
				
				try {
					FileUtils.copy(this.world.getWorldFolder(), this.data.getParentFile());
				} catch (Exception ignore) {}
				
				this.data.setTag("Path", this.world.getWorldFolder().getAbsolutePath());
				this.data.setTag("Length", this.tick);
				
				LinkedHashMap<UUID, String[]> recorded = new LinkedHashMap<UUID, String[]>();
				
				for (Recordable recordable : this.recorded.values()) {
					
					String[] frames = recordable.getFrames().stream().map(f -> f.toString()).toArray(i -> new String[i]);
					
					recorded.put(recordable.getUniqueId(), frames);
				
				}
				
				this.data.write(recorded);
				
				File file = FileUtils.zip(this.data.getParentFile());
				
				Fukkit.getConnectionHandler().getDatabase().addRow("flex_recording", 
						
						SQLMap.of(
								
								SQLMap.entry("uuid", this.uid),
								SQLMap.entry("context", this.context != null ? RecordingContext.NONE : this.context.toString()),
								SQLMap.entry("time", System.currentTimeMillis()),
								SQLMap.entry("state", RecordingState.COMPLETE.name()),
								SQLMap.entry("world", this.world.getName()),
								SQLMap.entry("players", this.recorded.keySet().stream().collect(Collectors.toList()).toString()),
								SQLMap.entry("data", Files.readAllBytes(file.toPath()))
								
						));
				
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
			this.data = null;
			
		}
		
	}
	
	public abstract void onPlayerDisconnect(FleXPlayer player);
	
	public abstract void onComplete();
	
}
