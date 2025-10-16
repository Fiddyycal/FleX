package org.fukkit.recording;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.World;
import org.fukkit.Fukkit;
import org.fukkit.api.helper.ConfigHelper;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.utils.BukkitUtils;
import org.fukkit.utils.WorldUtils;

import io.flex.commons.Nullable;
import io.flex.commons.sql.SQLCondition;
import io.flex.commons.sql.SQLDatabase;
import io.flex.commons.sql.SQLRowWrapper;
import io.flex.commons.utils.FileUtils;

public class Replay extends Recording {
	
	private Set<FleXPlayer> watchers = new HashSet<FleXPlayer>();
	
	protected Replay(File container) throws SQLException {
		
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
	
	public static Replay download(String uniqueId, @Nullable RecordingContext context) throws SQLException, IOException {
		
		SQLCondition<?>[] conditions = context != null ? new SQLCondition<?>[] {
			
				SQLCondition.where("uuid").is(uniqueId),
				SQLCondition.where("context").is(context.toString())
				
			} : new SQLCondition<?>[] {
				
				SQLCondition.where("uuid").is(uniqueId)
				
			};
		
		SQLDatabase base = Fukkit.getConnectionHandler().getDatabase();
		SQLRowWrapper row = base.getRow("flex_recording", conditions);
		
		if (row == null)
			return null;
		
		if (!row.getString("state").equals(RecordingState.COMPLETE.name()))
			throw new IOException("recording is not complete");
		
		String path = ConfigHelper.flow_path + File.separator + uniqueId;
	    File file = new File(path + ".zip");
	    
	    if (file.getParentFile() != null)
	    	file.getParentFile().mkdirs();
	    
	    byte[] data = row.getByteArray("data");
	    
	    try (FileOutputStream fos = new FileOutputStream(file)) {
	        fos.write(data);
	    }
	    
	    FileUtils.unzip(file, path);
	    
	    System.out.println("UNZIPPINGGGGGGGGGGGGGGGGGGGGGGGGGG: " + file.getAbsolutePath() + " to " + path);
		
		return new Replay(new File(path));
		
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
		
		if (world == null)
			throw new UnsupportedOperationException("world must not be null");
		
		for (FleXPlayer watcher : watchers)
			this.watchers.add(watcher);
		
		this.world = world;
		
		Location tp = null;
		
		for (Recordable recordable : this.getRecorded().values()) {
			for (Frame frame : recordable.getFrames()) {
				
				Location loc = frame.getLocation();
				
				if (loc != null) {
					
					loc.setWorld(world);
					
					if (tp == null)
						tp = loc;
					
				}
				
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
