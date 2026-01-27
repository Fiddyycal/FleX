package org.fukkit.api.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fukkit.Fukkit;

import io.flex.FleX;
import io.flex.commons.socket.Data;
import io.flex.commons.socket.DataServer;
import io.flex.commons.socket.RelayDataServer;
import io.flex.commons.utils.CollectionUtils;

import net.md_5.fungee.FungeeCord;
import net.md_5.fungee.utils.NetworkUtils;

public class DataHelper {
	
	public static void send(String key, Object value, String ip, int port) {
		
		DataServer server = NetworkUtils.isProxy() ? FungeeCord.getDataServer() : Fukkit.getConnectionHandler().getLocalData();
		
		server.sendData(new Data(key, String.valueOf(value), server.getPort()), ip, port);
		
	}

	public static void set(String key, Object value, String ip, int port) {
		set(key, value, ip, port, -1);
	}

	public static void set(String key, Object value, String ip, int port, long deleteMs) {

		DataServer server = NetworkUtils.isProxy() ? FungeeCord.getDataServer() : Fukkit.getConnectionHandler().getLocalData();
		
		server.setData(new Data(key, String.valueOf(value), server.getPort()), ip, port, deleteMs);
		
	}

	public static String get(String key, Object value, String ip, int port) {
		
		DataServer server = NetworkUtils.isProxy() ? FungeeCord.getDataServer() : Fukkit.getConnectionHandler().getLocalData();
		
		return server.getData(key, ip, port);
		
	}

	public static void set(String key, Object value) {
		set(key, value, -1);
	}

	public static void set(String key, Object value, long deleteMs) {
		
		DataServer server = NetworkUtils.isProxy() ? FungeeCord.getDataServer() : Fukkit.getConnectionHandler().getLocalData();
		
		String ip = NetworkUtils.isProxy() ? FleX.LOCALHOST_IP : RelayDataServer.DEFAULT_WRITABLE_IP;
		
		server.setData(new Data(key, String.valueOf(value), server.getPort()), ip, DataServer.DEFAULT_WRITABLE_PORT, deleteMs);
		
	}
	
	public static String getString(String key) {
		
		DataServer server = NetworkUtils.isProxy() ? FungeeCord.getDataServer() : Fukkit.getConnectionHandler().getLocalData();
		
		String ip = NetworkUtils.isProxy() ? FleX.LOCALHOST_IP : RelayDataServer.DEFAULT_WRITABLE_IP;
		
		return server.getData(key, ip, DataServer.DEFAULT_WRITABLE_PORT);
		
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
