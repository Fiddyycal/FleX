package org.fukkit.fle;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.entity.FleXPlayerNotLoadedException;
import org.fukkit.event.FleXEventListener;
import org.fukkit.theme.Theme;

public class CommandLogListeners extends FleXEventListener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void event(PlayerCommandPreprocessEvent event) {
		
		if (event.isCancelled())
			return;
		
		FleXPlayer player = Fukkit.getPlayerExact(event.getPlayer());
		
		Theme theme = player.getTheme();
		
		if (theme != null) {
			
			try {
				
				player.getHistory().getChatAndCommands().add(event.getMessage());
				return;
				
			} catch (FleXPlayerNotLoadedException ignore) {}
			
		}
		
		// If theme or history hasn't loaded yet.
		event.setCancelled(true);
		
		// TODO
		player.sendMessage((theme != null ? theme : Memory.THEME_CACHE.getDefaultTheme()).format("<engine><failure>Loading profile, please wait<pp>..."));
		return;
		
	}

}
