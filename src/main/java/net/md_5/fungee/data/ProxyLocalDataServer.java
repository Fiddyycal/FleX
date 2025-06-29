package net.md_5.fungee.data;

import java.io.IOException;

import io.flex.commons.socket.Data;
import io.flex.commons.socket.DataCommand;
import io.flex.commons.socket.DataServer;
import net.md_5.fungee.FungeeCord;
import net.md_5.fungee.event.data.AsyncDataReceivedEvent;

public class ProxyLocalDataServer extends DataServer {
	
	public ProxyLocalDataServer(int port) throws IOException {
		super(port);
	}

	@Override
	public void onDataReceive(Data data, DataCommand command) {
		FungeeCord.getInstance().getProxy().getPluginManager().callEvent(new AsyncDataReceivedEvent(data, command));
    }
	
}
