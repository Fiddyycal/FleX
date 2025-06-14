package io.flex;

public class FleXMissingResourceException extends FleXException {
	
	private static final long serialVersionUID = 8902204232151873552L;
	
	public FleXMissingResourceException() {
		super();
	}
	
	public FleXMissingResourceException(String message) {
		super(message);
	}
	
	public FleXMissingResourceException(Throwable throwable) {
		super(throwable);
	}
	
	public FleXMissingResourceException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
