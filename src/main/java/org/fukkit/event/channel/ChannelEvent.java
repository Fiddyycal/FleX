package org.fukkit.event.channel;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.md_5.fungee.channel.Channel;

public class ChannelEvent extends Event {
	
	private static HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {       
		return handlers;   
	}
	
	private Channel channel;
	
	public ChannelEvent(Channel channel) {
		this.channel = channel;
	}
	
	public Channel getChannel() {
		return this.channel;
	}

}
