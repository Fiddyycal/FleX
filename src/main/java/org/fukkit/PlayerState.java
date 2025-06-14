package org.fukkit;

import org.fukkit.disguise.Visibility;

public enum PlayerState {

	IDLE(Visibility.UNCHANGED),
	CONNECTING(Visibility.UNCHANGED),
	DISCONNECTING(Visibility.UNCHANGED),
	
	INLOBBY(Visibility.ALL),
	INGAME_PVE_ONLY(Visibility.ALL),
	INGAME(Visibility.ALL),
	SPECTATING(Visibility.NONE),
	
	FLOW(Visibility.SPECTATORS_ONLY),
	CUSTOM(Visibility.UNCHANGED),
	UNKNOWN(Visibility.UNCHANGED);
	
	private Visibility visibility;
	
	private PlayerState(Visibility visibility) {
		this.visibility = visibility;
	}
	
	public Visibility getVisibility() {
		return this.visibility;
	}
	
	public boolean isImpervious() {
		return this != IDLE && this != INGAME && this != INGAME_PVE_ONLY && this != CUSTOM;
	}
	
}
