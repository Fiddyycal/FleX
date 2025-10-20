package org.fukkit.recording;

import java.util.Map;
import java.util.UUID;

import org.fukkit.entity.FleXPlayer;

public interface Recordable {

	public String getName();
	
	public UUID getUniqueId();
	
	public Map<Long, Frame> getFrames();
	
	public FleXPlayer toPlayer();
	
}
