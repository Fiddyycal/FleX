package net.md_5.fungee.channel;

import java.util.Arrays;
import java.util.Map;

import org.fukkit.Fukkit;
import org.fukkit.entity.FleXPlayer;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import io.flex.commons.Nullable;
import io.flex.commons.cache.Cacheable;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.fungee.utils.NetworkUtils;

public abstract class Channel implements Cacheable {
	
	/**
	 * Must contain a ":" and be entirely lowercase
	 * Bungeecord limitation.
	 */
	public static final String FLEX = "flex:channel";
	
	private String name;
	
	public Channel(String name) {
		
		if (name.equalsIgnoreCase("bungeecord"))
			throw new UnsupportedOperationException("The channel \"" + name + "\" is already registered.");
		
		this.name = name;
		
	}
	
	public String getName() {
		return this.name;
	}
	
	public void send(Map<String, String> entries, String... servers) {
		this.send(false, null, entries, servers);
	}
	
	public void send(boolean queue, Map<String, String> entries, String... servers) {
		this.send(queue, null, entries, servers);
	}
	
	public void send(@Nullable FleXPlayer sender, Map<String, String> entries, String... servers) {
		this.send(false, sender, entries, servers);
	}
	
	public void send(boolean queue, @Nullable FleXPlayer sender, Map<String, String> entries, String... servers) {
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		
		String name = entries.getOrDefault("ChannelName", this.name);
		
		out.writeUTF(name);
		
		entries.put("Time", String.valueOf(System.currentTimeMillis()));
		entries.put("Queue", String.valueOf(queue));
		entries.put("ChannelName", name);
		
		ChannelHandler handler = ChannelCache.getHandler();
		
		String destination = servers != null && servers.length > 0 ? Arrays.asList(servers).toString() : NetworkUtils.NONE;
		
		if (servers.length == 1 && servers[0].equals(NetworkUtils.ALL))
			destination = NetworkUtils.ALL;
		
		if (servers.length == 1 && servers[0].equals(NetworkUtils.ALL_OTHER))
			destination = NetworkUtils.ALL_OTHER;
		
		entries.put("MessageOrigin", entries.getOrDefault("MessageOrigin", NetworkUtils.isProxy() ? ProxyServer.getInstance().getName() : Fukkit.getServerHandler().getName()));
		entries.put("MessageDestination", entries.getOrDefault("MessageDestination", destination));
		
		entries.forEach((k, v) -> {
			
			out.writeUTF(k != null ? k : "null");
			out.writeUTF(v != null ? v : "null");
			
		});
		
		handler.sendMessage(sender, out.toByteArray(), servers);
		
	}
	
	public abstract void onReceive(Map<String, String> entries);
	
	@Override
	public String toString() {
		return this.name;
	}

}
