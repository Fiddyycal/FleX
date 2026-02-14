package net.md_5.fungee.utils;

import org.fukkit.world.FleXWorld;

public class NetworkUtils {
	
	/**
	 * 
	 * @see {@link FleXWorld#CONTAINER_FORMAT}
	 * 
	 * @param name
	 * @return partition world name
	 */
	public static String getSimpleWorldName(String name) {
		
		if (!name.startsWith(".p_"))
			return name;
		
		if (!name.contains("-"))
			return name;
		
		return name.split("-")[1];
		
	}
	
	public static final String ALL = "ALL", ALL_OTHER = "OTHER", NONE = "NONE";
	
	public static final int SPIGOT = 0, VELOCITY = 1, BUNGEECORD = 2;
	
	public static int getType() {
		
		try {
			
			Class.forName("net.md_5.bungee.api.ProxyServer");
			return BUNGEECORD;
			
		} catch(ClassNotFoundException ignore) {}
		
		try {
			
		    Class.forName("com.velocitypowered.api.proxy.ProxyServer");
		    return VELOCITY;
		    
		} catch (ClassNotFoundException ignore) {}
		
		return SPIGOT;
		
	}
	
	public static boolean isProxy() {
		
		try {
			
			Class.forName("net.md_5.bungee.api.ProxyServer");
			return true;
			
		} catch(ClassNotFoundException ignore) {}
		
		try {
			
		    Class.forName("com.velocitypowered.api.proxy.ProxyServer");
			return true;
		    
		} catch (ClassNotFoundException ignore) {}
		
		return false;
		
	}

}
