package net.md_5.fungee.channel;

import org.fukkit.entity.FleXPlayer;

import io.flex.commons.Nullable;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class FungeeChannelHandler implements ChannelHandler {

	@Override
	public void sendMessage(@Nullable FleXPlayer sender, byte[] send, String... servers) {
		
		/**
		 * ProxiedPlayer#sendData has never worked properly...
		 * @param sender ignored.
		 */
		for (String server : servers) {
			
			ServerInfo info = ProxyServer.getInstance().getServerInfo(server);
			
			if (info != null)
				info.sendData(Channel.FLEX, send);
			
		}
		
	}
	
	

}
