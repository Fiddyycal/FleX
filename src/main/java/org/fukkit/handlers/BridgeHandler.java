package org.fukkit.handlers;

public class BridgeHandler {
	
	private boolean citizens;
	
	private static boolean registered = false;
	
	public BridgeHandler() {
		
		if (registered)
			return;
		
		registered = true;
		
	}
	
	public static boolean isRegistered() {
		return registered;
	}
	
	public boolean isCitizensEnabled() {
		
		if (this.citizens)
			return this.citizens;
		
		try {
			
			Class.forName("net.citizensnpcs.api.CitizensAPI");
			Class.forName("net.citizensnpcs.Citizens");
			
			return this.citizens = true;
			
		} catch (ClassNotFoundException e) {
			return this.citizens;
		}
		
	}

}
