package org.fukkit.world;

public enum WorldState {

	LOADING, RELOADING, RESETTING, BACKING_UP, IDLE;
	
	public boolean isJoinable() {
		return this == IDLE || this == BACKING_UP;
	}
	
}
