package org.fukkit.recording;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.fukkit.Fukkit;
import org.fukkit.event.FleXEventListener;

import net.md_5.fungee.server.ServerVersion;

public class RecordingListeners extends FleXEventListener {

	private Recording recording;
	
	public RecordingListeners(Recording recording) {
		this.recording = recording;
	}
	
	@EventHandler
	public void event(PlayerInteractEvent event) {
		
		Player player = event.getPlayer();
		
		System.out.println("ACTION TAKEN WAS: " + event.getAction());
		
		if (!this.recording.isRecording(player))
			return;
		
		Recordable recordable = this.recording.getRecorded().stream().filter(r -> r.getUniqueId().equals(player.getUniqueId())).findFirst().orElse(null);
		
		recordable.getFrames().add(new Frame(RecordedAction.SWING_ARM, player.getLocation(), event.getClickedBlock() != null ? event.getClickedBlock().getLocation() : null));
		
	}
	
	@EventHandler
	public void event(EntityDamageEvent event) {
		
		Entity entity = event.getEntity();
		
		if (!this.recording.isRecording(entity))
			return;
		
    	DamageCause cause = event.getCause();
    	
    	ServerVersion version = Fukkit.getServerHandler().getServerVersion();
    	
    	/**
    	 * Should be handled by other event below as this is a PVE only event.
    	 */
    	if (cause == DamageCause.ENTITY_ATTACK || cause == DamageCause.ENTITY_EXPLOSION || (version.ordinal() > ServerVersion.v1_8_R3.ordinal() && cause == DamageCause.valueOf("ENTITY_SWEEP_ATTACK")))
    		return;
    	
		Recordable recordable = this.recording.getRecorded().stream().filter(r -> r.getUniqueId().equals(entity.getUniqueId())).findFirst().orElse(null);
		
		recordable.getFrames().add(new Frame(RecordedAction.DAMAGE, entity.getLocation()));
		
	}
	
	@EventHandler
	public void event(EntityDamageByEntityEvent event) {
		
		Entity entity = event.getEntity();
		
		if (!this.recording.isRecording(entity))
			return;
		
		Entity damager = event.getDamager();
		
		if (damager != null && damager instanceof Projectile) {
			
			if (((Projectile)damager).getShooter() instanceof Entity)
				damager = (Entity)((Projectile)damager).getShooter();
			
		}
		
		Recordable recordable = this.recording.getRecorded().stream().filter(r -> r.getUniqueId().equals(entity.getUniqueId())).findFirst().orElse(null);
		
		recordable.getFrames().add(new Frame(RecordedAction.DAMAGE /* TODO Determine if this is a critical hit or hit with damage multiplying enchantment. */, entity.getLocation(), damager != null ? damager.getLocation() : null));
		
	}
	
}
