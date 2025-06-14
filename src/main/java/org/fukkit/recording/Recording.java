package org.fukkit.recording;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.fukkit.Fukkit;
import org.fukkit.ai.AIDriver;
import org.fukkit.entity.FleXBot;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.handlers.FlowLineEnforcementHandler;
import org.fukkit.utils.WorldUtils;

import io.flex.commons.Nullable;
import io.flex.commons.file.DataFile;
import io.flex.commons.utils.FileUtils;

public class Recording extends BukkitRunnable {
	
	public static final int RECORDING_LENGTH = 300;
	
	private DataFile<HashMap<UUID, String[]>> file;
	
	private World world;
	
	private boolean pause = false;
	
	private long tick = 0;
	
	private Set<FleXPlayer> watchers = new HashSet<FleXPlayer>();
	private Set<Recordable> recorded = new HashSet<Recordable>();
	
	private RecordingListeners listener;
	
	public Recording(@Nullable String path, String name, World world, FleXPlayer... record) {
		
		this.file = new DataFile<HashMap<UUID, String[]>>(path != null ? path : "", name + ".rec", new LinkedHashMap<UUID, String[]>(), false);
		
		this.world = world;
		
		if (!this.file.isFresh())
			throw new UnsupportedOperationException("recording data file already exists");
		
		if (record == null || record.length == 0)
			throw new UnsupportedOperationException("record cannot be null");
		
		for (FleXPlayer fp : record)
			this.recorded.add(CraftRecordable.of(fp));
		
		FileUtils.copy(this.world.getWorldFolder(), this.file.getParentFile());
		
		this.runTaskTimer(Fukkit.getInstance(), 0L, 2L);
		
	}
	
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
	
	public DataFile<HashMap<UUID, String[]>> getData() {
		return this.file;
	}
	
	public Set<Recordable> getRecorded() {
		return this.recorded;
	}
	
	public World getWorld() {
		return this.world;
	}
	
	public long getTick() {
		return this.tick;
	}
	
	public boolean isRecording(Entity entity) {
		return this.isRecording() && this.recorded.stream().anyMatch(r -> r.getUniqueId().equals(entity.getUniqueId()));
	}
	
	public boolean isRecording(FleXPlayer player) {
		return this.isRecording(player.getPlayer());
	}
	
	public boolean isRecording() {
		return this.watchers.isEmpty();
	}

	@Override
	public void run() {
		
		if (this.pause)
			return;
		
		if (this.listener == null)
			this.listener = new RecordingListeners(this);
		
		if (this.isRecording()) {
			
			for (Recordable recorded : this.recorded) {
				
				FleXPlayer fp = recorded.toPlayer();
				
				if (fp == null) {
					
					this.end("fp cannot be null");
					return;
					
				}
				
				List<Frame> frames = recorded.getFrames();
				
				if (fp == null || !fp.isOnline() || !fp.getPlayer().getWorld().getUID().equals(this.world.getUID())) {
					
					// Player is not present, but may come back.
					frames.add(null);
					continue;
					
				}
				
				frames.add(new Frame(RecordedAction.MOVE, fp.getLocation()));
				
			}
			
			if (this.tick == RECORDING_LENGTH) {
				
				this.end("End of recording.");
				return;
				
			}
			
		} else {
			
			for (Recordable recorded : this.recorded) {
				
				if (recorded instanceof FleXBot == false)
					continue;
				
				FleXBot bot = (FleXBot) recorded;
				
				try {
					
					Frame frame = recorded.getFrames().get((int)this.tick);
					
					if (frame.getAction() == null || frame.getAction() == RecordedAction.IDLE)
						continue;
					
					bot.teleport(frame.getLocation());
					
					if (frame.getAction().isAnimation())
						bot.playAnimation(frame.getAction());
						
					continue;
					
				} catch (Exception e) {}

				// If an exception is thrown as a result of location being null or IndexOutOfBounds, delete the bot.
				bot.delete();
				
			}
			
			if (this.tick == RECORDING_LENGTH) {
				
				this.tick = 0;
				return;
				
			}
			
		}
		
		this.tick++;
		
	}
	
	public void end(String... reason) {
		
		if (reason == null || reason.length == 0)
			reason = new String[]{ "No further information." };
		
		System.out.println("Stopping recording: " + reason[0]);
		
		this.cancel();
		
		if (this.isRecording()) {
			
			if (this.listener != null)
				this.listener.unregister();
				
			if (reason[0].equals("End of recording.")) {
				
				this.file.setTag("Length", this.tick);
				
				LinkedHashMap<UUID, String[]> recorded = new LinkedHashMap<UUID, String[]>();
				
				for (Recordable recordable : this.recorded) {
					
					String[] frames = recordable.getFrames().stream().map(f -> f.toString()).toArray(i -> new String[i]);
					
					recorded.put(recordable.getUniqueId(), frames);
					
				}
				
				this.file.write(recorded);
				
			}
			
		} else {
			
			if (reason[0].equals("End of recording.")) {
				
				System.out.println("Stopping stage: " + reason[0]);
				
				for (FleXPlayer fp : this.watchers) {
					
					if (fp != null && fp.isOnline())
						fp.kick("The stage has closed: " + reason[0]);
						
				}
				
				for (Recordable recorded : this.recorded)
					if (recorded instanceof FleXBot)
						((FleXBot)recorded).delete();
				
				try {
					
					if (this.world != null)
						WorldUtils.unloadWorld(this.world);
					
				} catch (Exception e) {}
				
				File file = this.world.getWorldFolder();
				
				if (file.exists())
					FileUtils.delete(file);
				
			}
			
		}
		
	}

}
