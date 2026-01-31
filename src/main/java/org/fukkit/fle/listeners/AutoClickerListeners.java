package org.fukkit.fle.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.fukkit.event.FleXEventListener;

import org.fukkit.fle.FlowProfile;

public class AutoClickerListeners extends FleXEventListener {
	
	@EventHandler
	public void event(PlayerJoinEvent event) {
		
		FlowProfile prof = FlowProfile.getProfile(event.getPlayer().getUniqueId());
		
		if (prof == null)
			new FlowProfile(event.getPlayer());
		
	}
	
	@EventHandler
	public void event(PlayerQuitEvent event) {
		
		FlowProfile prof = FlowProfile.getProfile(event.getPlayer().getUniqueId());
		
		if (prof != null)
			prof.destroy();
		
	}
	
	@EventHandler
	public void event(PlayerInteractEvent event) {
		
	    if (event.getAction() != Action.LEFT_CLICK_AIR)
	        return;
	    
	    FlowProfile prof = FlowProfile.getProfile(event.getPlayer().getUniqueId());
	    
	    prof.getAutoClickDetector().update();
	    
	}
	
}
