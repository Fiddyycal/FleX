package org.fukkit.combat;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public interface CombatFactoryRefactor {
	
	float KNOCKBACK_VELOCITY_RAW = 0.62F;
	float KNOCKBACK_HEIGHT_RAW = 0.31F;
	
	float KNOCKBACK_VELOCITY_SPRINT = 0.71F;
	float KNOCKBACK_HEIGHT_SPRINT = 0.31F;
	
	float KNOCKBACK_VELOCITY_AIR = 0.6F;
	float KNOCKBACK_HEIGHT_AIR = 0.4F;
	
	double DAMAGE_DELAY = 0.95;
    double VANILLA_DAMAGE_DELAY = 1.0;
	
	public float getKnockbackHeight();
	
	public float getKnockbackVelocity();
	
	public double getDamageDelay();
	
	public void setDamageDelay(double damageDelay);
	
	public void setKnockbackHeight(float height);
	
	public void setKnockbackVelocity(float velocity);
	
	public void setAntiLockupKnockback(boolean noLockup);
	
	public void setKnockbackRealistic(boolean realistic);
	
	public void setLegacy(boolean legacy);
	
	public void setEnabled(boolean enabled);
	
	public boolean isAntiLockupKnockbackEnabled();
	
	public boolean isKnockbackRealistic();
	
	public boolean isLegacy();
	
	public boolean isEnabled();

	public default void knockback(LivingEntity entity, Entity source, double modifier) {
		
	}
	
}
