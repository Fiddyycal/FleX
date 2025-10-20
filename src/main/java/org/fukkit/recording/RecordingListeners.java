package org.fukkit.recording;

import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.fukkit.Fukkit;
import org.fukkit.entity.FleXBot;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.FleXEventListener;
import org.fukkit.event.player.FleXPlayerMessageReceiveEvent;

import net.md_5.fungee.server.ServerVersion;

public class RecordingListeners extends FleXEventListener {

	private Recording recording;
	
	public RecordingListeners(Recording recording) {
		this.recording = recording;
	}
	
	@EventHandler
	public void event(FleXPlayerMessageReceiveEvent event) {
		
		FleXPlayer player = event.getPlayer();
		
		if (player instanceof FleXBot)
			return;
		
		if (!this.recording.isRecording(player))
			return;
		
		this.editFrame(player, f -> f.setMessage(event.getMessage()));
		
	}
	
	@EventHandler
	public void event(PlayerItemHeldEvent event) {
		
		Player player = event.getPlayer();
		
		if (!this.recording.isRecording(event.getPlayer()))
			return;
		
		this.editFrame(player, f -> f.setItem(player.getInventory().getItem(event.getNewSlot())));
		
	}
	
	@EventHandler
	public void event(PlayerInteractEvent event) {
		
		Entity entity = event.getPlayer();
		
		if (!this.recording.isRecording(entity))
			return;
    	
		if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK)
			return;
		
    	this.editFrame(entity, f -> {
    		
    		f.addAction(RecordedAction.SWING_ARM);
    		
    		if (event.getClickedBlock() != null)
    			f.setInteractAtLocation(event.getClickedBlock().getLocation());
    		
    	});
		
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
    	
    	this.editFrame(entity, f -> f.addAction(RecordedAction.DAMAGE));
		
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
		
		Entity parse = damager;
    	
		this.editFrame(entity, f -> {
			
			f.addAction(RecordedAction.DAMAGE);
			
			if (parse != null)
				f.setInteractAtLocation(parse.getLocation());
			
		});
		
		if (damager != null) {
			
			this.editFrame(damager, f -> {
				
				f.addAction(RecordedAction.SWING_ARM);
				f.setInteractAtLocation(entity.getLocation());
				
				if (parse != null)
					f.setInteractAtLocation(parse.getLocation());
				
			});
			
		}
    	
	}
	
	private void editFrame(FleXPlayer player, Consumer<Frame> update) {
		this.editFrame(player.getUniqueId(), player.getLocation(), update);
	}
	
	private void editFrame(Entity entity, Consumer<Frame> update) {
		this.editFrame(entity.getUniqueId(), entity.getLocation(), update);
	}
	
	private void editFrame(UUID uuid, Location location, Consumer<Frame> update) {
		
		Recordable recordable = this.recording.getRecorded().get(uuid);
		
		Frame frame = recordable.getFrames().getOrDefault(this.recording.tick, new Frame(location));
		
		update.accept(frame);
		
		recordable.getFrames().put(this.recording.tick, frame);
		
	}
	
}
