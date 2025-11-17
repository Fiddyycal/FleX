package org.fukkit.entity;

import org.bukkit.entity.Entity;

public interface FleXLivingEntity extends FleXEntity {

    public FleXPlayer getKiller();
	
	public Entity getNearestEntityInSight(int range);
	
    public boolean isOnGround();
	
}