package com.velocity.data;

import java.io.IOException;

import com.velocity.Felocity;
import com.velocity.event.AsyncDataReceivedEvent;

import io.flex.commons.socket.Data;
import io.flex.commons.socket.DataCommand;
import io.flex.commons.socket.DataServer;

public class ProxyLocalDataServer extends DataServer {
	
	public ProxyLocalDataServer(int port) throws IOException {
		super(port);
	}

	@Override
	public void onDataReceive(Data data, DataCommand command) {
		Felocity.getInstance().getServer().getEventManager().fire(new AsyncDataReceivedEvent(data, command)).join();
    }
	
}
