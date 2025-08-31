package org.fukkit.scoreboard.playerlist.tab;

import org.bukkit.entity.Player;
import org.fukkit.entity.FleXPlayer;

public interface TabFactory {
	
	public void add(FleXPlayer player, int index, Player entity);
	
	public void add(FleXPlayer player, Player... entities);
	
	public void remove(FleXPlayer player, Player... entities);
	
	public void remove(FleXPlayer player, int index, Player entity);
	
	public void update(FleXPlayer player, Player... entities);

}