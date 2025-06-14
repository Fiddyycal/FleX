package org.fukkit.fle.flow;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.fukkit.Fukkit;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.FleXEventListener;

public class ChatLogListeners extends FleXEventListener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void event(AsyncPlayerChatEvent event) {
		
		if (event.isCancelled())
			return;
		
		FleXPlayer player = Fukkit.getPlayerExact(event.getPlayer());
		
		player.getHistory().getMessages().add(event.getMessage());
		
	}

}
