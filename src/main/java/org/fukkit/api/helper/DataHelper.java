package org.fukkit.api.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fukkit.Fukkit;
import io.flex.commons.socket.Data;
import io.flex.commons.socket.DataServer;
import io.flex.commons.utils.CollectionUtils;
import net.md_5.fungee.FungeeCord;
import net.md_5.fungee.utils.NetworkUtils;

public class DataHelper {

	public static void send(String key, Object value, String ip, int port) {
		
		if (NetworkUtils.isProxy())
			FungeeCord.getDataServer().sendData(new Data(key, String.valueOf(value), DataServer.DEFAULT_DATA_RECEIVING_PORT), ip, port);
			
		else {
			
			DataServer server = Fukkit.getConnectionHandler().getLocalData();
			
			server.sendData(new Data(key, String.valueOf(value), server.getPort()), ip, port);
			
		}
		
	}

	public static void set(String key, Object value) {
		
		if (NetworkUtils.isProxy())
			FungeeCord.getDataServer().setData(new Data(key, String.valueOf(value), DataServer.DEFAULT_DATA_RECEIVING_PORT), DataServer.DEFAULT_DATA_RECEIVING_PORT);
			
		else {
			
			DataServer server = Fukkit.getConnectionHandler().getLocalData();
			
			server.setData(new Data(key, String.valueOf(value), server.getPort()), DataServer.DEFAULT_DATA_RECEIVING_PORT);
			
		}
		
	}
	
	public static String getString(String key) {
		
		if (NetworkUtils.isProxy())
			return FungeeCord.getDataServer().getData(key, DataServer.DEFAULT_DATA_RECEIVING_PORT);
			
		else {
			
			DataServer server = Fukkit.getConnectionHandler().getLocalData();
			
			return server.getData(key, DataServer.DEFAULT_DATA_RECEIVING_PORT);
			
		}
		
	}
	
	public static int getInt(String key) {
		try {
			return Integer.parseInt(getString(key));
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	public static boolean getBoolean(String key) {
		return Boolean.parseBoolean(getString(key));
	}
	
	public static List<String> getList(String key) {
		
		String list = getString(key);
		
		if (list != null)
			return (List<String>) CollectionUtils.toCollection(list);
		
		return new ArrayList<String>();
		
	}
	
	public static Map<String, String> getMap(String key) {
		
		String map = getString(key);
		
		if (map != null)
			return (Map<String, String>) CollectionUtils.toMap(map);
		
		return new HashMap<String, String>();
		
	}
	
}
