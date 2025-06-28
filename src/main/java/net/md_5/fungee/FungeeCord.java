package net.md_5.fungee;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.flex.commons.socket.DataServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.fungee.data.ProxyLocalDataServer;
import net.md_5.fungee.listeners.ChannelListeners;
import net.md_5.fungee.listeners.FlowListeners;
import net.md_5.fungee.listeners.UpdaterListeners;

public class FungeeCord extends Plugin implements Listener {
	
	public static final String NAME = "FungeeCord";
	public static final String VERSION = "1.0.1b-SNAPSHOT";
	public static final String COMMIT = "e274f23";
	public static final String TRAVERTINE = "86";

	public static final String AUTHOR = "md_5";
	public static final String ADDJUNCT = String.valueOf(new char[]{ '5', 'O', 'c', 'a', 'l' });
	
	private static final DataServer data_server = createDataServer();
	
	private static DataServer createDataServer() {
		try {
			return new ProxyLocalDataServer(15565);
		} catch (IOException e) {
			throw new UnsupportedOperationException("An error occurred while attempting to create local data server: " + e.getMessage());
		}
	}
	
	private static FungeeCord instance;
	
	public void onEnable() {
		
		instance = this;
		
		data_server.start();
		
		new ChannelListeners();
		new FlowListeners();
		//new QueueListeners();
		new UpdaterListeners();
		
		Memory.load();
		
		this.getProxy().getServers().forEach((s, i) -> {
			
			Map<String, String> entries = new HashMap<String, String>();
			
			entries.put("Name", s);
			entries.put("Port", String.valueOf(i.getAddress().getPort()));
			
			Memory.CHANNEL_CACHE.get("ServerUpdater").send(entries, s);
			
		});
		
	}
	
	public void onDisable() {
		
		data_server.kill();
		
		instance = null;
		
	}
	
	public static FungeeCord getInstance() {
		return instance;
	}
	
	public static DataServer getDataServer() {
		return data_server;
	}

}
