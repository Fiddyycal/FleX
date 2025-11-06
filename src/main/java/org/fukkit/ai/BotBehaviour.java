package org.fukkit.ai;

public enum BotBehaviour {
	
	/**
	 * Do nothing, this will also allow bots to teleport, because teleports are cancelled due to buggy citizens navigation.
	 */
	AFK(false, false),
	
	/**
	 * Waiting for state, this should be used when players cannot move but still appear to be at keyboard.
	 */
	IDLE(false, false),
	
	/**
	 * Rome around aimlessly but will not agro unless provoked.
	 */
	PASSIVE(false, true),
	
	/**
	 * Mine, place, survive.
	 */
	SURVIVAL(true, true),
	
	/**
	 * Search for loot chests, gear up, kill others.
	 */
	SEARCH_AND_DESTROY(true, true),
	
	/**
	 * Kill everything, no mercy.
	 */
	HOSTILE(true, false);
	
	private boolean aggressive, cautious;
	
	private BotBehaviour(boolean aggressive, boolean cautious) {
		this.aggressive = aggressive;
		this.cautious = cautious;
	}
	
	public boolean isAggressive() {
		return this.aggressive;
	}
	
	public boolean isCautious() {
		return this.cautious;
	}
	
}
