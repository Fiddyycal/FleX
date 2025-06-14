package org.fukkit.event;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.fukkit.Fukkit;

public abstract class FleXEventListener implements Listener {
	
	public FleXEventListener() {
		this.register();
	}
	
	public void register() {
		Fukkit.getInstance().getServer().getPluginManager().registerEvents(this, Fukkit.getInstance());
	}
	
	public void unregister() {
		HandlerList.unregisterAll(this);
	}
	
}
