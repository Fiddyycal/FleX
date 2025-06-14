package org.fukkit.data;

import org.fukkit.api.helper.EventHelper;
import org.fukkit.event.data.DataReceivedEvent;

import io.flex.commons.socket.Data;
import io.flex.commons.socket.DataCommand;
import io.flex.commons.socket.DataServer;

public class BukkitLocalDataServer extends DataServer {
	
	public BukkitLocalDataServer(int port) {
		super(port);
	}

	@Override
	public void onDataReceive(Data data, DataCommand command) {
    	EventHelper.callEvent(new DataReceivedEvent(data, command));
    }
	
}
