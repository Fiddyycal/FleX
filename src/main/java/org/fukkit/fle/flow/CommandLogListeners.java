package org.fukkit.fle.flow;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.fukkit.Fukkit;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.FleXEventListener;

public class CommandLogListeners extends FleXEventListener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void event(PlayerCommandPreprocessEvent event) {
		
		if (event.isCancelled())
			return;
		
		FleXPlayer player = Fukkit.getPlayerExact(event.getPlayer());
		
		player.getHistory().getChatAndCommands().add(event.getMessage());
		
	}

}
