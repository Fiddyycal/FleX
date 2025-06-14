package org.fukkit.command;

import org.bukkit.command.CommandException;

public class FleXCommandNotFoundException extends CommandException {
	
	private static final long serialVersionUID = 382288001336324737L;
	
	public FleXCommandNotFoundException() {
		super("FleX command annotation not found; @Command must be defined.");
	}
    
    public FleXCommandNotFoundException(String message) {
        super(message);
    }
    
    public FleXCommandNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
