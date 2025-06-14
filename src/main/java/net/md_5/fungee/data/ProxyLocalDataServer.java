package net.md_5.fungee.data;

import io.flex.commons.socket.Data;
import io.flex.commons.socket.DataCommand;
import io.flex.commons.socket.DataServer;
import net.md_5.fungee.FungeeCord;
import net.md_5.fungee.event.data.DataReceivedEvent;

public class ProxyLocalDataServer extends DataServer {
	
	public ProxyLocalDataServer(int port) {
		super(port);
	}

	@Override
	public void onDataReceive(Data data, DataCommand command) {
		FungeeCord.getInstance().getProxy().getPluginManager().callEvent(new DataReceivedEvent(data, command));
    }
	
}
