package org.fukkit.ai;

public enum BotBehaviour {
	
	/**
	 * Do nothing, this will also allow bots to teleport, because teleports are cancelled due to buggy citizens navigation.
	 */
	AFK,
	
	/**
	 * Waiting for state, this should be used when players cannot move but still appear to be at keyboard.
	 */
	IDLE,
	
	/**
	 * Rome around aimlessly but will not agro unless provoked.
	 */
	PASSIVE,
	
	/**
	 * Mine, place, survive.
	 */
	SURVIVAL,
	
	/**
	 * Search for loot chests, gear up, kill others.
	 */
	SEARCH_AND_DESTROY,
	
	/**
	 * Kill everything, no mercy.
	 */
	HOSTILE;
	
}
