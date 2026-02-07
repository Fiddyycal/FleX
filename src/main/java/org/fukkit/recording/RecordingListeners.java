package org.fukkit.recording;

import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.fukkit.Fukkit;
import org.fukkit.entity.FleXBot;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.FleXEventListener;
import org.fukkit.event.player.FleXPlayerMessageReceiveEvent;
import org.fukkit.item.Item;
import org.fukkit.utils.BukkitUtils;
import org.fukkit.utils.VersionUtils;

import net.md_5.fungee.server.ServerVersion;

@SuppressWarnings("deprecation")
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
		
		this.editFrame(player, f -> {
			
			f.addAction(RecordedAction.EQUIP_HAND);
			
			f.setItem(player.getInventory().getItem(event.getNewSlot()));
			
			ItemStack item = player.getInventory().getItem(event.getPreviousSlot());
			
			if (item != null && item.getType() == Material.FISHING_ROD)
				f.addAction(RecordedAction.STOP_USE_ITEM);
			
		});
		
	}
	
	@EventHandler
	public void event(PlayerInteractEvent event) {
		
		Entity entity = event.getPlayer();
		
		if (!this.recording.isRecording(entity))
			return;
		
		Action action = event.getAction();
		Block block = event.getClickedBlock();
		
		boolean container = false;
		
		if (action == Action.RIGHT_CLICK_BLOCK) {
			
			Material type = block.getType();
			Material ct = VersionUtils.material("CRAFTING_TABLE", "WORKBENCH", "LEGACY_WORKBENCH");
			
			if (type == Material.CHEST || type == Material.ENDER_CHEST || type == Material.FURNACE || type.name().equals("BLAST_FURNACE") || type.name().contains("TABLE") || type == ct)
				container = true;
			
		}
    	
		if (!container && action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK)
			return;
		
    	this.editFrame(entity, f -> {
    		
    		f.addAction(RecordedAction.SWING_ARM);
    		
    		if (block != null)
    			f.setInteractAtLocation(block.getLocation());
    		
    	});
		
	}
	
	@EventHandler
	public void event(PlayerFishEvent event) {
		
		Entity entity = event.getPlayer();
		
		if (!this.recording.isRecording(entity))
			return;
		
    	this.editFrame(entity, f -> f.addAction(event.getState() == State.FISHING ? RecordedAction.USE_ITEM : RecordedAction.STOP_USE_ITEM));
		
	}
	
	@EventHandler
	public void event(ProjectileLaunchEvent event) {
		
		ProjectileSource entity = ((Projectile)event.getEntity()).getShooter();
		
		if (entity instanceof LivingEntity) {
			
			LivingEntity living = (LivingEntity) entity;
			
			if (!this.recording.isRecording((LivingEntity)entity))
				return;
			
	    	this.editFrame(living, f -> {
	    		
	    		EntityType type = event.getEntity().getType();
	    		Material mat = null;
	    		
	    		switch (type) {
				case ARROW:
					
					mat = Material.ARROW;
					break;
					
				case SNOWBALL:
					
					mat = VersionUtils.material("SNOWBALL", "SNOW_BALL", "LEGACY_SNOW_BALL");
					break;
					
				case ENDER_SIGNAL:
					
					mat = VersionUtils.material("EYE_OF_ENDER", "ENDER_EYE", "LEGACY_EYE_OF_ENDER");
					break;
					
				case ENDER_PEARL:
					
					mat = Material.ENDER_PEARL;
					break;
					
				case EGG:

					mat = Material.EGG;
					break;
					
				default:
					break;
				}
	    		
	    		if (mat != null) {
	    			
	    			Item item = new Item(mat);
	    			
	    			if (mat == Material.ARROW) {
	    				
		    			Vector vector = event.getEntity().getVelocity();
		    			
	    				item.setName(vector.getX() + "," + vector.getY() + "," + vector.getZ() + "," + ((Arrow)event.getEntity()).isCritical());
	    				
	    			}
	    			
		    		f.setItem(item);
		    		
		    		f.addAction(RecordedAction.LAUNCH_PROJECTILE);
	    			
	    		}
	    		
	    	});
			
		}
		
	}
	
	@EventHandler
	public void onItemSwitch(PlayerItemHeldEvent event) {
		
		Player player = event.getPlayer();
		
		if (!this.recording.isRecording(player))
			return;
		
    	this.editFrame(player, f -> f.setItem(player.getInventory().getItem(event.getNewSlot())));
    	
	}
	
	@EventHandler
	public void onItemSwitch(PlayerDropItemEvent event) {
		
		Player player = event.getPlayer();
		
		if (!this.recording.isRecording(player))
			return;
		
		this.editFrame(player, f -> {
			
			f.addAction(RecordedAction.DROP);
			f.setItem(event.getItemDrop().getItemStack());
			
		});
    	
	}
	
	@EventHandler
	public void onItemSwitch(PlayerPickupItemEvent event) {
		
		Player player = event.getPlayer();
		
		if (!this.recording.isRecording(player))
			return;
		
		this.editFrame(player, f -> {
			
			f.addAction(RecordedAction.PICKUP);
			f.setItem(event.getItem().getItemStack());
			
		});
    	
	}
	
	@EventHandler
	public void onItemSwitch(InventoryClickEvent event) {
		
		Player player = (Player) event.getWhoClicked();
		
		if (!this.recording.isRecording(player))
			return;
		
		BukkitUtils.runLater(() -> this.editFrame(player, f -> f.setItem(player.getInventory().getItemInHand())));
    	
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void event(EntityDamageEvent event) {
		
		if (event.isCancelled())
			return;
		
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
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void event(EntityDamageByEntityEvent event) {
		
		if (event.isCancelled())
			return;
		
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
