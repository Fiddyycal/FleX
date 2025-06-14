package org.fukkit.recording;

public enum RecordedAction {
	
	NONE, IDLE,
	
	CROUCH, UNCROUCH,
	
	START_CONSUME, CONSUME,
	
	SLEEP, WAKE,
	
	EQUIP_HAND, EQUIP_OFF_HAND, EQUIP_ARMOR,
	
	START_LAUNCH_PROJECTILE, LAUNCH_PROJECTILE,
	
	SWING_ARM, INTERACT,
	
	DAMAGE, DAMAGE_CRITICAL, DAMAGE_MAGIC,
	
	MOVE,
	
	DROP, PICKUP;
	
	public boolean isAnimation() {
		return (this != DROP) && (this != PICKUP) && (this != LAUNCH_PROJECTILE) && (this != CONSUME) && (this != IDLE) && (this != NONE);
	}

}
