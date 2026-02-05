package org.fukkit.recording;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.fukkit.Fukkit;
import org.fukkit.entity.FleXBot;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.flow.ReplayCompleteEvent;
import org.fukkit.event.flow.ReplayEndEvent;
import org.fukkit.event.flow.ReplayStartEvent;
import org.fukkit.event.flow.ReplayWatchEvent;
import org.fukkit.theme.Theme;
import org.fukkit.utils.WorldUtils;

import io.flex.commons.Nullable;
import io.flex.commons.sql.SQLCondition;
import io.flex.commons.sql.SQLDatabase;
import io.flex.commons.sql.SQLRowWrapper;
import io.flex.commons.utils.FileUtils;
import io.flex.commons.utils.NumUtils;

public class Replay extends Recording {
	
	protected Location spawn;
	
	private List<FleXPlayer> watchers = new LinkedList<FleXPlayer>();
	
	private UUID transcript = null;
	
	private ReplayListeners listener;
	
	public Replay(File container) throws SQLException {
		
		super(container, null);
		
		if (this.length < 1)
			throw new UnsupportedOperationException("invalid recording length");
			
		LinkedHashMap<UUID, String[]> recorded = (LinkedHashMap<UUID, String[]>) this.getData().read();
		
		if (recorded == null || recorded.isEmpty())
			throw new UnsupportedOperationException("recorded data is empty");
		
		for (Entry<UUID, String[]> entry : recorded.entrySet()) {
			
			UUID uuid = entry.getKey();
			
			String[] actions = entry.getValue();
			
			LinkedHashMap<Long, Frame> frames = new LinkedHashMap<Long, Frame>();
			
			for (int i = 0; i < actions.length; i++)
				frames.put((long)i, Frame.from(actions[i]));
			
			CraftRecorded actor = CraftRecorded.of(Fukkit.getPlayer(uuid), frames);
			
			// Update uniqueId for bots.
			uuid = actor.getUniqueId();
			
			this.getRecorded().put(uuid, actor);
			
		}
		
		if (this.getRecorded().isEmpty())
			throw new UnsupportedOperationException("recorded data is empty");
		
	}
	
	public static Replay download(File destination, @Nullable RecordingContext context) throws SQLException, IOException {
		return download(destination, context, true);
	}
	
	public static Replay download(File destination, @Nullable RecordingContext context, boolean overwrite) throws SQLException, IOException {
		
		String name = destination.getName();
		
		boolean exists = destination.exists();
		
		if (!overwrite && exists)
			throw new FileAlreadyExistsException("file \"" + name + "\" already exists at destination path, use the overwrite parameter to clear the existing recording");
		
		SQLDatabase base = Fukkit.getConnectionHandler().getDatabase();
		
		Set<SQLRowWrapper> rows = context != null ? base.getRows(name, SQLCondition.where("context").is(context.toString())) : base.getRows("flex_recording");
		
		SQLRowWrapper row = rows.stream().filter(r -> r != null && r.getString("uuid") != null && r.getString("uuid").contains(name)).findAny().orElse(null);
		
		if (row == null)
			return null;
		
		// Only deletes if row is found.
		if (overwrite && exists)
			FileUtils.delete(destination);
		
		if (row.getString("state").equals(RecordingState.ERROR.name())) {
			
			String error;
			
			try {
				error = new String((byte[]) row.getByteArray("data"), StandardCharsets.UTF_8);
			} catch (Exception e) {
				error = "No further information";
			}
			
			throw new IOException("error uploading recording: " + error);
			
		}
		
		if (!row.getString("state").equals(RecordingState.COMPLETE.name()))
			throw new IOException("recording is not complete");
		
		File recordings = destination.getParentFile();
		
		if (recordings != null)
			recordings.mkdirs();
		
	    File zip = new File(recordings.getAbsolutePath(), destination.getName() + ".zip");
	    
	    byte[] data = row.getByteArray("data");
	    
	    try (FileOutputStream fos = new FileOutputStream(zip)) {
	        fos.write(data);
	    }
	    
	    File unzipped = FileUtils.unzip(zip, recordings.getAbsolutePath());
	    
		return new Replay(unzipped);
		
	}
	
	public FleXPlayer getHost() {
		return !this.watchers.isEmpty() ? this.watchers.get(0) : null;
	}
	
	public Set<FleXPlayer> getWatchersUnsafe() {
		return this.watchers.stream().collect(Collectors.toSet());
	}
	
	public boolean isWatching(Entity entity) {
		return this.watchers.stream().anyMatch(p -> p.getUniqueId().equals(entity.getUniqueId()));
	}
	
	public void setTranscript(FleXPlayer player) {
		this.transcript = player.getUniqueId();
	}
	
	public void addWatcher(FleXPlayer player) {
		
		if (player instanceof FleXBot)
			return;
		
		ReplayWatchEvent event = new ReplayWatchEvent(this, player);
		
		Fukkit.getEventFactory().call(event);
		
		if (event.isCancelled())
			return;
		
		if (this.watchers.stream().noneMatch(w -> player.getUniqueId().equals(w.getUniqueId())))
	        this.watchers.add(player);
		
		player.teleport(this.spawn);
		
		Player pl = player.getPlayer();
		
		pl.setAllowFlight(true);
		pl.setFlying(true);
		
		Theme theme = player.getTheme();
		
		if (this.transcript == null)
			player.sendMessage(theme.format("<flow><pc>Not currently displaying a transcript<pp>."));
		
		else player.sendMessage(theme.format("<flow><pc>You are seeing<reset> <sc>" + this.getRecorded().get(this.transcript).toPlayer().getDisplayName(theme, true) + "<pc>'s complete chat log<pp>."));
		
		player.sendMessage(theme.format("<flow><pc>View player recieved messages by right clicking them<pp>."));
		
	}

	@Override
	public void run() {
		
		for (FleXPlayer watcher : this.watchers) {
			
			if (watcher == null)
				continue;
			
			if (!watcher.isOnline() || !watcher.getPlayer().getWorld().getUID().equals(this.world.getUID())) {
				this.onPlayerDisconnect(watcher);
				return;
			}
			
			if (this.tick % 3 == 0)
				this.world.playEffect(watcher.getLocation().clone().add(NumUtils.getRng().getDouble(-0.3, 0.3), 1, NumUtils.getRng().getDouble(-0.3, 0.3)), Effect.MOBSPAWNER_FLAMES, 0);
			
		}
		
		if (this.pause)
			return;
		
		if (this.tick == this.getLength()) {
			
			ReplayCompleteEvent event = new ReplayCompleteEvent(this);
			
			Fukkit.getEventFactory().call(event);
			
			if (event.isCancelled())
				return;
			
			this.onComplete();
			return;
			
		}
		
		for (Recordable recordable : this.getRecorded().values()) {
			
			Map<Long, Frame> frames = recordable.getFrames();
			
			if ((int)this.tick < frames.size()) {
				
				Frame frame = frames.get(this.tick);
				
				Location location = frame.getLocation();
				
				RecordedAction[] actions = frame.getActions();
				
				FleXBot bot = ((CraftRecorded)recordable).getActor();
				
				if (actions != null && actions.length > 0) {
					for (RecordedAction action : actions) {
						
						if (action == RecordedAction.NONE)
							continue;
						
						if (action == RecordedAction.MOVE)
							continue;
						
						if (action == RecordedAction.EQUIP_HAND) {
							bot.setItemInHand(frame.getItem());
							continue;
						}
						
						if (action == RecordedAction.EQUIP_HELMET) {
							bot.setHelmet(frame.getItem());
							continue;
						}
						
						if (action == RecordedAction.EQUIP_CHESTPLATE) {
							bot.setChestplate(frame.getItem());
							continue;
						}
						
						if (action == RecordedAction.EQUIP_LEGGINGS) {
							bot.setLeggings(frame.getItem());
							continue;
						}
						
						if (action == RecordedAction.EQUIP_BOOTS) {
							bot.setBoots(frame.getItem());
							continue;
						}
						
						if (action == RecordedAction.DROP) {
							this.world.dropItem(bot.getLocation(), frame.getItem());
							continue;
						}
						
						if (action == RecordedAction.PICKUP) {
							
							ItemStack item = frame.getItem();
							
							Collection<Entity> entities = this.world.getNearbyEntities(location, 1, 1, 1);
							
							for (Entity e : entities)
								if (e instanceof Item && ((Item)e).getItemStack().equals(item))
									e.remove();
							
							continue;
							
						}
						
						((CraftRecorded)recordable).getActor().playAction(action);
						
					}
				}
			
				if (location != null)
					bot.teleport(location);
				
				if (this.transcript != null) {
					
					if (recordable.getUniqueId().equals(this.transcript)) {
						
						String message = frame.getMessage();
						
						if (message != null) {
							for (FleXPlayer watcher : this.watchers) {
								
								if (watcher != null && watcher instanceof FleXPlayer) {
									
									Theme theme = watcher.getTheme();
									
									watcher.sendMessage(theme.format("<pp>[<pv>" + frame.getTimeStamp() + "<pp>]<reset> ") + message);
									
								}
								
							}
						}
						
					}
					
				}
				
			}
			
		}
		
		this.tick++;
		
	}
	
	@Override
	public void start(World world, int duration, FleXPlayer... watchers) {
		
		if (world == null)
			throw new UnsupportedOperationException("world must not be null");
		
		long request = (long) (duration * (20.0 / TICK_RATE));
		
		if (request < this.length)
			this.length = request;
		
		if (this.length <= 0)
			throw new UnsupportedOperationException("length must be more than 0");
		
		this.world = world;
		
		for (Recordable recordable : this.getRecorded().values()) {
			for (Frame frame : recordable.getFrames().values()) {
				
				Location loc = frame.getLocation();
				
				if (loc != null) {
					
					loc.setWorld(world);
					
					if (this.spawn == null)
						this.spawn = loc;
					
					FleXBot bot = ((CraftRecorded)recordable).getActor();
					
					if (!bot.isOnline()) {
						bot.teleport(loc);
						bot.getAI().setGravity(false);
					}
					
				}
				
			}
		}
		
		if (this.spawn == null)
			throw new UnsupportedOperationException("Could not find appropriate spawn location for watchers");
		
		for (FleXPlayer watcher : watchers)
			this.addWatcher(watcher);
		
		this.pause = false;
		
		this.listener = new ReplayListeners(this);
		
		ReplayStartEvent event = new ReplayStartEvent(this);
		
		Fukkit.getEventFactory().call(event);
		
		if (event.isCancelled())
			return;
		
		this.runTaskTimer(Fukkit.getInstance(), 120L, TICK_RATE);
		
	}

	@Override
	public void end() {
		this.end(null);
	}

	@Override
	public void end(@Nullable String reason) {
		
		if (!Bukkit.isPrimaryThread())
			throw new IllegalStateException("Replay end must be called synchronously.");
			
		String parse = reason == null ? "No further information." : reason;
		
		ReplayEndEvent event = new ReplayEndEvent(this, parse);
		
		if (event.isCancelled())
			return;
		
		Fukkit.getEventFactory().call(event);
		
		System.out.println("Stopping stage: " + parse);
		
		this.cancel();
		
		List<FleXPlayer> all = new ArrayList<FleXPlayer>(this.watchers);
		
		for (FleXPlayer watcher : all) {
			
			if (watcher != null && watcher.isOnline())
				try {
					watcher.kick("The stage has closed: " + parse);
				} catch (IllegalStateException ignore) {}
				
		}
		
		this.destroy();
		
	}
	
	@Override
	public void destroy() {
		
		if (this.world != null) {
			WorldUtils.unloadWorld(this.world, false);
			FileUtils.delete(this.world.getWorldFolder());
		}
		
		if (this.watchers != null)
			this.watchers.clear();
		
		if (this.listener != null)
			this.listener.unregister();
		
		this.listener = null;
		this.watchers = null;
		this.spawn = null;
		
		super.destroy();
		
	}
	
	public boolean isPlaying() {
		return !this.watchers.isEmpty();
	}

	@Override
	public void onPlayerDisconnect(FleXPlayer player) {
		
		if (player.getUniqueId().equals(this.getHost().getUniqueId()))
			this.end("Host has disconnected.");
		
	}

	@Override
	public void onComplete() {
		
		this.getRecorded().forEach((k, v) -> {
			
			if (k.equals(Recordable.SYSTEM_UID))
				return;
			
			v.setHelmet(null);
			v.setChestplate(null);
			v.setLeggings(null);
			v.setBoots(null);
			
		});
		
		for (FleXPlayer watcher : this.watchers) {
			
			if (watcher != null && watcher instanceof FleXPlayer)
				watcher.sendMessage(watcher.getTheme().format("<flow><pc>Restarting recording<pp>..."));
			
		}
		
		this.tick = 0;
		
		this.pause = false;
		
	}
	
}
