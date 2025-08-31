package org.fukkit.ai;

public enum BotBehaviour {
	
	/**
	 * Do nothing, this will also allow bots to teleport, because teleports are cancelled due to buggy citizens navigation.
	 */
	AFK(false),
	
	/**
	 * Waiting for state, this should be used when players cannot move but still appear to be at keyboard.
	 */
	IDLE(false),
	
	/**
	 * Rome around aimlessly but will not agro unless provoked.
	 */
	PASSIVE(false),
	
	/**
	 * Mine, place, survive.
	 */
	SURVIVAL(true),
	
	/**
	 * Search for loot chests, gear up, kill others.
	 */
	SEARCH_AND_DESTROY(true),
	
	/**
	 * Kill everything, no mercy.
	 */
	HOSTILE(true);
	
	private boolean aggressive;
	
	private BotBehaviour(boolean aggressive) {
		this.aggressive = aggressive;
	}
	
	public boolean isAggressive() {
		return this.aggressive;
	}
	
}
