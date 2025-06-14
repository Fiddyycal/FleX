package io.flex;

public class FleXRuntimeException extends Exception {
	
	private static final long serialVersionUID = 8464246565557206292L;

	public FleXRuntimeException() {
		super("Runtime has been intercepted by FleX for debugging.");
	}
	
	public FleXRuntimeException(Throwable cause) {
		super("Runtime has been intercepted by FleX for debugging.", cause);
	}
	
	public FleXRuntimeException(String message) {
		super("Runtime has been intercepted by FleX: " + message);
	}
	
	public FleXRuntimeException(String message, Throwable cause) {
		super("Runtime has been intercepted by FleX: " + message, cause);
	}

}
