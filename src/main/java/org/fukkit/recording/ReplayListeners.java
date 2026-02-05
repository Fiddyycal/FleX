package org.fukkit.recording;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.fukkit.Fukkit;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.FleXEventListener;
import org.fukkit.event.flow.ReplayStartEvent;
import org.fukkit.event.player.FleXPlayerAsyncChatEvent;
import org.fukkit.theme.Theme;

@SuppressWarnings("deprecation")
public class ReplayListeners extends FleXEventListener {

	private Replay replay;
	
	public ReplayListeners(Replay replay) {
		this.replay = replay;
	}
	
	@EventHandler
	public void event(EntitySpawnEvent event) {
		
		World world = event.getLocation().getWorld();
		
		if (!this.replay.getWorld().getUID().equals(world.getUID()))
			return;
		
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void event(PlayerDropItemEvent event) {
		
		if (!this.replay.isWatching(event.getPlayer()))
			return;
		
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void event(PlayerPickupItemEvent event) {
		
		if (!this.replay.isWatching(event.getPlayer()))
			return;
		
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void event(BlockPlaceEvent event) {
		
		if (!this.replay.isWatching(event.getPlayer()))
			return;
		
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void event(BlockBreakEvent event) {
		
		if (!this.replay.isWatching(event.getPlayer()))
			return;
		
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void event(ReplayStartEvent event) {
		
		if (event.getRecording() != this.replay)
			return;
		
		Bukkit.getServer().getOnlinePlayers().forEach(p -> {
			Bukkit.getServer().getOnlinePlayers().forEach(o -> {
				
				if (p == null && o == null)
					return;
				
				if (p.getWorld().getUID().equals(o.getWorld().getUID())) {
					Fukkit.getImplementation().showEntity(p, o);
					return;
				}
				
				Fukkit.getImplementation().hideEntity(p, o);
				
			});
		});
		
	}
	
	@EventHandler
	public void event(PlayerInteractEvent event) {
		
		Entity entity = event.getPlayer();
		
		if (!this.replay.isWatching(entity))
			return;
		
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void event(PlayerInteractAtEntityEvent event) {
		
		Entity clicked = event.getRightClicked();
		
		if (clicked == null)
			return;
		
		// Unique id's do not work here because Entity uuid is different from Bot uuid.
		if (!this.replay.getRecorded().values().stream().anyMatch(r -> r.getName().equals(clicked.getName())))
			return;
		
		Entity entity = event.getPlayer();
		
		if (!this.replay.isWatching(entity))
			return;
		
		FleXPlayer player = Fukkit.getPlayer(entity.getUniqueId());
		FleXPlayer transcript = Fukkit.getCachedPlayer(clicked.getUniqueId());
		
		this.replay.setTranscript(transcript);
		
		player.sendMessage(player.getTheme().format("<flow><pc>You are seeing<reset> <sc>" + transcript.getDisplayName(player.getTheme(), true) + "<pc>'s complete chat log<pp>."));
		
    	event.setCancelled(true);
		
	}
	
	@EventHandler
	public void event(PlayerDeathEvent event) {
		
		Player player = event.getEntity();
		
		if (this.replay.isWatching(player)) {
			player.kickPlayer(ChatColor.RED + "You died in a replay?");
			return;
		}
		
	}
	
	@EventHandler
	public void event(EntityDamageEvent event) {
		
		Entity entity = event.getEntity();
		
		if (this.replay.isWatching(entity)) {
	    	event.setCancelled(true);
			return;
		}
		
		if (event.getCause() != DamageCause.CUSTOM) {
			if (this.replay.getRecorded().values().stream().anyMatch(r -> r.getName().equalsIgnoreCase(entity.getName()))) {
		    	event.setCancelled(true);
				return;
			}
		}
		
	}
	
	@EventHandler
	public void event(EntityDamageByEntityEvent event) {
		
		Entity entity = event.getDamager();
		
		if (entity != null && entity instanceof Projectile && ((Projectile)entity).getShooter() instanceof Player)
			entity = (Player) ((Projectile)entity).getShooter();
		
		if (!this.replay.isWatching(entity))
			return;
		
    	event.setCancelled(true);
		
	}
	
	@EventHandler
	public void event(FleXPlayerAsyncChatEvent event) {
		
		FleXPlayer player = event.getPlayer();
		
		if (!this.replay.isWatching(player.getEntity()))
			return;
		
		Theme theme = player.getTheme();
		
		player.sendMessage(theme.format("<prefix><failure>You cannot use the chat right now<pp>."));
		
		event.setCancelled(true);
		
	}
	
}
