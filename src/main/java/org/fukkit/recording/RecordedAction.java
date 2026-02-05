package org.fukkit.recording;

public enum RecordedAction {
	
	NONE, IDLE,
	
	CROUCH, UNCROUCH,
	
	USE_ITEM, STOP_USE_ITEM, CONSUME, STOP_CONSUME,
	
	SLEEP, WAKE,
	
	EQUIP_HAND, EQUIP_OFF_HAND,
	
	// I know this is a lot, but it's the best way to know what armour slot if item is null.
	EQUIP_HELMET, EQUIP_CHESTPLATE, EQUIP_LEGGINGS, EQUIP_BOOTS,
	
	LAUNCH_PROJECTILE,
	
	SWING_ARM, INTERACT,
	
	DAMAGE, DAMAGE_CRITICAL, DAMAGE_MAGIC,
	
	MOVE,
	
	DROP, PICKUP;
	
	public boolean isAnimation() {
		return (this != DROP) && (this != PICKUP) && (this != LAUNCH_PROJECTILE) && (this != IDLE) && (this != NONE);
	}

}
