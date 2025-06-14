package org.fukkit.entity;

public class FleXPlayerNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = -1018622337934769906L;
    
    public FleXPlayerNotFoundException(String message) {
        super(message);
    }
    
    public FleXPlayerNotFoundException(Throwable cause) {
        super(cause);
    }
    
    public FleXPlayerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
