package org.fukkit.recording;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.flow.AsyncRecordingCompleteEvent;
import org.fukkit.event.flow.AsyncRecordingStartEvent;

import io.flex.FleX.Task;
import io.flex.commons.Nullable;
import io.flex.commons.cache.Cacheable;
import io.flex.commons.file.DataFile;
import io.flex.commons.sql.SQLCondition;
import io.flex.commons.sql.SQLDatabase;
import io.flex.commons.sql.SQLMap;
import io.flex.commons.sql.SQLRowWrapper;
import io.flex.commons.utils.ArrayUtils;
import io.flex.commons.utils.FileUtils;

public abstract class Recording extends BukkitRunnable implements Cacheable {
	
	public static final long TICK_RATE = 2L;
	
	protected String name;
	
	protected World world;
	
	private DataFile<HashMap<UUID, String[]>> data;
	
	protected boolean pause = true;
	
	protected long tick = 0, length = -1;
	
	private RecordingListeners listener;
	
	private Map<UUID, Recordable> recorded = new HashMap<UUID, Recordable>();
	
	private RecordingContext context;
	
	public Recording(File container, @Nullable RecordingContext context) throws SQLException {
		
		Objects.requireNonNull(container, "container cannot be null");
		
		this.name = container.getName();
		
		if (this.name.contains(" "))
			throw new IllegalArgumentException("name cannot contain any spaces");
		
		boolean replay = this instanceof Replay;
		
		if (replay) {
			
			if (!container.exists())
				throw new UnsupportedOperationException("container does not exist");
			
			if (!container.isDirectory())
				throw new UnsupportedOperationException("container must be a directory");
			
			if (!ArrayUtils.contains(container.list(), "data.rec"))
				throw new UnsupportedOperationException("data.rec not found at " + container.getAbsolutePath());
			
		} else {
			
			if (container.exists())
				throw new UnsupportedOperationException("recording name must be unique, recording \"" + this.name + "\" already exists");
			
		}
		
		this.context = context;
		
		this.data = new DataFile<HashMap<UUID, String[]>>(container.getAbsolutePath(), "data.rec", new LinkedHashMap<UUID, String[]>(), false);
		
		long length = this.data.getTag("Length", -1L);
		
		this.data.setTag("UniqueId", this.name);
		this.data.setTag("Length", this.length = length);
		
		// init recording row
		if (!replay)
			this.getRow();
		
		Memory.RECORDING_CACHE.add(this);
		
	}
	
	public String getUniqueId() {
		return this.name;
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
	
	public World getWorld() {
		return this.world;
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
		
		for (Recordable recorded : this.recorded.values()) {
			
			Map<Long, Frame> frames = recorded.getFrames();
			
			if (recorded.getUniqueId().equals(Recordable.SYSTEM_UID)) {
				
				frames.put(this.tick, null);
				continue;
				
			}
			
			FleXPlayer player = recorded.toPlayer();
			
			if (player == null) {
				
				// Player (or Bot) existed before and is now invalid.
				continue;
				
			}
			
			if (player != null && !player.isOnline()) {
				
				// Player has disconnected but may come back...
				frames.put(this.tick, null);
				
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
				frames.put(this.tick, null);
				continue;
				
			}
			
			Frame frame = frames.getOrDefault(this.tick, new Frame(player.getLocation()));
			
			frame.addAction(RecordedAction.MOVE);
			
			if (player.getPlayer().isSneaking())
				frame.addAction(RecordedAction.CROUCH);
			
			else frame.addAction(RecordedAction.UNCROUCH);
			
			ItemStack helmet = player.getHelmet();
			ItemStack chestplate = player.getChestplate();
			ItemStack leggings = player.getLeggings();
			ItemStack boots = player.getBoots();
			
			if (helmet != null) {
				
				ItemStack cached = recorded.getHelmet();
				
				if (cached == null || !cached.equals(helmet)) {
					
					recorded.setHelmet(helmet);
					
					frame.addAction(RecordedAction.EQUIP_HELMET);
					frame.setItem(helmet);
					
				}
				
			}
			
			if (chestplate != null) {
				
				ItemStack cached = recorded.getChestplate();
				
				if (cached == null || !cached.equals(chestplate)) {
					
					recorded.setChestplate(chestplate);
					
					frame.addAction(RecordedAction.EQUIP_CHESTPLATE);
					frame.setItem(chestplate);
					
				}
					
			}
			
			if (leggings != null) {
				
				ItemStack cached = recorded.getLeggings();
				
				if (cached == null || !cached.equals(leggings)) {
					
					recorded.setLeggings(leggings);
					
					frame.addAction(RecordedAction.EQUIP_LEGGINGS);
					frame.setItem(leggings);
					
				}
					
			}
			
			if (boots != null) {
				
				ItemStack cached = recorded.getBoots();
				
				if (cached == null || !cached.equals(boots)) {
					
					recorded.setBoots(boots);
					
					frame.addAction(RecordedAction.EQUIP_BOOTS);
					frame.setItem(boots);
					
				}
					
			}
			
			frames.put(this.tick, frame);
			
		}
		
		if (this.tick == this.length) {
			
			this.end();
			return;
			
		}
		
		this.tick++;
		
	}
	
	public void start(World world, int duration, FleXPlayer... players) throws SQLException {
		
		if (world == null)
			throw new UnsupportedOperationException("world must not be null");
		
		this.world = world;
		
		this.length = (long) (duration * (20.0 / TICK_RATE));
		
		if (this.length <= 0)
			throw new UnsupportedOperationException("length must be more than 0");
		
		if (players == null || players.length == 0)
			throw new UnsupportedOperationException("players cannot be null");
		
		AsyncRecordingStartEvent event = new AsyncRecordingStartEvent(this);
		
		Fukkit.getEventFactory().call(event);
		
		if (event.isCancelled())
			return;
		
		this.recorded.put(Recordable.SYSTEM_UID, new Recordable() {
			
			private Map<Long, Frame> frames = new HashMap<Long, Frame>();
			
			@Override
			public FleXPlayer toPlayer() {
				throw new UnsupportedOperationException("cannot cast system to player");
			}
			
			@Override
			public boolean isBot() {
				return false;
			}
			
			@Override
			public UUID getUniqueId() {
				return Recordable.SYSTEM_UID;
			}
			
			@Override
			public String getName() {
				return "SYSTEM";
			}
			
			@Override
			public Map<Long, Frame> getFrames() {
				return this.frames;
			}

			@Override
			public ItemStack getHelmet() {
				throw new UnsupportedOperationException("cannot cast system to player");
			}

			@Override
			public ItemStack getChestplate() {
				throw new UnsupportedOperationException("cannot cast system to player");
			}

			@Override
			public ItemStack getLeggings() {
				throw new UnsupportedOperationException("cannot cast system to player");
			}

			@Override
			public ItemStack getBoots() {
				throw new UnsupportedOperationException("cannot cast system to player");
			}

			@Override
			public void setHelmet(ItemStack helmet) {
				throw new UnsupportedOperationException("cannot cast system to player");
			}

			@Override
			public void setChestplate(ItemStack chestplate) {
				throw new UnsupportedOperationException("cannot cast system to player");
			}

			@Override
			public void setLeggings(ItemStack leggings) {
				throw new UnsupportedOperationException("cannot cast system to player");
			}

			@Override
			public void setBoots(ItemStack boots) {
				throw new UnsupportedOperationException("cannot cast system to player");
			}
			
		});
		
		for (FleXPlayer fp : players) {
			
			if (fp == null)
				continue;
			
			CraftRecordable rec = CraftRecordable.of(fp);
			
			this.recorded.put(fp.getUniqueId(), rec);
			
		}
		
		this.pause = false;
		
		// Need to re-retrieve row because of some internal data changes...
		SQLDatabase base = Fukkit.getConnectionHandler().getDatabase();
		SQLRowWrapper row = base.getRow("flex_recording", SQLCondition.where("uuid").is(this.name), SQLCondition.where("context").is(this.context.toString()));
		
		if (row != null) {
			
			row.set("time", System.currentTimeMillis());
			row.set("state", RecordingState.RECORDING.name());
			row.update();
			
		}
		
		if (this.listener == null)
			this.listener = new RecordingListeners(this);
		
		this.runTaskTimerAsynchronously(Fukkit.getInstance(), 0L, TICK_RATE);
		
	}
	
	public void end() {
		this.end(null);
	}
	
	public void end(@Nullable String reason) {
		
		if (Bukkit.isPrimaryThread())
			throw new IllegalStateException("end must be called asynchronously.");
			
		this.cancel();
		
		this.pause = true;
		
		if (reason == null)
			reason = "End of recording.";
		
		boolean end = reason.equalsIgnoreCase("End of recording.");
		
		if (end) {
			
			Task.debug("Stopping recording: " + reason);
			
			Fukkit.getEventFactory().call(new AsyncRecordingCompleteEvent(this));
			
			this.onComplete();
			
		}
		
		else Task.error("Cancelled recording: " + reason);
		
		try {
			
			if (end) {
				
				// TODO Make an option for copying the whole world, null != null means this is disablerd right now because world files are large and I don't want the server to crash atm.
				// At the moment, if the recording is made on a MAP then the Path will point to the map is playback mode.
				if (null != null) {
					try {
						FileUtils.copy(this.world.getWorldFolder(), this.data.getParentFile());
					} catch (Exception ignore) {}
				}
				
				this.data.setTag("Path", this.world.getWorldFolder().getAbsolutePath());
				this.data.setTag("Length", this.tick);
				
				LinkedHashMap<UUID, String[]> recorded = new LinkedHashMap<UUID, String[]>();
				
				for (Recordable recordable : this.recorded.values()) {
					
					String[] frames = recordable.getFrames().values().stream().map(f -> f == null ? null : f.toString()).toArray(i -> new String[i]);
					
					recorded.put(recordable.getUniqueId(), frames);
					
				}
				
				this.data.write(recorded);
				
				File file = FileUtils.zip(this.data.getParentFile());
				
				SQLRowWrapper row = this.getRow();
				
				if (row != null) {
					
					try {
						
						row.set("time", System.currentTimeMillis());
						row.set("duration", this.tick * (double) TICK_RATE / 20.0);
						row.set("state", RecordingState.COMPLETE.name());
						row.set("data", Files.readAllBytes(file.toPath()));
						
						row.update();
						
					} catch (Exception e) {
						
						row.set("state", RecordingState.ERROR.name());
						row.set("data", e.getMessage().getBytes(StandardCharsets.UTF_8));
						
						row.update();
						
					}
					
				}
				
			}
			
		} catch(Exception e) {
			
			e.printStackTrace();
			
		} finally {
			
			this.destroy();
			
		}
		
	}
	
	public void destroy() {
		
		// Cleanup
		if (this.listener != null)
			this.listener.unregister();
		
		if (!this.recorded.isEmpty())
			this.recorded.clear();
		
		this.world = null;
		this.listener = null;
		this.recorded = null;
		this.data = null;
		
		Memory.RECORDING_CACHE.remove(this);
		
	}
	
	public SQLRowWrapper getRow() throws SQLException {
		
		SQLDatabase base = Fukkit.getConnectionHandler().getDatabase();
		SQLRowWrapper row = null;
		
		Set<SQLRowWrapper> rows = base.getRows("flex_recording", SQLCondition.where("context").is(this.context.toString()));
		
		if (row == null)
			row = rows.stream().findFirst().orElse(null);
		
		if (row == null) {
			
			return base.addRow("flex_recording", "context",
					
					SQLMap.of(
							
							SQLMap.entry("uuid", this.name),
							SQLMap.entry("context", this.context.toString()),
							SQLMap.entry("time", System.currentTimeMillis()),
							SQLMap.entry("duration", 0.0),
							SQLMap.entry("state", RecordingState.STAGED.name()),
							SQLMap.entry("world", this.world != null ? this.world.getName() : null),
							SQLMap.entry("players", this.recorded.keySet().stream().collect(Collectors.toList()).toString()),
							SQLMap.entry("data", Collections.emptyList().toString())
							
					));
			
		}
			
		return row;
		
	}
	
	public abstract void onPlayerDisconnect(FleXPlayer player);
	
	public abstract void onComplete();
	
}
