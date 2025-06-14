package net.md_5.fungee.server;

public class ServerConnectException extends RuntimeException {
	
	private static final long serialVersionUID = 1330138165002982192L;
	
	public static final String SERVER_ERROR = "There was a problem connecting you to that server: Connection refused";
	public static final String FALLBACK_ERROR = "Could not connect to default or fallback server, please try again later";
    
	public ServerConnectException() {
		super(SERVER_ERROR + ": no further information:");
	}
	
    public ServerConnectException(String message) {
        super(message);
    }
    
    public ServerConnectException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ServerConnectException(Throwable cause) {
        super(cause);
    }
    
}
