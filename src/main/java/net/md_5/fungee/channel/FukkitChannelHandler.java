package net.md_5.fungee.channel;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.messaging.Messenger;
import org.fukkit.Fukkit;
import org.fukkit.FukkitRunnable;
import org.fukkit.entity.FleXBot;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.FleXEventListener;
import org.fukkit.event.channel.ChannelMessageReceivedEvent;
import org.fukkit.event.channel.ChannelMessageSendEvent;
import org.fukkit.event.player.FleXPlayerLoadEvent;
import org.fukkit.event.player.FleXPlayerUpdateEvent;
import org.fukkit.theme.Theme;
import org.fukkit.utils.BukkitUtils;
import org.fukkit.utils.ChatUtils;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import io.flex.commons.utils.ClassUtils;
import io.flex.commons.utils.CollectionUtils;

import net.md_5.fungee.Memory;
import net.md_5.fungee.ProtocolVersion;

public class FukkitChannelHandler extends FleXEventListener implements ChannelHandler {

	private static final Map<UUID, Map<String, String>> CACHE = new HashMap<UUID, Map<String, String>>();
	
	@EventHandler(priority = EventPriority.LOW)
	public void event(FleXPlayerLoadEvent event) {
		
		if (event.isOffline())
			return;
		
		FleXPlayer player = event.getPlayer();
		
		if (player instanceof FleXBot)
			return;
		
		new FukkitRunnable() {
			
			private int attempt = 0;
			
			@Override
			public void execute() {

				this.attempt++;
				
				if (!player.isOnline()) {
					
					this.cancel();
					return;
					
				}
				
				if (this.attempt == 5) {
					
					Theme theme = player.getTheme() != null ? player.getTheme() : org.fukkit.Memory.THEME_CACHE.getDefaultTheme();
					
					BukkitUtils.mainThread(() -> {
						
						if (player.isOnline())
							player.kick(theme.format("<failure>Failed to load version: Player updater time out, please login again..."));
						
					});
					
					this.cancel();
					return;
					
				}
				
				UUID uid = event.getPlayer().getUniqueId();
				
				Map<String, String> entries = CACHE.get(uid);
				
				FleXPlayer player = event.getPlayer();
				
				if (entries == null || entries.isEmpty()) {
					
					System.err.println(player.getName() + "'s FleXPlayer loaded but the connection could not be resolved, retrying... (" + this.attempt + ")");
					return;
					
				}
				
				if (entries.containsKey("Version")) {

					String ver = entries.get("Version");
					
					if (ClassUtils.canParseAsInteger(ver)) {
						
						ProtocolVersion version = ProtocolVersion.fromProtocol(Integer.parseInt(ver));
						
						if (version == null)
							return;
						
						player.setVersion(version);
						
					}
					
				} else {
					
					System.err.println(player.getName() + "'s FleXPlayer loaded but the client version could not be found, retrying... (" + this.attempt + ")");
					return;
					
				}
				
				if (entries.containsKey("Domain")) {
					
					String dom = entries.get("Domain");
					
					BukkitUtils.mainThread(() -> {
						
						if (player.isOnline())
							player.setMetadata("domain", new FixedMetadataValue(Fukkit.getInstance(), dom));
						
						Fukkit.getEventFactory().call(new FleXPlayerUpdateEvent(player));
						
					});
					
				} else {
					
					System.err.println(player.getName() + "'s FleXPlayer loaded but the client version could not be found, retrying... (" + this.attempt + ")");
					return;
					
				}
				
				String target = entries.get("Server_Target");
				String from = entries.get("Server_From");
				
				boolean change = entries.containsKey("Server_Change");
				
				String connection = "-x-";
				
				if (change && target != null && from != null)
					connection = from = " -> " + target;
				
				if (target != null)
					connection = " -> " + target;
				
				if (from != null)
					connection = from + " -x";
				
				String con = connection;
				
				player.getHistoryAsync(history -> history.getConnections().add(con), null);
				
				CACHE.remove(uid);
				
				this.cancel();
				
			}
			
		}.runTaskTimerAsynchronously(0L, 20L);
		
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void event(PlayerLoginEvent event) {
		
		Map<String, String> entries = CACHE.get(event.getPlayer().getUniqueId());
		
		if (entries == null || entries.isEmpty())
			return;
		
		if (entries.containsKey("Domain")) {
			
			String dom = entries.get("Domain");
			
			event.getPlayer().setMetadata("domain", new FixedMetadataValue(Fukkit.getInstance(), dom));
			
		}
		
	}
	
	public FukkitChannelHandler() {
		
		Memory.CHANNEL_CACHE.add(new Channel("PlayerUpdater") {
			
			@Override
			public void onReceive(Map<String, String> entries) {
				
				if (entries.containsKey("UUID")) {
					
					UUID uid = UUID.fromString(entries.get("UUID"));
					
					Map<String, String> cached = CACHE.get(uid);
					
					if (cached != null)
						cached.forEach((k, v) -> {
							entries.put(k, v);
						});
					
					CACHE.put(uid, entries);
					
					/**
					 * Timeout...
					 */
					new FukkitRunnable() {
						
						private String time = entries.get("Time");
						
						@Override
						public void execute() {
							
							if (CACHE.containsKey(uid) && CACHE.get(uid).get("Time").equals(this.time))
								CACHE.remove(uid);
							
						}
						
					}.runTaskLaterAsynchronously(1200L);
					
				}
				
			}
			
		});
		
		Memory.CHANNEL_CACHE.add(new Channel("PlayerChat") {
			
			@Override
			public void onReceive(Map<String, String> entries) {
				
				String uid = entries.get("UUID");
				
				FleXPlayer player = Fukkit.getPlayer(UUID.fromString(uid));
				
				if (player != null) {
					
					String message = entries.get("Message");
					String origin = entries.get("MessageOrigin");
					
					Collection<String> uids = CollectionUtils.toCollection(entries.get("Mentions"));
					
					Set<FleXPlayer> mentions = uids.stream().map(u -> Fukkit.getPlayer(UUID.fromString(u))).filter(p -> p != null && p.isOnline()).collect(Collectors.toSet());
					
					Set<FleXPlayer> recipients = new HashSet<FleXPlayer>();
					
					Bukkit.getOnlinePlayers().forEach(p -> {
						
						FleXPlayer fp = Fukkit.getPlayerExact(p);
						
						if (fp == null || !fp.isOnline())
							return;
						
						recipients.add(fp);
						
					});
					
					BukkitUtils.asyncThread(() -> ChatUtils.sendChat(player, player.isMasked() ? player.getMask() : player.getRank(), message, origin, mentions, recipients));
					
				}
				
			}
			
		});
		
		Memory.CHANNEL_CACHE.add(new Channel("ServerUpdater") {
			
			@Override
			public void onReceive(Map<String, String> entries) {
				Fukkit.getServerHandler().setName(entries.get("Name"));
			}
			
		});
		
		Fukkit instance = Fukkit.getInstance();
		
		Messenger messenger = instance.getServer().getMessenger();
		
		if (!messenger.getOutgoingChannels().contains(Channel.FLEX))
			messenger.registerOutgoingPluginChannel(instance, Channel.FLEX);
		
		if (!messenger.getIncomingChannels().contains(Channel.FLEX))
			messenger.registerIncomingPluginChannel(instance, Channel.FLEX, (channel, player, message) -> {
				
				if (!channel.equals(Channel.FLEX))
		            return;
				
				ByteArrayDataInput input = ByteStreams.newDataInput(message);
				
				String name = input.readUTF();
				
				Memory.CHANNEL_CACHE.stream().forEach(c -> {
					
					if (!name.equals(c.getName()))
						return;
					
			        try {
			        	
			        	Map<String, String> entries = new HashMap<String, String>();
			        	
			        	String next, key = null, value = null;
			        	
			        	while (true) {
			        		
			        		try {
			        			next = input.readUTF();
							} catch (Exception e) {
								break;
							}
			        		
			        		if (key != null)
			        			value = next;
			        		
			        		else key = next;
			        		
			        		if (key != null && value != null) {
			        			
			        			entries.put(key, value);
			        			
			        			key = null;
			        			value = null;
			        			
			        		}
							
						}
			        	
			        	ChannelMessageSendEvent event = new ChannelMessageSendEvent(c, entries);
			        	
			        	Fukkit.getEventFactory().call(event);
			        	
			    		/**
			    		 * player.sendData never worked well, so no point using the player object.
			    		 */
			        	if (!event.isCancelled())
			        		c.onReceive(entries);
			        	
			        	Fukkit.getEventFactory().call(new ChannelMessageReceivedEvent(c, event));
						
					} catch (Exception e) {
						
						e.printStackTrace();
						
						BukkitUtils.runLater(() -> {
							
							if (player != null)
								player.sendMessage(ChatColor.RED + "An error occurred receiving data from FleX. A stacktrace has been printed to console for Administration to review.");
							
						});
						
						throw e;
						
					}
					
				});
				
			});
		
		
	}

	@Override
	public void sendMessage(FleXPlayer sender, byte[] send, String... servers) {
		(sender != null ? sender.getPlayer() : Bukkit.getServer()).sendPluginMessage(Fukkit.getInstance(), Channel.FLEX, send);
	}

}
