package net.md_5.fungee.server;

public enum ServerState {
	
	ONLINE(true), LOCAL(true), OFFLINE(false), PENDING(false), RESTARTING(false), WHITELISTED(false), MAINTENANCE(false);
	
	private boolean joinable;
	
	private ServerState(boolean joinable) {
		this.joinable = joinable;
	}
	
	public boolean isJoinable() {
		return this.joinable;
	}
	
}
