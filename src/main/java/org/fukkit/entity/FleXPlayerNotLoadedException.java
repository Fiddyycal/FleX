package org.fukkit.entity;

public class FleXPlayerNotLoadedException extends Exception {
	
	private static final long serialVersionUID = 5679628351498687458L;

	public FleXPlayerNotLoadedException() {
		super("Player history has not loaded yet.");
	}
	
	public FleXPlayerNotLoadedException(Throwable cause) {
		super("Player history has not loaded yet.", cause);
	}
	
	public FleXPlayerNotLoadedException(String message) {
		super("Player history has not loaded yet: " + message);
	}
	
	public FleXPlayerNotLoadedException(String message, Throwable cause) {
		super("Player history has not loaded yet: " + message, cause);
	}

}
