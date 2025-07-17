package org.fukkit.recording;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.handlers.FlowLineEnforcementHandler;
import org.fukkit.recording.Recording;
import org.fukkit.utils.WorldUtils;

import io.flex.commons.Nullable;

public class Stage {
	
	private UUID uuid = UUID.randomUUID();
	
	private Set<FleXPlayer> watchers;
	
	private Recording recording;
	
	private World world;
	
	private long tick = 0;
	
	private boolean pause;
	
	public Stage(Recording recording, @Nullable FleXPlayer... watchers) {
		
		this.recording = recording;
		
		this.watchers = new HashSet<FleXPlayer>();
		
		for (FleXPlayer watcher : watchers)
			this.watchers.add(watcher);
		
		World world = WorldUtils.copyWorld(recording.getData(), Bukkit.getWorldContainer().getPath() + File.separator + "flow-" + this.uuid);
		
		this.world = world;
			
			Location tp = null;
			
			for (Entry<UUID, Location[]> entry : this.recording.getFrames().entrySet()) {
				
				UUID uuid = entry.getKey();
				Location[] frames = entry.getValue();
				
				for (Location loc : frames) {
					
					loc.setWorld(world);
					
					if (tp == null && uuid.equals(this.recording.getSuspect().getUniqueId()))
						tp = loc;
						
				}
				
			}
			
			if (tp == null)
				throw new UnsupportedOperationException("Could not find appropriate spawn location for watchers.");
			
			for (FleXPlayer watcher : watchers)
				watcher.teleport(tp);
			
		}
		
		this.runTaskTimerAsynchronously(Fukkit.getInstance(), 10L, 2L);
		
	}
	
	public Set<FleXPlayer> getWatchers() {
		return this.watchers;
	}
	
	public RecordingData getRecording() {
		return this.recording;
	}
	
	public World getWorld() {
		return this.world;
	}

	@Override
	public void run() {
		
		if (this.isWatching()) {
			
			for (FleXPlayer watcher : this.watchers) {
				if (!watcher.isOnline() || !watcher.getPlayer().getWorld().getUID().equals(this.getWorld().getUID())) {
					this.end();
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
			
			for (Entry<UUID, Location[]> entry : this.recording.getFrames().entrySet()) {
				
				UUID uuid = entry.getKey();
				Location[] frames = entry.getValue();
				
				for (Location loc : frames) {
					
					loc.setWorld(world);
					
					if (tp == null && uuid.equals(this.recording.getSuspect().getUniqueId()))
						tp = loc;
						
				}
				
			}
			for (PreRecorded r : frames.entrySet())
				if (r instanceof Recorded)
					((Recorded)r).teleport(r.getFrames().get(this.tick));
			
		} else {
			
			FleXPlayer suspect = this.recording.getSuspect();
			
			if (!suspect.isOnline() || suspect.getState() != PlayerState.INGAME) {

				System.out.println("===========================================================");
				System.out.println("===========================================================");
				System.out.println("===========================================================");
				System.out.println(suspect.getName() + ": state=" + suspect.getState());
				System.out.println(suspect.getName() + ": online=" + suspect.isOnline());
				System.out.println("===========================================================");
				System.out.println("===========================================================");
				System.out.println("===========================================================");
				
				this.recording.getReports().stream().forEach(re -> {
					
					if (re.getBy().isOnline())
						re.getBy().sendMessage(ThemeMessage.FLOW_RECORDING_INTERRUPTED.format(re.getBy().getTheme(), re.getBy().getLanguage(), ThemeUtils.getNameVariables(re.getPlayer(), re.getBy().getTheme())));
					
				});
				
				this.end(!suspect.isOnline() ? "Suspect has gone offline." : "Suspect is not INGAME.");
				return;
				
			}
			
			if (this.pause)
				return;
			
			for (PreRecorded r : recorded)
				r.getFrames().put(this.tick, r.toPlayer().getLocation());
			
			if (this.tick == 300/* Stops after 30 seconds. *) {
				this.end("End of recording.");
				return;
			}
			
		}
		
		this.tick++;
		
	}
	
	public void end(String... reason) {
		
		if (reason == null || reason.length == 0)
			reason = new String[]{ "No further information." };
		
		if (this.isWatching()) {
			
			System.out.println("Stopping stage: " + reason[0]);
			
			this.cancel();
			
			for (FleXPlayer watcher : this.watchers) {
				
				if (watcher.isOnline())
					watcher.kick("The stage has closed: " + reason[0]);
					
			}
			
		} else {
			
			Fukkit.getFlowLineEnforcementHandler().setRecording(this.recording.getSuspect(), false);
			
			if (reason[0].equals("End of recording.")) {
				
				this.recording.setTag("Length", this.tick);
				
				this.recording.write((HashSet<PreRecorded>)this.recording.getRecorded());
				
			}
			
			System.out.println("Stopping recording: " + reason[0]);
			
			this.cancel();
			
		}
		
	}
	
	public boolean isWatching() {
		return !this.watchers.isEmpty();
	}
	
}
