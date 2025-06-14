package org.fukkit.backup;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.fukkit.event.FleXEventListener;

public class PlayerIgniteListener extends FleXEventListener {

	public PlayerIgniteListener() {
		super();
	}
	
	@EventHandler
	public void event(EntityDamageByBlockEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (event.getCause() == DamageCause.FIRE) {
				System.out.println(">>>>>>>>>> Set player on fire.");
				player.setFireTicks(7*20);
			}
		}
	}

}
