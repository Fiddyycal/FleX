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

	public static void send(String key, Object value, int port) {
		
		if (NetworkUtils.isProxy())
			FungeeCord.getDataServer().sendData(new Data(key, String.valueOf(value), DataServer.DEFAULT_DATA_RECEIVING_PORT), port);
			
		else {
			
			DataServer server = Fukkit.getConnectionHandler().getLocalData();
			
			server.sendData(new Data(key, String.valueOf(value), server.getPort()), port);
			
		}
		
	}

	public static void send(String key, Object value) {
		send(key, value, DataServer.DEFAULT_DATA_RECEIVING_PORT);
	}

	public static void set(String key, Object value, int port) {
		
		if (NetworkUtils.isProxy())
			FungeeCord.getDataServer().setData(new Data(key, String.valueOf(value), DataServer.DEFAULT_DATA_RECEIVING_PORT), port);
			
		else {
			
			DataServer server = Fukkit.getConnectionHandler().getLocalData();
			
			server.setData(new Data(key, String.valueOf(value), server.getPort()), port);
			
		}
		
	}

	public static void set(String key, Object value) {
		set(key, value, DataServer.DEFAULT_DATA_RECEIVING_PORT);
	}
	
	public static String getString(String key, int port) {
		
		if (NetworkUtils.isProxy())
			return FungeeCord.getDataServer().getData(key, port);
			
		else {
			
			DataServer server = Fukkit.getConnectionHandler().getLocalData();
			
			return server.getData(key, port);
			
		}
		
	}
	
	public static String getString(String key) {
		return getString(key, DataServer.DEFAULT_DATA_RECEIVING_PORT);
	}
	
	public static int getInt(String key, int port) {
		try {
			return Integer.parseInt(getString(key, port));
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	public static int getInt(String key) {
		return getInt(key, DataServer.DEFAULT_DATA_RECEIVING_PORT);
	}
	
	public static boolean getBoolean(String key, int port) {
		return Boolean.parseBoolean(getString(key, port));
	}
	
	public static boolean getBoolean(String key) {
		return getBoolean(key, DataServer.DEFAULT_DATA_RECEIVING_PORT);
	}
	
	public static List<String> getList(String key, int port) {
		
		String list = getString(key, port);
		
		if (list != null)
			return (List<String>) CollectionUtils.toCollection(list);
		
		return new ArrayList<String>();
		
	}
	
	public static List<String> getList(String key) {
		return getList(key, DataServer.DEFAULT_DATA_RECEIVING_PORT);
	}
	
	public static Map<String, String> getMap(String key, int port) {
		
		String map = getString(key, port);
		
		if (map != null)
			return (Map<String, String>) CollectionUtils.toMap(map);
		
		return new HashMap<String, String>();
		
	}
	
	public static Map<String, String> getMap(String key) {
		return getMap(key, DataServer.DEFAULT_DATA_RECEIVING_PORT);
	}
	
}
