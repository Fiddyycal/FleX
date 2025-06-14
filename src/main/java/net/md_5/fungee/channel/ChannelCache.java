package net.md_5.fungee.channel;

import java.util.Map;

import io.flex.commons.cache.LinkedCache;
import net.md_5.fungee.utils.NetworkUtils;

public class ChannelCache extends LinkedCache<Channel, String> {
	
	private static final long serialVersionUID = -8024452242960268884L;
	
	private static ChannelHandler handler;
	
	public static final String BUNGEECORD_BRIDGE = "PingPong";
	
	public ChannelCache() {
		super((channel, key) -> channel.toString().equals(key));
	}
	
	@Override
	public Channel get(String arg0) {
		
		if (NetworkUtils.isProxy())
			return new Channel(arg0) {
				
				@Override
				public void onReceive(Map<String, String> entries) {}
				
			};
			
		Channel channel = super.get(arg0);
		
		if (channel == null)
			throw new UnsupportedOperationException("Could not find channel '" + arg0 + "': Has it been added to this cache?");
			
		return super.get(arg0);
		
	}
	
	/**
	 * @deprecated Not recommended.
	 * @return Any sub-channel with the same parent channel.
	 */
	@Deprecated
	public Channel getByParentChannel(String channel) {
		return this.stream().filter(b -> b.getName().equals(channel)).findFirst().orElse(null);
	}
	
	public static ChannelHandler getHandler() {
		return handler;
	}
	
	@Override
	public boolean load() {
		
		handler = NetworkUtils.isProxy() ? new FungeeChannelHandler() : new FukkitChannelHandler();
		return true;
		
	}
	
}
