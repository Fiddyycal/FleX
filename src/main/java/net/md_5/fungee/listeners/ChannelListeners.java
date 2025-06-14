package net.md_5.fungee.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import io.flex.commons.utils.CollectionUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.fungee.FungeeCord;
import net.md_5.fungee.Memory;
import net.md_5.fungee.channel.Channel;
import net.md_5.fungee.utils.NetworkUtils;

public class ChannelListeners implements Listener {
	
	public ChannelListeners() {
		
		ProxyServer.getInstance().getPluginManager().registerListener(FungeeCord.getInstance(), this);
		
		ProxyServer.getInstance().registerChannel(Channel.FLEX);
		
	}
	
	@EventHandler
    public void event(PluginMessageEvent event) {
		
		String channel = event.getTag();
		
        if (channel.equalsIgnoreCase(Channel.FLEX)) {
        	
        	ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());
			
        	String name = input.readUTF();
        	
			Map<String, String> entries = new HashMap<String, String>();
        	
        	String next, key = null, value = null;
			
			String server = null;
			
			if (event.getSender() instanceof Server)
				server = ((Server) event.getSender()).getInfo().getName();
			
			if (event.getSender() instanceof ProxiedPlayer)
				server = ((ProxiedPlayer)event.getSender()).getServer().getInfo().getName();
			
        	while (true) {
        		
        		try {
        			next = input.readUTF();
				} catch (Exception e) {
					break;
				}
        		
        		if (key != null) {
        			
        			if (key.equals("MessageOrigin")) {
        				value = server != null ? server : next;
        				
        			} else value = next;
        			
        		}
        		
        		else key = next;
        		
        		if (key != null && value != null) {
        			
        			entries.put(key, value);
        			
        			key = null;
        			value = null;
        			
        		}
				
			}
        	
        	String destination = entries.get("MessageDestination");
        	
        	boolean queue = Boolean.valueOf(entries.get("Queue"));
        	
        	Collection<String> servers;
        	
        	switch (destination) {
        	
			case NetworkUtils.NONE:
				return;
				
			case NetworkUtils.ALL:
				
				servers = ProxyServer.getInstance().getServers().keySet();
				break;
				
			case NetworkUtils.ALL_OTHER:
				
				Set<String> destinations = new HashSet<String>(ProxyServer.getInstance().getServers().keySet());
				
				destinations.remove(server);
				
				servers = destinations;
				break;
				
			default:
				
				servers = CollectionUtils.toCollection(destination);
				break;
				
			}
        	
        	if (name.equals("PlayerChat"))
        		servers.removeIf(s -> ProxyServer.getInstance().getServers().get(s).getPlayers().size() == 0);
        	
        	String servs = new ArrayList<String>(servers).toString();
        	
        	CommandSender console = ProxyServer.getInstance().getConsole();
        	
            ProxyServer.getInstance().getConsole().sendMessage(new ComponentBuilder("(" + name + ") [INFO] " + server + " is sending " + entries.toString() + " to " + servs + ".").color(ChatColor.GREEN).create());
            
            servers.forEach(s -> {
            	
            	ServerInfo info = ProxyServer.getInstance().getServers().get(s);
            	
            	if (info == null)
            		return;
            	
            	if (info.getPlayers().isEmpty())
            		console.sendMessage(new ComponentBuilder("(" + name + ") [WARN] " + info.getName() + " is empty, " + (queue ? "message queued" : "message cancelled") + ".").color(ChatColor.GOLD).create());
            	
            });
        	
    		Memory.CHANNEL_CACHE.get(name).send(entries, servers.toArray(new String[servers.size()]));
            
        }
            
    }
	
}
