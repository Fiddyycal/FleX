package io.flex.commons.socket;

import java.io.IOException;
import java.net.Socket;
import io.flex.FleX.Task;

public abstract class RelayDataServer extends DataServer {
	
	public static final String DEFAULT_WRITABLE_IP = scanForWritableIp();
	
	private static String scanForWritableIp() {
		
		String ip;
		Socket client;
		
		Task.print("Sockets", "Inter-server socket communication is enabled, scanning for writable socket...");
		
		client = attemptConnection(ip = "localhost", DEFAULT_WRITABLE_PORT);
		
		if (client == null)
			client = attemptConnection(ip = "127.0.0.1", DEFAULT_WRITABLE_PORT);
		
		if (client == null)
			for (int i = 0; i < 50; i++) {
				
				if ((client = attemptConnection(ip = "172.18.0." + i, DEFAULT_WRITABLE_PORT)) != null)
					break;
				
			}
		
		if (client == null)
			throw new UnsupportedOperationException(
					"All connection attempts for a writable socket have failed, startup cannot continue until this is resolved. Ensure that a DataServer with writable memory is online within the same machine and try again.");
		
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return ip;
		
	}
	
	public RelayDataServer(int port) throws IOException {
		super(port);
	}
	
}
