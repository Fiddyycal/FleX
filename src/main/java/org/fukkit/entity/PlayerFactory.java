package org.fukkit.entity;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.fukkit.PlayerState;
import org.fukkit.disguise.FleXSkin;

import io.flex.commons.Nullable;

public interface PlayerFactory {
	
	public FleXPlayer createFukkitSafe(UUID uuid, String name);
	
	public FleXPlayer createFukkitSafe(UUID uuid, String name, @Nullable PlayerState state);
	
	public FleXBot createFukkitFake(String name);
	
	public FleXBot createFukkitFake(String name, @Nullable FleXSkin skin);
	
	public Player createBukkitFake(String name, FleXSkin skin);

}
