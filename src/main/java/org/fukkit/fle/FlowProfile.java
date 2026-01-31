package org.fukkit.fle;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.fukkit.Fukkit;
import org.fukkit.entity.FleXPlayer;

public class FlowProfile {

	private AutoClickerDetector autoClickDetector;
	
	private static Set<FlowProfile> profiles = new HashSet<FlowProfile>();
	
	public static FlowProfile getProfile(UUID uuid) {
		return profiles.stream().filter(p -> p.uid.equals(uuid)).findFirst().orElse(null);
	}
	
	private UUID uid;
	
	public FlowProfile(Player player) {
		
		this.uid = player.getUniqueId();
		
		this.autoClickDetector = new AutoClickerDetector(this);
		
		profiles.add(this);
		
	}
	
	public AutoClickerDetector getAutoClickDetector() {
		return this.autoClickDetector;
	}
	
	public FleXPlayer getPlayer() {
		return Fukkit.getPlayer(this.uid);
	}
	
	public void destroy() {
		
		this.autoClickDetector.clear();
		this.autoClickDetector = null;
		
		profiles.remove(this);
		
	}
	
}
