package org.fukkit.listeners;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.fukkit.Fukkit;
import org.fukkit.WorldSetting;
import org.fukkit.event.FleXEventListener;
import org.fukkit.world.FleXWorld;

public class CreatureListeners extends FleXEventListener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void event(CreatureSpawnEvent event) {
		
		Entity entity = event.getEntity();
		
		if (entity instanceof Creature == false)
			return;
		
		FleXWorld world = Fukkit.getWorld(entity.getWorld().getUID());
		
		if (world == null)
			return;
		
		boolean animals = world.getSetting(WorldSetting.SPAWN_CREATURE_ANIMALS);
		boolean monsters = world.getSetting(WorldSetting.SPAWN_CREATURE_MONSTERS);
		boolean other = world.getSetting(WorldSetting.SPAWN_CREATURE_TESTIFICATES);
		
		boolean remove = (entity instanceof Animals && !animals) || (entity instanceof Monster && !monsters);
		
		if (remove || (!remove && !other))
			event.setCancelled(true);
		
	}

}