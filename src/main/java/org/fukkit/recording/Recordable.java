package org.fukkit.recording;

import java.util.List;
import java.util.UUID;

import org.fukkit.entity.FleXPlayer;

public interface Recordable {

	public UUID getUniqueId();
	
	public List<Frame> getFrames();
	
	public FleXPlayer toPlayer();
	
}
