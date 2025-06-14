package org.fukkit.ai;

@Deprecated
public enum BotState {

	/*
	 * Staying still or acting as if away from keyboard.
	 */
	IDLE,
	
	/*
	 * Searching area for ground items or players.
	 */
	SEARCHING,
	
	/*
	 * Sifting through ground items or looting chest.
	 */
    LOOTING,
	
	/*
	 * Engaging in combat with another living entity.
	 */
    COMBAT,
	
	/*
	 * Fleeing from combat after either losing too much health or evaluating opponent to be visibly stronger.
	 */
    ESCAPING,
	
	/*
	 * Recovering from being stuck during pathfinding.
	 */
    RECOVERING
	
}
