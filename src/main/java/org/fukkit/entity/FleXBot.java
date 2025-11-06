package org.fukkit.entity;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.fukkit.ai.BotAnimation;
import org.fukkit.ai.BotBehaviour;
import org.fukkit.ai.FleXBotAI;
import org.fukkit.disguise.FleXSkin;
import org.fukkit.recording.RecordedAction;

import io.flex.commons.Nullable;

public interface FleXBot extends FleXPlayer {

	public UUID getUniqueId();
	
	public String getName();
	
	public FleXSkin getSkin();
	
	public Location getLocation();
	
	public FleXBotAI getAI();
	
	public long getLastDamageTime();
	
	public BotBehaviour getBehaviour();
	
	public BotAnimation getAnimation();
	
	public void setName(String name);
	
	public void setSkin(FleXSkin skin);
	
	public void setLastDamage(EntityDamageEvent event);

	public void setBehaviour(BotBehaviour behaviour);
	
	public boolean teleport(Location location, TeleportCause cause);
	
	public boolean teleport(Location location);
	
	public void playAnimation(@Nullable BotAnimation animation, int duration);
	
	public void playAction(RecordedAction action);
	
	public void delete();
	
}
