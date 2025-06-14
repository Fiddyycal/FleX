package org.fukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.fukkit.Fukkit;
import org.fukkit.WorldSetting;
import org.fukkit.event.FleXEventListener;
import org.fukkit.world.FleXWorld;

public class WorldListeners extends FleXEventListener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void event(ThunderChangeEvent event) {
		
		FleXWorld world = Fukkit.getWorld(event.getWorld().getUID());
		
		if (world == null)
			return;
		
		if ((boolean)world.getSetting(WorldSetting.CYCLE_WEATHER))
			return;
		
		if (event.toThunderState())
			event.setCancelled(true);
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void event(WeatherChangeEvent event) {

		FleXWorld world = Fukkit.getWorld(event.getWorld().getUID());
		
		if (world == null)
			return;
		
		if ((boolean)world.getSetting(WorldSetting.CYCLE_WEATHER))
			return;
		
		if (event.toWeatherState())
			event.setCancelled(true);
		
	}

}
