package org.fukkit.ai;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public interface FleXAI extends Runnable {
	
	public static final long NO_DAMAGE_MILLIS = 500;
	
	public static final float WALK_SPEED = 1f, SPRINT_SPEED = 1.3f;
	
	public FleXAITask getTask();
	
	public Set<Location> getChestMemory();
	
	public void setTask(FleXAITask task);
	
	public void look(Location location);
	
	public void navigate(Location location);
	
	public boolean evaluate(LivingEntity entity);
	
	public void attack(LivingEntity entity);
	
	public boolean setAggressive(boolean aggressive);
	
	public boolean isAggressive();
	
}
