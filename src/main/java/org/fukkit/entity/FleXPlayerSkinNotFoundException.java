package org.fukkit.entity;

public class FleXPlayerSkinNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = -3344695201804331315L;
    
    public FleXPlayerSkinNotFoundException(String message) {
        super(message);
    }
    
    public FleXPlayerSkinNotFoundException(Throwable cause) {
        super(cause);
    }
    
    public FleXPlayerSkinNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
