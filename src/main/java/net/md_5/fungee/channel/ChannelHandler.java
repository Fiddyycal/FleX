package net.md_5.fungee.channel;

import org.fukkit.entity.FleXPlayer;

import io.flex.commons.Nullable;

public interface ChannelHandler {

	public void sendMessage(@Nullable FleXPlayer sender, byte[] send, String... servers);
	
}
