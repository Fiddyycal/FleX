package org.fukkit.entity;

public class FleXPlayerHistoryNotLoadedException extends Exception {
	
	private static final long serialVersionUID = 5679628351498687458L;

	public FleXPlayerHistoryNotLoadedException() {
		super("History object has not loaded yet.");
	}
	
	public FleXPlayerHistoryNotLoadedException(Throwable cause) {
		super("History object has not loaded yet.", cause);
	}
	
	public FleXPlayerHistoryNotLoadedException(String message) {
		super("History object has not loaded yet: " + message);
	}
	
	public FleXPlayerHistoryNotLoadedException(String message, Throwable cause) {
		super("History object has not loaded yet: " + message, cause);
	}

}
