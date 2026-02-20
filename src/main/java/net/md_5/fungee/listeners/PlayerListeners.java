package net.md_5.fungee.listeners;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.fukkit.api.helper.DataHelper;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.fungee.FungeeCord;
import net.md_5.fungee.ProtocolVersion;

public class PlayerListeners implements Listener {
	
	public PlayerListeners() {
		ProxyServer.getInstance().getPluginManager().registerListener(FungeeCord.getInstance(), this);
	}
	
	@EventHandler
	public void event(ServerConnectEvent event) {
		
		ProxiedPlayer player = event.getPlayer();
		
		PendingConnection connection = player.getPendingConnection();
	    
	    InetSocketAddress host = connection.getVirtualHost();
	    
	    if (host != null) {
	    	
	    	String name = event.getPlayer().getName();
	        String domain = host.getHostName();
	        
	        int protocol = connection.getVersion();
	        
	        ProtocolVersion version = ProtocolVersion.fromProtocol(protocol);
	        
	    	System.out.println("==============================================================================");
			System.out.println("[FleX] Player '" + name + "' is using client version " + version.name() + " (" + protocol + ") to log into '" + domain + "'...");
	    	System.out.println("==============================================================================");
	    	
	    	Map<String, String> entries = new HashMap<String, String>();
			
			entries.put("version", String.valueOf(protocol));
			entries.put("domain", domain);
			
			DataHelper.set("player." + name + ".metadata", entries.toString());
	        
	    }
	}

	@EventHandler
	public void event(PlayerDisconnectEvent event) {
		
		ProxiedPlayer player = event.getPlayer();
		
    	String name = player.getName();
    	
    	System.out.println("===================================================================");
		System.out.println("[FleX] Removing connection information for player " + name + "...");
		System.out.println("===================================================================");
		
		DataHelper.set("player." + name + ".metadata", null);
		
	}
	
}
