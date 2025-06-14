package org.fukkit.event.channel;

import java.util.Collection;
import java.util.Map;

import org.bukkit.event.Cancellable;

import net.md_5.fungee.channel.Channel;

public class ChannelMessageSendEvent extends ChannelEvent implements Cancellable {
	
	private boolean cancel = false;

	private Map<String, String> entries;
	
	public ChannelMessageSendEvent(Channel channel, Map<String, String> entries) {
		
		super(channel);
		
		this.entries = entries;
		
	}
	
	public Collection<String> getKeys() {
		return this.entries.keySet();
	}
	
	public Collection<String> getValues() {
		return this.entries.values();
	}
	
	public String get(String key) {
		return this.entries.get(key);
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	@Override
	public boolean isCancelled() {
		return this.cancel;
	}

}
