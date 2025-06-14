package org.fukkit;

public class FukkitStartupException extends Exception {

	private static final long serialVersionUID = -1610952269504894366L;
	
	public FukkitStartupException() {
		super();
	}
	
	public FukkitStartupException(String message) {
		super(message);
	}
	
	public FukkitStartupException(Throwable throwable) {
		super(throwable);
	}
	
	public FukkitStartupException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
