package org.fukkit.recording;

public enum RecordedAction {
	
	NONE, IDLE,
	
	CROUCH, UNCROUCH,
	
	USE_ITEM, STOP_USE_ITEM, CONSUME, STOP_CONSUME,
	
	SLEEP, WAKE,
	
	EQUIP_HAND, EQUIP_OFF_HAND, EQUIP_ARMOR,
	
	LAUNCH_PROJECTILE,
	
	SWING_ARM, INTERACT,
	
	DAMAGE, DAMAGE_CRITICAL, DAMAGE_MAGIC,
	
	MOVE,
	
	DROP, PICKUP;
	
	public boolean isAnimation() {
		return (this != DROP) && (this != PICKUP) && (this != LAUNCH_PROJECTILE) && (this != IDLE) && (this != NONE);
	}

}
