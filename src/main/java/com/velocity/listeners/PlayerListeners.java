package com.velocity.listeners;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.fukkit.api.helper.DataHelper;

import com.velocity.Felocity;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent.PreLoginComponentResult;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.Player;

public class PlayerListeners {

	public PlayerListeners() {
        Felocity.getInstance().getServer().getEventManager().register(Felocity.getInstance(), this);
	}
	
	@Subscribe(order = PostOrder.LATE)
	public void event(PreLoginEvent event) {
		
		if (event.getResult() != PreLoginComponentResult.allowed())
			return;
		
	    InboundConnection connection = event.getConnection();
	    
	    InetSocketAddress host = connection.getVirtualHost().orElse(null);
	    
	    if (host != null) {
	    	
	    	String name = event.getUsername();
	        String domain = host.getHostName();
	        
	        if (domain == null)
	        	return;
	        
	        ProtocolVersion version = connection.getProtocolVersion();
	        
	        int protocol = version.getProtocol();
	        
	    	System.out.println("==============================================================================");
			System.out.println("[FleX] Player '" + name + "' is using client version " + version.name() + " (" + protocol + ") to log into '" + domain + "'...");
	    	System.out.println("==============================================================================");
	    	
	    	Map<String, String> entries = new HashMap<String, String>();
			
			entries.put("version", String.valueOf(protocol));
			entries.put("domain", domain);
			
			DataHelper.set("player." + name + ".metadata", entries.toString());
	        
	    }
	    
	}
	
	@Subscribe
	public void event(DisconnectEvent event) {
		
		Player player = event.getPlayer();
		
    	String name = player.getUsername();
    	
    	System.out.println("===================================================================");
		System.out.println("[FleX] Removing connection information for player " + name + "...");
		System.out.println("===================================================================");
		
		DataHelper.set("player." + name + ".metadata", null);
		
	}
	
}
