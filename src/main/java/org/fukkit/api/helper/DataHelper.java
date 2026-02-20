package org.fukkit.api.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.fukkit.Fukkit;

import com.velocity.Felocity;

import io.flex.FleX;
import io.flex.commons.socket.Data;
import io.flex.commons.socket.DataServer;
import io.flex.commons.socket.RelayDataServer;
import io.flex.commons.utils.CollectionUtils;

import net.md_5.fungee.FungeeCord;
import net.md_5.fungee.utils.NetworkUtils;

public class DataHelper {
	
	private static final Map<String, List<Consumer<Object>>> SUBSCRIBERS = new ConcurrentHashMap<>();

	public static <T> void subscribe(String key, Consumer<T> callback, Class<T> type) {
		
	    SUBSCRIBERS.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>()).add(o -> {
	    	
	    	try {
	    		if (type.isInstance(o))
	    			callback.accept(type.cast(o));
	    		
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	    	
	    });
	    
	    Object existing = get(key);
	    
	    if (existing != null && type.isInstance(existing))
	        callback.accept(type.cast(existing));
	    
	}
	
	public static void send(String key, Object value, String ip, int dataPort) {
		
		DataServer server = NetworkUtils.getType() == NetworkUtils.BUNGEECORD ? FungeeCord.getDataServer() : NetworkUtils.getType() == NetworkUtils.VELOCITY ? Felocity.getDataServer() : Fukkit.getConnectionHandler().getLocalData();
		
		server.sendData(new Data(key, String.valueOf(value), server.getPort()), ip, dataPort);
		
	}

	public static void set(String key, Object value, String ip, int dataPort) {
		set(key, value, ip, dataPort, -1);
	}

	public static void set(String key, Object value, String ip, int dataPort, long deleteMs) {

		DataServer server = NetworkUtils.getType() == NetworkUtils.BUNGEECORD ? FungeeCord.getDataServer() : NetworkUtils.getType() == NetworkUtils.VELOCITY ? Felocity.getDataServer() : Fukkit.getConnectionHandler().getLocalData();
		
		server.setData(new Data(key, String.valueOf(value), server.getPort()), ip, dataPort, deleteMs);
		
	}

	public static String get(String key, Object value, String ip, int dataPort) {
		
		DataServer server = NetworkUtils.getType() == NetworkUtils.BUNGEECORD ? FungeeCord.getDataServer() : NetworkUtils.getType() == NetworkUtils.VELOCITY ? Felocity.getDataServer() : Fukkit.getConnectionHandler().getLocalData();
		
		return server.getData(key, ip, dataPort);
		
	}

	public static void set(String key, Object value) {
		set(key, value, -1);
	}

	public static void set(String key, Object value, long deleteMs) {
		
		DataServer server = NetworkUtils.getType() == NetworkUtils.BUNGEECORD ? FungeeCord.getDataServer() : NetworkUtils.getType() == NetworkUtils.VELOCITY ? Felocity.getDataServer() : Fukkit.getConnectionHandler().getLocalData();
		
		String ip = NetworkUtils.isProxy() ? FleX.LOCALHOST_IP : RelayDataServer.DEFAULT_WRITABLE_IP;
		
		server.setData(new Data(key, String.valueOf(value), server.getPort()), ip, DataServer.DEFAULT_WRITABLE_PORT, deleteMs);
		
	}
	
	public static String getString(String key) {
		
		DataServer server = NetworkUtils.getType() == NetworkUtils.BUNGEECORD ? FungeeCord.getDataServer() : NetworkUtils.getType() == NetworkUtils.VELOCITY ? Felocity.getDataServer() : Fukkit.getConnectionHandler().getLocalData();
		
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
	
	public static Object get(String key) {
		
	    DataServer server = NetworkUtils.getType() == NetworkUtils.BUNGEECORD ? FungeeCord.getDataServer() : NetworkUtils.getType() == NetworkUtils.VELOCITY ? Felocity.getDataServer() : Fukkit.getConnectionHandler().getLocalData();

	    String raw = server.getData(key, NetworkUtils.isProxy() ? FleX.LOCALHOST_IP : RelayDataServer.DEFAULT_WRITABLE_IP, DataServer.DEFAULT_WRITABLE_PORT);

	    if (raw == null)
	    	return null;
	    
	    return parse(raw);
	    
	}
	
	private static Object parse(String value) {
		
	    if (value == null)
	    	return null;
	    
	    if (value.startsWith("{") && value.endsWith("}"))
	        return CollectionUtils.toMap(value);
	        
	    else if (value.startsWith("[") && value.endsWith("]"))
	        return CollectionUtils.toCollection(value);
	    
	    else if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value))
	        return Boolean.parseBoolean(value);
	    
	    else {
	    	
	        try {
	            return Integer.parseInt(value);
	        } catch (NumberFormatException ignored) {}
	        
	        return value;
	        
	    }
	    
	}
	
}
