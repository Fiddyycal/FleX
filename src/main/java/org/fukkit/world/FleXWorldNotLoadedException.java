package org.fukkit.world;

public class FleXWorldNotLoadedException extends Exception {
	
	private static final long serialVersionUID = -8283991174528380484L;

	public FleXWorldNotLoadedException() {
		super("FleXWorld object has not loaded yet.");
	}
	
	public FleXWorldNotLoadedException(Throwable cause) {
		super("FleXWorld object has not loaded yet.", cause);
	}
	
	public FleXWorldNotLoadedException(String message) {
		super("FleXWorld object has not loaded yet: " + message);
	}
	
	public FleXWorldNotLoadedException(String message, Throwable cause) {
		super("FleXWorld object has not loaded yet: " + message, cause);
	}

}
