package org.fukkit.api.helper;

import org.fukkit.Fukkit;
import io.flex.commons.socket.Data;
import io.flex.commons.socket.DataServer;
import net.md_5.fungee.FungeeCord;
import net.md_5.fungee.utils.NetworkUtils;

public class DataHelper {
	
	public static void send(String key, Object value, String ip, int port) {
		
		DataServer server = NetworkUtils.isProxy() ? FungeeCord.getDataServer() : Fukkit.getConnectionHandler().getLocalData();
		
		server.sendData(new Data(key, String.valueOf(value), server.getPort()), ip, port);
		
	}

	public static void set(String key, Object value, String ip, int port) {

		DataServer server = NetworkUtils.isProxy() ? FungeeCord.getDataServer() : Fukkit.getConnectionHandler().getLocalData();
		
		if (NetworkUtils.isProxy())
			FungeeCord.getDataServer().setData(new Data(key, String.valueOf(value), server.getPort()), ip, port);
		
	}
	
}
