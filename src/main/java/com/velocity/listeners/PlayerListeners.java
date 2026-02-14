package com.velocity.listeners;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.fukkit.api.helper.DataHelper;

import com.velocity.Felocity;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;

public class PlayerListeners {

	public PlayerListeners() {
        Felocity.getInstance().getServer().getEventManager().register(Felocity.getInstance(), this);
	}
	
	private final static Map<String, String> domains = new HashMap<String, String>();
	
	@Subscribe
	public void event(PreLoginEvent event) {
		
	    InboundConnection connection = event.getConnection();
	    
	    InetSocketAddress host = connection.getVirtualHost().orElse(null);
	    
	    if (host != null) {
	    	
	    	String name = event.getUsername();
	        String domain = host.getHostName();
	        
	    	System.out.println("====================================================================");
			System.out.println("[FleX] Player '" + name + "' is logging in...");
			System.out.println("===================================================================");
	        
	        domains.put(name, domain);
	        
	    }
	    
	}
	
	@Subscribe
	public void event(ServerConnectedEvent event) {
		
		Player player = event.getPlayer();
		
		String name = player.getUsername();
		
		if (domains.containsKey(name)) {
			
			String domain = domains.remove(name);
	        
	        ProtocolVersion version = event.getPlayer().getProtocolVersion();
	        
	        int protocol = version.getProtocol();
	    	
	    	System.out.println("====================================================================");
			System.out.println("[FleX] Updating connection information for '" + name + "'...");
			System.out.println("[FleX] Connection from domain '" + domain + "' on version " + version.name() + " (" + protocol + ").");
			System.out.println("===================================================================");
			
			Map<String, String> entries = new HashMap<String, String>();
			
			entries.put("version", String.valueOf(protocol));
			entries.put("domain", domain);
			
			Server
			
			for (RegisteredServer server : Felocity.getInstance().getServer().getAllServers()) {
				
				int port = server.getServerInfo().getAddress().getPort();
				
				
				
				int dataPort = server.get
						
				DataHelper.send(name, entries, domain, dataPort);
				
				System.out.println("SERVERRRRRRRRRRRRRRRRRRRRRRRRRRRR: " + server.getServerInfo().getName());
				
			}
			DataHelper.set("player." + player.getUniqueId() + ".metadata", entries.toString());
			
		}
		
	}
	
	@Subscribe
	public void event(DisconnectEvent event) {
		
		Player player = event.getPlayer();
		
    	String name = player.getUsername();
    	
    	if (domains.containsKey(name))
    		domains.remove(name);
    		
    	System.out.println("===================================================================");
		System.out.println("[FleX] Removing connection information for player " + name + "...");
		System.out.println("===================================================================");
		
		DataHelper.set("player." + player.getUniqueId() + ".metadata", null);
		
	}
	
}
