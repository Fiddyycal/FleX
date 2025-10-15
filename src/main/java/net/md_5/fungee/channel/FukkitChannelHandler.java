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
import org.bukkit.plugin.messaging.Messenger;
import org.fukkit.Fukkit;
import org.fukkit.cache.PlayerCache;
import org.fukkit.cache.PlayerCache.PlayerCacheMeta;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.FleXEventListener;
import org.fukkit.event.channel.ChannelMessageReceivedEvent;
import org.fukkit.event.channel.ChannelMessageSendEvent;
import org.fukkit.utils.BukkitUtils;
import org.fukkit.utils.ChatUtils;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import io.flex.commons.utils.ClassUtils;
import io.flex.commons.utils.CollectionUtils;

import net.md_5.fungee.Memory;
import net.md_5.fungee.ProtocolVersion;

public class FukkitChannelHandler extends FleXEventListener implements ChannelHandler {
	
	public FukkitChannelHandler() {
		
		Memory.CHANNEL_CACHE.add(new Channel("PlayerUpdater") {
			
			@Override
			public void onReceive(Map<String, String> entries) {
				
				if (entries == null || entries.isEmpty())
					return;
				
				if (entries.containsKey("UUID")) {
					
					UUID uid = UUID.fromString(entries.get("UUID"));
					
					PlayerCacheMeta meta = PlayerCache.getCachedAttributes(uid);
					
					if (entries.containsKey("Version")) {
						
						String ver = entries.get("Version");
						
						if (ClassUtils.canParseAsInteger(ver)) {
							
							ProtocolVersion version = ProtocolVersion.fromProtocol(Integer.parseInt(ver));
							
							if (version == null)
								return;
							
							meta.setVersion(version);
							
						}
						
					}
					
					if (entries.containsKey("Domain")) {
						
						String domain = entries.get("Domain");
						
						meta.setDomain(domain);
						
					}
					
					// If player happens to be loaded already.
					FleXPlayer player = Fukkit.getCachedPlayer(uid);
					
					if (player != null)
						player.update();
					
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
