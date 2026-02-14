package com.velocity;

import java.io.IOException;

import com.google.inject.Inject;
import com.velocity.data.ProxyLocalDataServer;
import com.velocity.listeners.PlayerListeners;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;

import io.flex.commons.socket.DataServer;

@Plugin(id = "flex", name = "FleX", version = "4.1-STABLE", authors = { "5Ocal" })
public class Felocity {
	
	private static final DataServer data_server = createDataServer();
	
	private static DataServer createDataServer() {
		try {
			return new ProxyLocalDataServer(DataServer.DEFAULT_WRITABLE_PORT);
		} catch (IOException e) {
			throw new UnsupportedOperationException("An error occurred while attempting to create local data server: " + e.getMessage());
		}
	}
	
	private static Felocity instance;
	
	private ProxyServer server;

	@Inject
	public Felocity(ProxyServer server) {
		
		System.out.println("[FleX] Initializing server instance.");
        
        instance = this;
		
        this.server = server;
        
		data_server.start();
		
	}
	
	public void onDisable() {
		
		data_server.kill();
		
		this.server = null;
		
		instance = null;
		
	}
    
    @Subscribe
    public void event(ProxyInitializeEvent event) {
    	
       new PlayerListeners();
       
    }
	
	public static Felocity getInstance() {
		return instance;
	}
	
	public ProxyServer getServer() {
		return this.server;
	}
	
	public static DataServer getDataServer() {
		return data_server;
	}

}
