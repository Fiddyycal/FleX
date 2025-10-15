package net.md_5.fungee.listeners;

import java.util.HashMap;
import java.util.Map;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.fungee.FungeeCord;
import net.md_5.fungee.Memory;

public class UpdaterListeners implements Listener {
	
	public UpdaterListeners() {
		ProxyServer.getInstance().getPluginManager().registerListener(FungeeCord.getInstance(), this);
	}

	@EventHandler
	public void event(ServerSwitchEvent event) {
		
		ProxiedPlayer player = event.getPlayer();
		
		Map<String, String> entries = new HashMap<String, String>();
		
		entries.put("UUID", player.getUniqueId().toString());
		entries.put("Server_Change", "true");
		
		Memory.CHANNEL_CACHE.get("PlayerUpdater").send(entries, player.getServer().getInfo().getName());
		
	}

	@EventHandler
	public void event(ServerConnectEvent event) {
		
		ServerInfo server = event.getTarget();
		
		ProxiedPlayer player = event.getPlayer();
		
		Map<String, String> entries = new HashMap<String, String>();
		
		entries.put("UUID", player.getUniqueId().toString());
		entries.put("Name", player.getName());
		entries.put("Time", String.valueOf(System.currentTimeMillis()));
		entries.put("Version", String.valueOf(player.getPendingConnection().getVersion()));
		entries.put("Domain", player.getPendingConnection().getVirtualHost().getHostName());
		entries.put("Server_Target", server.getName());

		System.out.println("=========================================================");
		System.out.println("[Player Updater] Sending ProxiedFleXPlayer information...");
		
		entries.forEach((k, v) -> {
			System.out.println("[Player Updater] " + k + ": " + v);
		});

		System.out.println("=========================================================");
		
		Memory.CHANNEL_CACHE.get("PlayerUpdater").send(entries, server.getName());
		
	}

	@EventHandler
	public void event(ServerDisconnectEvent event) {
		
		ServerInfo server = event.getTarget();
		
		ProxiedPlayer player = event.getPlayer();
		
		Map<String, String> entries = new HashMap<String, String>();
		
		entries.put("UUID", player.getUniqueId().toString());
		entries.put("Server_From", server.getName());
		
		Memory.CHANNEL_CACHE.get("PlayerUpdater").send(entries, server.getName());
		
	}
	
}
