package net.md_5.fungee;

public enum ConnectionReason {

	PROXY_CONNECT, PROXY_DISCONNECT, SERVER_CHANGE, CONNECTION_LOST, SERVER_KICK;
	
	public boolean isConnect() {
		return this == PROXY_CONNECT;
	}
	
	public boolean isDisconnect() {
		return this == CONNECTION_LOST || this == ConnectionReason.PROXY_DISCONNECT;
	}
	
}
