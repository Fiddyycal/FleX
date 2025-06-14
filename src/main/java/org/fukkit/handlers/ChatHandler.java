package org.fukkit.handlers;

public class ChatHandler {
	
	public static final String[] CHAT_FILTER = { "nigger", "niggers", "nigga", "niggas", "chink", "chinks", "fag", "fags", "faggot", "faggots", "fagget", "faggets" };
	
	private static boolean registered = false;

	private String format = "%1$s%f: %c%2$s";
	
	public String getFormat() {
		return this.format;
	}
	
	public ChatHandler() {
		
		if (registered)
			return;
		
		/*
		 * 
		 * TODO: MAKE CHAT FORMAT PER SERVER
		 * 
		 */
		
		registered = true;
		
	}
	
	public static boolean isRegistered() {
		return registered;
	}
	
}
