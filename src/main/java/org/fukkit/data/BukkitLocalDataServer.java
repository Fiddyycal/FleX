package org.fukkit.data;

import java.io.IOException;

import org.fukkit.api.helper.EventHelper;
import org.fukkit.event.data.AsyncDataReceivedEvent;
import org.fukkit.utils.BukkitUtils;

import io.flex.commons.socket.Data;
import io.flex.commons.socket.DataCommand;
import io.flex.commons.socket.RelayDataServer;

public class BukkitLocalDataServer extends RelayDataServer {
	
	public BukkitLocalDataServer(int port) throws IOException {
		super(port);
	}

	@Override
	public void onDataReceive(Data data, DataCommand command) {
		BukkitUtils.asyncThread(() -> EventHelper.callEvent(new AsyncDataReceivedEvent(data, command)));
    }
	
}
