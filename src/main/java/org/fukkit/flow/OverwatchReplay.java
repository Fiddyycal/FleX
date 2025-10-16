package org.fukkit.flow;

import java.io.File;

import org.fukkit.recording.Replay;

public class OverwatchReplay extends Replay {

	private boolean anonymous = false;
	
	public OverwatchReplay(File container, boolean anonymous) {
		
		super(container);
		
		this.anonymous = anonymous;
		
	}
	
	public boolean isAnonymous() {
		return this.anonymous;
	}

}
