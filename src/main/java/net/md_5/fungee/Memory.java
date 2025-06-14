package net.md_5.fungee;

import net.md_5.fungee.channel.ChannelCache;

public interface Memory {

	ChannelCache CHANNEL_CACHE = new ChannelCache();
	
	static void load() {
		
		CHANNEL_CACHE.load();
		
	}
	
}
