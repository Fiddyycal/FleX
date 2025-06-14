package org.fukkit.event.bot;

import org.bukkit.Location;
import org.fukkit.entity.FleXBot;

public class FleXBotNavigationCompleteEvent extends FleXBotEvent {
	
	private Location location;
	
	public FleXBotNavigationCompleteEvent(FleXBot bot, Location location, boolean async) {
		
		super(bot, async);
		
		this.location = location;
		
	}
	
	public Location getLocation() {
		return this.location;
	}

}
