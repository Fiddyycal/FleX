package org.fukkit.ai;

import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.fukkit.entity.FleXBot;

public interface FleXBotAI extends FleXAI {
	
	public static final long NO_DAMAGE_MILLIS = 500;
	
	public static final float WALK_SPEED = 1f, SPRINT_SPEED = 1.3f;
	
	public FleXBot getBot();
	
	public Set<Location> getChestMemory();
	
	public void look(Location location);
	
	public boolean evaluate(LivingEntity entity);
	
	public void attack(LivingEntity entity);
	
	public FleXPathFinder getPathFinder();
	
	public void setAggressive(boolean aggressive);
	
	public void setEvaluating(boolean evaluating);
	
	public void setGravity(boolean gravity);
	
	public void setGameMode(GameMode gamemode);
	
	public boolean isAggressive();
	
	public boolean isEvaluating();
	
}
