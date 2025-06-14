package io.flex;

public class FleXException extends RuntimeException {
	
	private static final long serialVersionUID = 6128339478323347075L;
	
	public FleXException() {
		super("FleX has encounted an exception that is in need of immediate attention.");
	}
	
	public FleXException(Throwable cause) {
		super("FleX has encounted an exception that is in need of immediate attention.", cause);
	}
	
	public FleXException(String message) {
		super("FleX has encounted an exception: " + message);
	}
	
	public FleXException(String message, Throwable cause) {
		super("FleX has encounted an exception: " + message, cause);
	}

}
