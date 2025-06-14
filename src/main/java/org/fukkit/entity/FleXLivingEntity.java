package org.fukkit.entity;

import java.util.Collection;

import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffect;

public interface FleXLivingEntity extends FleXEntity {

    public FleXPlayer getKiller();
	
	public Entity getNearestEntityInSight(int range);

	public boolean addPotionEffect(PotionEffect effect);

	public boolean addPotionEffect(PotionEffect effect, boolean force);

	public boolean addPotionEffects(Collection<PotionEffect> effects);
	
    public boolean isOnGround();
	
}