package org.fukkit.fle.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.FleXEventListener;
import org.fukkit.history.variance.ChatCommandHistory;
import org.fukkit.theme.Theme;

public class HistoryListeners extends FleXEventListener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void event(PlayerCommandPreprocessEvent event) {
		
		if (event.isCancelled())
			return;
		
		FleXPlayer player = Fukkit.getPlayer(event.getPlayer());
		
		Theme theme = player.getTheme();
		
		if (theme != null) {
			
			player.getOrLoadHistoryAsync(ChatCommandHistory.class, chatAndCommands -> chatAndCommands.add(event.getMessage()));
			return;
			
		}
		
		// If theme or history hasn't loaded yet.
		event.setCancelled(true);
		
		// TODO message in yml
		player.sendMessage((theme != null ? theme : Memory.THEME_CACHE.getDefaultTheme()).format("<engine><failure>Loading profile, please wait<pp>..."));
		return;
		
	}
	
}
