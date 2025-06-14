package org.fukkit.event.channel;

import java.util.Collection;

import net.md_5.fungee.channel.Channel;

public class ChannelMessageReceivedEvent extends ChannelEvent {

	private ChannelMessageSendEvent event;
	
	public ChannelMessageReceivedEvent(Channel channel, ChannelMessageSendEvent event) {
		
		super(channel);
		
		this.event = event;
		
	}
	
	public Collection<String> getKeys() {
		return this.event.getKeys();
	}
	
	public Collection<String> getValues() {
		return this.event.getValues();
	}
	
	public String get(String key) {
		return this.event.get(key);
	}

}
