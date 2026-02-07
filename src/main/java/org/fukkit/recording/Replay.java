package org.fukkit.recording;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.fukkit.Fukkit;
import org.fukkit.entity.FleXBot;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.flow.ReplayCompleteEvent;
import org.fukkit.event.flow.ReplayEndEvent;
import org.fukkit.event.flow.ReplayJoinEvent;
import org.fukkit.event.flow.ReplayLeaveEvent;
import org.fukkit.event.flow.ReplayStartEvent;
import org.fukkit.flow.OverwatchReplay;
import org.fukkit.recording.loadout.ReplayLoadout;
import org.fukkit.theme.Theme;
import org.fukkit.utils.VersionUtils;
import org.fukkit.utils.WorldUtils;

import io.flex.commons.Nullable;
import io.flex.commons.sql.SQLCondition;
import io.flex.commons.sql.SQLDatabase;
import io.flex.commons.sql.SQLRowWrapper;
import io.flex.commons.utils.FileUtils;
import io.flex.commons.utils.NumUtils;

public class Replay extends Recording {
	
	protected Location spawn;
	
	private Map<FleXPlayer, UUID> watchersAndTranscripts = new LinkedHashMap<FleXPlayer, UUID>();
	
	private ReplayListeners listener;
	
	private UUID host;
	
	private double speed = 1.0;
	
	private boolean reverse = false, joined = false;
	
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
		return this.host != null ? Fukkit.getPlayer(this.host) : null;
	}
	
	public Set<FleXPlayer> getWatchersUnsafe() {
		return this.watchersAndTranscripts.keySet().stream().collect(Collectors.toSet());
	}
	
	public UUID getTranscript(FleXPlayer watcher) {
		return this.watchersAndTranscripts.get(watcher);
	}
	
	public double getSpeed() {
		return NumUtils.roundToDecimal(this.speed, 2);
	}
	
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	public boolean isWatching(Entity entity) {
		return this.watchersAndTranscripts.keySet().stream().anyMatch(p -> p.getUniqueId().equals(entity.getUniqueId()));
	}
	
	public void setTranscript(FleXPlayer watcher, UUID transcript) {
		this.watchersAndTranscripts.put(watcher, transcript);
	}
	
	public void onJoin(FleXPlayer player) {
		
		Objects.requireNonNull(player, "player cannot be null");
		
		if (player instanceof FleXBot)
			return;
		
		ReplayJoinEvent event = new ReplayJoinEvent(this, player);
		
		Fukkit.getEventFactory().call(event);
		
		if (event.isCancelled())
			return;
		
		UUID transcript = null;
		
		if (this instanceof OverwatchReplay)
			transcript = ((OverwatchReplay)this).getSuspect().getUniqueId();
		
		if (!this.isWatching(player.getPlayer()))
	        this.watchersAndTranscripts.put(player, transcript);
		
		else if (transcript != null)
			this.setTranscript(player, transcript);
		
		if (this.watchersAndTranscripts.size() == 1)
			this.host = player.getUniqueId();
		
		this.joined = true;
		
		player.teleport(this.spawn);
		
		Player pl = player.getPlayer();
		
		pl.setAllowFlight(true);
		pl.setFlying(true);
		
		Theme theme = player.getTheme();
		
		UUID ts = this.watchersAndTranscripts.get(player);
		
		player.setLoadout(new ReplayLoadout(player, this), true);
		
		if (ts == null)
			player.sendMessage(theme.format("<flow><pc>Not currently displaying a transcript<pp>."));
		
		else player.sendMessage(theme.format("<flow><pc>You are seeing<reset> <sc>" + this.getRecorded().get(ts).toPlayer().getDisplayName(theme, true) + "<pc>'s complete chat log<pp>."));
		
		player.sendMessage(theme.format("<flow><pc>View player recieved messages by right clicking them<pp>."));
		
	}
	
	public void onLeave(FleXPlayer player) {
		
		Objects.requireNonNull(player, "player cannot be null");
		
		if (player instanceof FleXBot)
			return;
		
		Iterator<Entry<FleXPlayer, UUID>> it = this.watchersAndTranscripts.entrySet().iterator();
		
		while(it.hasNext())
			if (it.next().getKey().getUniqueId().equals(player.getUniqueId()))
				it.remove();
		
		ReplayLeaveEvent event = new ReplayLeaveEvent(this, player);
		
		Fukkit.getEventFactory().call(event);
		
		if (event.isCancelled())
			throw new UnsupportedOperationException("cancelling ReplayLeaveEvent does nothing.");
		
	}
	
	@Override
	public void run() {
		
		if (!this.joined)
			return;
		
		for (FleXPlayer fp : this.watchersAndTranscripts.keySet()) {
			
			if (fp == null)
				continue;
			
			if (!fp.isOnline() || !fp.getPlayer().getWorld().getUID().equals(this.world.getUID())) {
				this.onPlayerDisconnect(fp);
				continue;
			}
			
			if (this.tick % 5 == 0)
				this.world.playEffect(fp.getLocation().clone().add(NumUtils.getRng().getDouble(-0.3, 0.3), 0.7, NumUtils.getRng().getDouble(-0.3, 0.3)), Effect.MOBSPAWNER_FLAMES, 0);
			
		}
		
		if (this.pause)
			return;
		
		if (this.tick == (this.reverse ? 0 : this.getLength())) {
			
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
						
						if (action == RecordedAction.LAUNCH_PROJECTILE) {
							
							ItemStack item = frame.getItem();
							
							Class<? extends Projectile> clazz = null;
							
							switch (item.getType()) {
							case ARROW:
								
								clazz = Arrow.class;
								break;
								
							case ENDER_PEARL:
								
								clazz = EnderPearl.class;
								break;
								
							case EGG:

								clazz = Egg.class;
								break;
								
							default:
								
								Material sb = VersionUtils.material("SNOWBALL", "SNOW_BALL", "LEGACY_SNOW_BALL");
								Material es = VersionUtils.material("EYE_OF_ENDER", "ENDER_EYE", "LEGACY_EYE_OF_ENDER");
								
								if (item.getType() == es)
									clazz = null; // Not supported. (for now?) :(
								
								if (item.getType() == sb)
									clazz = Snowball.class;
								
								break;
							}
							
							if (clazz != null) {
								
								Projectile projectile = bot.getPlayer().launchProjectile(clazz);
								
								if (projectile instanceof Arrow && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
									
									String[] split = item.getItemMeta().getDisplayName().split(",");
									
									if (split.length == 4) {
										
										try {
											
											double x = Double.parseDouble(split[0]);
											double y = Double.parseDouble(split[1]);
											double z = Double.parseDouble(split[2]);

											Vector vector = new Vector(x, y, z);
											
											boolean critical = Boolean.valueOf(split[3]);
											
											((Arrow)projectile).setVelocity(vector);
											((Arrow)projectile).setCritical(critical);
											
										} catch (IndexOutOfBoundsException | NumberFormatException ignore) {}
										
									}
									
								}
								
							}
							
							continue;
							
						}
						
						((CraftRecorded)recordable).getActor().playAction(action);
						
					}
				}
			
				if (location != null)
					bot.teleport(location);
				
				for (Entry<FleXPlayer, UUID> ts : this.watchersAndTranscripts.entrySet()) {
					
					UUID uid = ts.getValue();
					
					if (uid != null && uid.equals(recordable.getUniqueId())) {
						
						String message = frame.getMessage();
						
						if (message != null) {
							
							FleXPlayer reader = ts.getKey();
							
							if (reader != null && reader.isOnline()) {
								
								Theme theme = reader.getTheme();
								
								reader.sendMessage(theme.format("<pp>[<pv>" + frame.getTimeStamp() + "<pp>]<reset> ") + message);
								
							}
							
						}
						
					}
					
				}
				
			}
			
		}
		
		if (this.reverse)
			this.tick--;
		
		else this.tick++;
		
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
			this.onJoin(watcher);
		
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
		
		List<FleXPlayer> all = new ArrayList<FleXPlayer>(this.watchersAndTranscripts.keySet());
		
		for (FleXPlayer watcher : all)
			if (watcher != null && watcher.isOnline())
				this.onLeave(watcher);
		
		this.destroy();
		
	}
	
	@Override
	public void destroy() {
		
		if (this.world != null) {
			WorldUtils.unloadWorld(this.world, false);
			FileUtils.delete(this.world.getWorldFolder());
		}
		
		if (this.watchersAndTranscripts != null)
			this.watchersAndTranscripts.clear();
		
		if (this.listener != null)
			this.listener.unregister();
		
		this.listener = null;
		this.watchersAndTranscripts = null;
		this.spawn = null;
		
		super.destroy();
		
	}
	
	public boolean isReversed() {
		return this.reverse;
	}
	
	public boolean isPlaying() {
		return !this.watchersAndTranscripts.isEmpty();
	}

	@Override
	public void onPlayerDisconnect(FleXPlayer player) {
		
		this.onLeave(player);
		
		if (player.getUniqueId().equals(this.host))
			this.end("Host has disconnected.");
		
	}

	@Override
	public void onComplete() {
		this.resart();
	}
	
	public void play(boolean reverse) {
		this.pause = false;
		this.reverse = reverse;
	}
	
	public void play() {
		this.pause = false;
	}
		
	public void pause() {
		this.pause = true;
	}
	
	public void resart() {
		
		this.getRecorded().forEach((k, v) -> {
			
			if (k.equals(Recordable.SYSTEM_UID))
				return;
			
			v.setHelmet(null);
			v.setChestplate(null);
			v.setLeggings(null);
			v.setBoots(null);
			
		});
		
		for (FleXPlayer watcher : this.watchersAndTranscripts.keySet()) {
			
			if (watcher != null && watcher instanceof FleXPlayer)
				watcher.sendMessage(watcher.getTheme().format("<flow><pc>Restarting recording<pp>..."));
			
		}
		
		this.tick = 0;
		
		this.pause = false;
		
	}
	
}
