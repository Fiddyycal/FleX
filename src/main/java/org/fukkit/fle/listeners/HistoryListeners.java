package org.fukkit.fle.listeners;

import java.net.InetSocketAddress;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.entity.FleXPlayerNotLoadedException;
import org.fukkit.event.FleXEventListener;
import org.fukkit.event.player.FleXPlayerLoadEvent;
import org.fukkit.history.variance.IPHistory;
import org.fukkit.theme.Theme;
import org.fukkit.utils.BukkitUtils;

public class HistoryListeners extends FleXEventListener {

	@EventHandler(priority = EventPriority.HIGH)
	public void event(FleXPlayerLoadEvent event) {
		
		if (event.isOffline())
			return;
		
		FleXPlayer player = event.getPlayer();
		
		if (player.isOnline()) {
			
			InetSocketAddress address = player.getPlayer().getAddress();
			
			if (address == null)
				return;
			
			String ip = address.getAddress().getHostAddress();
			
			player.getHistoryAsync(h -> {
				
				IPHistory ips = h.getIps();
				
				if (!ips.ipSet().contains(ip))
					BukkitUtils.asyncThread(() -> ips.add(ip));
				
			});
			
		}
		
	}
	
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
		
		// TODO message in yml
		player.sendMessage((theme != null ? theme : Memory.THEME_CACHE.getDefaultTheme()).format("<engine><failure>Loading profile, please wait<pp>..."));
		return;
		
	}
	
}
