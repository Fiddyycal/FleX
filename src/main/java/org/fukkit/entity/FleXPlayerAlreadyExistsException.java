package org.fukkit.entity;

public class FleXPlayerAlreadyExistsException extends Exception {
	
	private static final long serialVersionUID = 3490822366668288637L;

	public FleXPlayerAlreadyExistsException() {
		super("FleXPlayer object with that unique id already exists.");
	}
	
	public FleXPlayerAlreadyExistsException(Throwable cause) {
		super("FleXPlayer object with that unique id already exists.", cause);
	}
	
	public FleXPlayerAlreadyExistsException(String message) {
		super("FleXPlayer object with that unique id already exists: " + message);
	}
	
	public FleXPlayerAlreadyExistsException(String message, Throwable cause) {
		super("FleXPlayer object with that unique id already exists: " + message, cause);
	}

}
