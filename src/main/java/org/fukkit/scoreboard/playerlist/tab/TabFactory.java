package org.fukkit.scoreboard.playerlist.tab;

import org.bukkit.entity.Player;
import org.fukkit.entity.FleXPlayer;

public interface TabFactory {
	
	public void setInTab(FleXPlayer player, int index, Player entity);
	
	public void removeFromTab(FleXPlayer player, Player... entities);
	
	public void update(FleXPlayer player, Player... entities);
	
	public void clear(FleXPlayer player);

}