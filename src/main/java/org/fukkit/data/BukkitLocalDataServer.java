package org.fukkit.data;

import org.fukkit.api.helper.EventHelper;
import org.fukkit.event.data.AsyncDataReceivedEvent;
import org.fukkit.utils.BukkitUtils;

import io.flex.commons.socket.Data;
import io.flex.commons.socket.DataCommand;
import io.flex.commons.socket.DataServer;

public class BukkitLocalDataServer extends DataServer {
	
	public BukkitLocalDataServer(int port) {
		super(port);
	}

	@Override
	public void onDataReceive(Data data, DataCommand command) {
		BukkitUtils.asyncThread(() -> EventHelper.callEvent(new AsyncDataReceivedEvent(data, command)));
    }
	
}
