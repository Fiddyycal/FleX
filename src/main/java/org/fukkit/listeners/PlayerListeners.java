package org.fukkit.listeners;

import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.util.Vector;
import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.NetworkSetting;
import org.fukkit.PlayerData;
import org.fukkit.PlayerState;
import org.fukkit.WorldSetting;
import org.fukkit.Memory.Setting;
import org.fukkit.ai.AIDriver;
import org.fukkit.api.helper.PlayerHelper;
import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.combat.CombatFactory;
import org.fukkit.entity.FleXBot;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.entity.FleXPlayerNotLoadedException;
import org.fukkit.event.FleXEventListener;
import org.fukkit.event.hologram.FloatingItemInteractEvent;
import org.fukkit.event.hologram.HologramInteractEvent;
import org.fukkit.event.player.FleXPlayerDeathEvent;
import org.fukkit.event.player.FleXPlayerDisguisedEvent;
import org.fukkit.event.player.FleXPlayerLoginEvent;
import org.fukkit.event.player.FleXPlayerMaskEvent;
import org.fukkit.event.player.FleXPlayerPreLoginEvent;
import org.fukkit.handlers.ConnectionHandler;
import org.fukkit.hologram.FloatingItem;
import org.fukkit.hologram.Hologram;
import org.fukkit.metadata.FleXFixedMetadataValue;
import org.fukkit.reward.Rank;
import org.fukkit.utils.BukkitUtils;
import org.fukkit.world.FleXWorld;

import io.flex.FleXMissingResourceException;
import io.flex.FleX.Task;
import io.flex.commons.Severity;
import io.flex.commons.console.Console;
import io.flex.commons.sql.SQLCondition;
import io.flex.commons.sql.SQLDatabase;
import io.flex.commons.sql.SQLRowWrapper;
import io.flex.commons.utils.NumUtils;
import io.netty.channel.ConnectTimeoutException;
import net.md_5.fungee.server.ServerConnectException;
import net.md_5.fungee.server.ServerVersion;

@SuppressWarnings("deprecation")
public class PlayerListeners extends FleXEventListener {
	
	@EventHandler(priority = EventPriority.LOW)
	public void event(AsyncPlayerPreLoginEvent event) {

	    FleXPlayer fp = (FleXPlayer) Memory.PLAYER_CACHE.getFromCache(event.getUniqueId());
		
		try {
			
			if (fp == null)
				Memory.PLAYER_CACHE.add(fp = Fukkit.getPlayerFactory().createFukkitSafe(event.getUniqueId(), event.getName(), PlayerState.CONNECTING));
			
			if (fp == null) {
				disconnect(event, FleXPlayerNotLoadedException.class.getName());
				return;
			}
			
		} catch (Exception e) {
			
			disconnect(event, e.getMessage());
			
			Console.log("FleXPlayer", Severity.CRITICAL, e);
	    	return;
			
		}
	    
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void event(PlayerJoinEvent event) {
		
		event.setJoinMessage(null);
		
		Player player = event.getPlayer();
		
	    FleXPlayer fp = (FleXPlayer) Memory.PLAYER_CACHE.getFromCache(player.getUniqueId());
	    
	    FleXPlayerPreLoginEvent preLoginEvent = Fukkit.getEventFactory().call(new FleXPlayerPreLoginEvent(fp));
		
	    if (preLoginEvent.isCancelled()) {
	    	
			disconnect(player, preLoginEvent.getKickMessage());
			return;
			
	    }
	    
		try {
			
			if (fp == null) {
				disconnect(player, FleXPlayerNotLoadedException.class.getName());
				return;
			}
			
			if (fp.hasMetadata("flex.player"))
				fp.removeMetadata("flex.player", Fukkit.getInstance());
			
			fp.setMetadata("flex.player", new FleXFixedMetadataValue(fp));
			
			FleXWorld world = Fukkit.getServerHandler().getDefaultWorld();
			
			if (world == null) {
				
				Task.error("World", "The default world cannot be found.");
				
				disconnect(player, "The default world cannot be found.");
				return;
				
			}
			
			boolean joinTp = (boolean) Fukkit.getServerHandler().getSetting(NetworkSetting.JOIN_TELEPORT);
			
			Location loc = null;
			
			if (!joinTp) {
				
				PlayerData data = world.getPlayerData(fp);
				
				Location lastSeen = data != null ? data.getLastSeen() : null;
				
				if (lastSeen != null) {
					
					FleXWorld fw = Fukkit.getWorld(lastSeen.getWorld().getUID());
					
					if (fw != null && fw.isJoinable()) {
						
						world = fw;
						loc = lastSeen;
						
					} else player.sendMessage(ChatColor.RED + "The world you are attempting to join is not accessible right now, joining default world.");
					
				}
				
			}
			
			if (loc == null)
				loc = world.getSpawnLocation() != null ? world.getSpawnLocation() : world.getBackupSpawnLocation();
			
			if (!world.getState().isJoinable()) {
				
				Task.error("World", "The world \"" + world.getName() + "\" is not joinable right now.");
				
				disconnect(player, "This server is not accessible right now, please wait...");
				return;
				
			}
			
			if (world.getSpawnLocation() == null && world.getBackupSpawnLocation() == null) {
				
				disconnect(player, ConnectTimeoutException.class.getName());
				return;
				
			}
			
			if (loc == null) {

				disconnect(player, "Could not find a location to spawn you.");
				return;
				
			}
			
			world.getOnlinePlayers().add(fp);
			
			fp.clean(Fukkit.getServerHandler().getSetting(NetworkSetting.CLEAN_TYPE));
			
			fp.onConnect(player);
			
			FleXPlayerLoginEvent loginEvent = new FleXPlayerLoginEvent(fp);
			
			Fukkit.getEventFactory().call(loginEvent);
			
			loc = loc.clone();
			
			if (joinTp) {
				
				int radius = (int)world.getSetting(WorldSetting.SPAWN_RADIUS) - 1;
				
				if (radius < 0)
					radius = 0;
				
				loc.setX(loc.getX() + NumUtils.getRng().getInt(-radius, radius));
				loc.setZ(loc.getZ() + NumUtils.getRng().getInt(-radius, radius));
				
			}
			
			loc = loginEvent.getSpawnLocation() != null ? loginEvent.getSpawnLocation() : loc;
			
			if (loc == null) {
				
				disconnect(player, "Spawn location cannot be null.");
				return;
				
			}
			
			player.teleport(loc);
			
		} catch (Exception e) {
			
			disconnect(player, "Player object failed to load properly.");
			
			Console.log("FleXPlayer", Severity.CRITICAL, e);
	    	return;
			
		}
		
		if (player == null || !player.isOnline())
			return;
		
		BukkitUtils.runLater(() -> {
			
			try {
				
				ConnectionHandler connectionHandler = Fukkit.getConnectionHandler();
				SQLDatabase database = connectionHandler.getDatabase();
				
				SQLRowWrapper row = database.getRow("flex_user", SQLCondition.where("uuid").is(player.getUniqueId()));
				
				if (row == null) {
					
					BukkitUtils.mainThread(() -> disconnect(player, "FleXPlayer failed to login. Please contact a staff member if this persists."));
					
					Console.log("FleXPlayer", Severity.CRITICAL, new FleXMissingResourceException("Player was not found in the FleX database: Search returned null in flex_user table."));
			    	return;
			    	
				}
				
			} catch (SQLException e) {
				BukkitUtils.mainThread(() -> disconnect(player, e.getMessage()));
			}
			
		}, 60L, true);
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void event(PlayerQuitEvent event) {
		
		UUID uuid = event.getPlayer().getUniqueId();
		
		FleXPlayer player = Fukkit.getPlayer(uuid);
		
		if (player != null) {
			
			if (player.getWorld() instanceof FleXWorld) {
				
				FleXWorld world = (FleXWorld) player.getWorld();
				
				world.getOnlinePlayers().remove(player);
				world.getPlayerData(player).setLastSeen(player.getLocation());
				
			}
			
			player.onDisconnect(event.getPlayer());
			
			/**
			 * We are removing the FleXPlayer object, so there may be excessive loading when player data is looked up in-game.
			 * 
			 * Running this async with a small delay also allows some time for any disconnect logic to finish up.
			 * 
			 * TODO: Make a low-latency mode, that does not remove players from cache when they disconnect (like it is atm).
			 * TODO: MAKE SURE TO CHECK EVERYWHERE REMOVE METHOD IS CALLED (I.e ConvictionListeners).
			 * 
			 * Example: When low-latency mode is disabled, players that dc are removed from cache and their data has to be retrieved everytime if they are offline.
			 */
			Memory.PLAYER_CACHE.remove(player);
			
		}
		
		event.setQuitMessage(null);
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
    public void event(PlayerTeleportEvent event) {
		
		FleXPlayer player = Fukkit.getPlayer(event.getPlayer().getUniqueId());
		
		/**
		 * Update player visibility.
		 */
		BukkitUtils.runLater(() -> {
			
			if (player == null || !player.isOnline())
				return;
			
			player.setVisibility(player.getVisibility());
			
		});
		
		Location from = event.getFrom();
		Location to = event.getTo();
		
		if (from.getWorld().equals(to.getWorld()))
			return;
		
		FleXWorld world = to.getWorld() != null ? Fukkit.getWorld(to.getWorld().getUID()) : null;
		
		if (world == null) {
			
			// TODO
			//event.setCancelled(true);
			
			//player.sendMessage(ChatColor.RED + "Teleport failed, the world you attempted to move to is broken, please report this to an admin.");
			return;
			
		}
		
		if (!world.getState().isJoinable()) {
			
			event.setCancelled(true);
			
			player.sendMessage(ChatColor.RED + "Teleport failed, the world you attempted to move to is not joinable right now.");
			return;
			
		}
		
    }
	
	@EventHandler(priority = EventPriority.MONITOR)
    public void event(PlayerChangedWorldEvent event) {
		
		FleXPlayer player = PlayerHelper.getPlayerSafe(event.getPlayer().getUniqueId());
		
		if (player == null)
			return;

		FleXWorld from = event.getFrom() != null ? Fukkit.getWorld(event.getFrom().getUID()) : null;
		FleXWorld world = player.getWorld() != null ? Fukkit.getWorld(player.getWorld().getUniqueId()) : null;
		
		if (from != null)
			from.getOnlinePlayers().remove(player);
		
		if (world == null) {
			
			// TODO finish this.
			
			//player.kickPlayer(ChatColor.RED + "This world is broken, please report this to an admin.");
			return;
		}
		
		if (!world.getState().isJoinable()) {
			player.kickPlayer(ChatColor.RED + "You cannot enter this world right now...");
			return;
		}
		
		world.getOnlinePlayers().add((FleXHumanEntity)player);
		
    }
	
	@EventHandler(priority = EventPriority.HIGH)
    public void event(PlayerMoveEvent event) {
    	
    	Player player = event.getPlayer();
    	
    	if (player.getGameMode() == GameMode.CREATIVE)
    		return;
		
    	if (player.hasMetadata("mode.build"))
    		return;
    	
		Location from = event.getFrom();
		Location to = event.getTo();
		
		if (from.getBlockY() == to.getBlockY())
			return;
		
    	if (to.getBlockY() >= 0)
    		return;
    	
    	FleXPlayer fp = PlayerHelper.getPlayerSafe(player.getUniqueId());
    	
    	if (fp == null)
    		return;
    	
    	FleXWorld fw = fp.getWorld();
		
    	if (fw == null)
    		return;
		
    	boolean tp = (boolean) fw.getSetting(WorldSetting.VOID_TELEPORT);
		
    	if (!tp)
    		return;
		
    	boolean spawn = (boolean) fw.getSetting(WorldSetting.VOID_TELEPORT_SPAWN);
		
    	Location loc = spawn ? fw.getSpawnLocation() : WorldSetting.LAST_KNOWN.get(player.getUniqueId());
    	
    	if (loc == null) {
    		// Last known not available.
    		loc = fw.getSpawnLocation();
    	}
    	
    	if (loc == null)
    		return;
    	
    	player.teleport(loc);
    	
    }
	
	@EventHandler(priority = EventPriority.HIGH)
    public void event(FoodLevelChangeEvent event) {
		
		if (event.isCancelled() || !(event.getEntity() instanceof Player))
			return;
    	
		Player player = (Player) event.getEntity();
		
    	if (player.hasMetadata("mode.build"))
    		return;
		
    	FleXPlayer fp = PlayerHelper.getPlayerSafe(player.getUniqueId());
    	
    	if (fp.getState().isImpervious())
    		event.setCancelled(true);
        
    }
	
	@EventHandler(priority = EventPriority.HIGH)
	public void event(EntityShootBowEvent event) {
		
		if (event.isCancelled())
			return;
		
		if (event.getEntity() instanceof Player == false)
			return;
		
		if (event.getProjectile() instanceof Arrow == false)
			return;

		FleXPlayer player = Fukkit.getPlayerExact((Player) event.getEntity());
		
		if (player.getState().isImpervious()) {
			event.setCancelled(true);
			return;
		}
		
		if (Setting.ARROW_FIX.asBoolean()) {
			
			event.getProjectile().remove();
			
			Arrow arrow = player.getPlayer().launchProjectile(Arrow.class, event.getProjectile().getVelocity());
			
			if (event.getForce() >= 1.0)
				arrow.setCritical(true);
			
		}
		
	}
	
	@EventHandler(priority = EventPriority.LOW)
    public void event(EntityDamageEvent event) {
    	
    	DamageCause cause = event.getCause();
    	
    	ServerVersion version = Fukkit.getServerHandler().getServerVersion();
    	
    	/**
    	 * Should be handled by other event below as this is a PVE only event.
    	 */
    	if (cause == DamageCause.ENTITY_ATTACK || cause == DamageCause.ENTITY_EXPLOSION || (version.ordinal() > ServerVersion.v1_8_R3.ordinal() && cause == DamageCause.valueOf("ENTITY_SWEEP_ATTACK")))
    		return;
		
		Entity entity = event.getEntity();
		
    	if (entity instanceof Player == false)
    		return;
		
    	if (entity.hasMetadata("mode.build"))
    		return;
    	
    	FleXPlayer fp = PlayerHelper.getPlayerSafe(entity.getUniqueId());
    	FleXWorld fw = fp != null ? fp.getWorld() : null;
    	
    	boolean cancel = false;
		
    	if (fw != null)
    		cancel = !(boolean)fw.getSetting(WorldSetting.DAMAGE_PVE);
    	
    	if (fp != null && fp.getState().isImpervious())
    		cancel = true;
    	
		event.setCancelled(cancel);
    	
		if (Fukkit.getFlowLineEnforcementHandler().getAIDriver() == AIDriver.FLEX) {
			
			if (cancel)
	    		return;
	    	
	    	if (fp instanceof FleXBot)
	    		((FleXBot) fp).setLastDamage(event);
			
		}
		
    }
	
	@EventHandler(priority = EventPriority.LOW)
    public void event(EntityDamageByEntityEvent event) {
		
		Entity entity = event.getEntity();
		Entity damager = event.getDamager();
		
		if (damager instanceof Player && entity.hasMetadata("hologram")) {
    		
			FleXPlayer player = Fukkit.getPlayer(damager.getUniqueId());
			
    		Hologram hologram = Fukkit.getHologramFactory().getHologram(entity);
    		
    		if (hologram != null)
    			Fukkit.getEventFactory().call(new HologramInteractEvent(hologram, player, ButtonAction.INTERACT_LEFT_CLICK_AIR));

    		FloatingItem item = Fukkit.getHologramFactory().getFloatingItem(entity);
    		
    		if (item != null)
    			Fukkit.getEventFactory().call(new FloatingItemInteractEvent(item, player, ButtonAction.INTERACT_LEFT_CLICK_AIR));
    			
    		return;
    		
    	}
    	
		if (damager instanceof Projectile && ((Projectile)damager).getShooter() instanceof Entity)
			damager = (Entity) ((Projectile)damager).getShooter();
		
    	FleXPlayer fp = PlayerHelper.getPlayerSafe(entity.getUniqueId());
    	
    	if (fp != null && fp.getState() == PlayerState.SPECTATING && event.getCause() == DamageCause.PROJECTILE) {
			
			Location loc = fp.getLocation().clone();
			
			loc.setY(loc.getY() + 5);
			
			fp.teleport(loc);
			
    	}
    	
		if (damager.hasMetadata("mode.build"))
			return;
    	
    	FleXPlayer other = PlayerHelper.getPlayerSafe(damager.getUniqueId());
		
    	FleXWorld fw = fp != null ? fp.getWorld() : other != null ? other.getWorld() : null;;
    	
		boolean pvp = fp != null && other != null;
		boolean cancel = false;
		
    	if (fw != null)
    		cancel = pvp ? !(boolean)fw.getSetting(WorldSetting.DAMAGE_PVP) : !(boolean)fw.getSetting(WorldSetting.DAMAGE_PVE);
    	
		if (fp != null)
			if (fp.getState().isImpervious() || (fp.getState() == PlayerState.INGAME_PVE_ONLY && damager instanceof Player == false))
				cancel = true;
		
		if (other != null)
			if (other.getState().isImpervious() || (other.getState() == PlayerState.INGAME_PVE_ONLY && entity instanceof Player == false))
				cancel = true;
		
    	event.setCancelled(cancel);
    	
		if (Fukkit.getFlowLineEnforcementHandler().getAIDriver() == AIDriver.FLEX) {
			
			if (cancel)
	    		return;
	    	
	    	if (fp instanceof FleXBot)
	    		((FleXBot) fp).setLastDamage(event);
			
		}
        
    }
	
	@EventHandler(priority = EventPriority.HIGH)
	public void event(PlayerDeathEvent event) {
		
		if (event.getEntity().hasMetadata("disguising"))
			event.setDeathMessage(null);
		
		FleXPlayer player = Fukkit.getPlayerExact(event.getEntity());
		
		if (player == null)
			return;
		
		Fukkit.getEventFactory().call(new FleXPlayerDeathEvent(
				
				player,
				event.getDrops(),
				event.getDroppedExp(),
				event.getNewExp(),
				event.getNewTotalExp(),
				event.getNewLevel(),
				event.getDeathMessage()));
		
		//TODO if (player.getServer().getSetting(PartitionSetting.MESSAGES_DEATH_KILL))
		event.setDeathMessage(null);
		
	}
	
	@EventHandler(priority = EventPriority.LOW)
    public void event(InventoryClickEvent event) {
		
		if (event.isCancelled())
			return;
		
    	HumanEntity player = event.getWhoClicked();
    	
    	if (player.getGameMode() == GameMode.CREATIVE)
    		return;
		
    	if (player.hasMetadata("mode.build"))
    		return;
    	
    	FleXPlayer fp = PlayerHelper.getPlayerSafe(player.getUniqueId());
    	
    	if (!fp.getState().isImpervious())
    		return;
    	
    	event.setCancelled(true);
        
    }
	
	@EventHandler(priority = EventPriority.HIGH)
    public void event(PlayerDropItemEvent event) {
		
		if (event.isCancelled())
			return;
		
    	Player player = event.getPlayer();
    	
    	if (player.getGameMode() == GameMode.CREATIVE)
    		return;
		
    	if (player.hasMetadata("mode.build"))
    		return;
    	
    	FleXPlayer fp = PlayerHelper.getPlayerSafe(player.getUniqueId());
    	
    	if (fp.getState().isImpervious())
    		event.setCancelled(true);
    	
    }
	
	@EventHandler(priority = EventPriority.HIGH)
    public void event(PlayerPickupItemEvent event) {
		
		if (event.isCancelled())
			return;
		
    	Player player = event.getPlayer();
    	
    	if (player.getGameMode() == GameMode.CREATIVE)
    		return;
		
    	if (player.hasMetadata("mode.build"))
    		return;
    	
    	FleXPlayer fp = PlayerHelper.getPlayerSafe(player.getUniqueId());
    	
    	if (fp.getState().isImpervious()) {
    		event.setCancelled(true);
    		return;
    	}
    	
    	FleXWorld fw = fp.getWorld();
		
    	if (fw == null)
    		return;
    	
    	boolean blocks = (boolean)fw.getSetting(WorldSetting.BLOCK_BREAK) || (boolean)fw.getSetting(WorldSetting.BLOCK_PLACE);
    	boolean pvpe = (boolean)fw.getSetting(WorldSetting.DAMAGE_PVP) || (boolean)fw.getSetting(WorldSetting.DAMAGE_PVE);
    	
    	event.setCancelled(!(blocks || pvpe));
        
    }
	
	@EventHandler(priority = EventPriority.HIGH)
    public void event(PlayerInteractEvent event) {
		
		if (event.isCancelled())
			return;
		
    	Player player = event.getPlayer();
    	
    	if (player.getGameMode() == GameMode.CREATIVE)
    		return;
		
    	if (player.hasMetadata("mode.build"))
    		return;
    	
    	FleXPlayer fp = PlayerHelper.getPlayerSafe(player.getUniqueId());
    	
    	if (fp == null)
    		return;
    	
    	if (fp.getState().isImpervious()) {
    		event.setCancelled(true);
    		return;
    	}
    	
    	FleXWorld fw = fp.getWorld();
		
    	if (fw == null)
    		return;
    	
    	boolean block = event.getClickedBlock() != null;
    	boolean bBreak = block && event.getAction() == Action.LEFT_CLICK_BLOCK;
    	boolean bPlace = block && event.getAction() == Action.RIGHT_CLICK_BLOCK;
    	
    	event.setCancelled((bBreak && !(boolean)fw.getSetting(WorldSetting.BLOCK_BREAK)) || (bPlace && !(boolean)fw.getSetting(WorldSetting.BLOCK_PLACE)));
    	
	}
	
	@EventHandler(priority = EventPriority.HIGH)
    public void event(VehicleEnterEvent event) {
		
		if (event.isCancelled())
			return;
		
		Entity entity = event.getEntered();
		
		if (entity instanceof Player == false)
			return;
		
    	Player player = (Player) entity;
    	
    	if (player.getGameMode() == GameMode.CREATIVE)
    		return;
		
    	if (player.hasMetadata("mode.build"))
    		return;
    	
    	FleXPlayer fp = PlayerHelper.getPlayerSafe(player.getUniqueId());
    	
    	if (fp.getState().isImpervious()) {
        	event.setCancelled(true);
    		return;
	    }
    	
    }
	
	@EventHandler(priority = EventPriority.HIGH)
    public void event(PlayerInteractAtEntityEvent event) {
		
    	FleXPlayer player = Fukkit.getPlayerExact(event.getPlayer());
    	
    	if (player == null)
    		return;
		
    	Entity entity = event.getRightClicked();
    	
    	if (entity.hasMetadata("hologram")) {
        	
    		Hologram hologram = Fukkit.getHologramFactory().getHologram(entity);
    		
    		if (hologram != null)
    			Fukkit.getEventFactory().call(new HologramInteractEvent(hologram, player, ButtonAction.INTERACT_RIGHT_CLICK_AIR));

    		FloatingItem item = Fukkit.getHologramFactory().getFloatingItem(entity);
    		
    		if (item != null)
    			Fukkit.getEventFactory().call(new FloatingItemInteractEvent(item, player, ButtonAction.INTERACT_RIGHT_CLICK_AIR));
    			
    		return;
    		
    	}
    	
    	if (player.getPlayer().getGameMode() == GameMode.CREATIVE)
    		return;
		
    	if (player.hasMetadata("mode.build"))
    		return;
    	
    	if (player.getState().isImpervious()) {
    		event.setCancelled(true);
    		return;
    	}
    	
    }
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void event(PlayerVelocityEvent event) {
		
		// TODO, intergrate new knockback
		if (null == null)
			return;
		
		Player player = event.getPlayer();
		FleXPlayer fp = Fukkit.getPlayerExact(player);
		
		if (fp.getState() == PlayerState.CONNECTING) {
	    	event.setCancelled(true);
	    	return;
		}
		
		CombatFactory combat = Fukkit.getCombatFactory();
		
		if (!combat.isEnabled())
			return;
		
		if (combat.isLegacy())
			return;
		
	    if (player.getLastDamageCause() == null)
	    	return;
	    
	    if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent == false)
	    	return;
	    
	    if (((EntityDamageByEntityEvent) player.getLastDamageCause()).getDamager() instanceof LivingEntity)
	    	event.setCancelled(true);
	    
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void event(PlayerRespawnEvent event) {

		Player pl = event.getPlayer();
		
		if (pl.hasMetadata("disguising"))
			return;
		
		FleXPlayer player = Fukkit.getPlayerExact(pl);
		
		if (player == null) {
			disconnect(pl, "FleXPlayer failed to load.");
			return;
		}
		
		FleXWorld world = Fukkit.getWorld(pl.getWorld().getUID());
		
		if (world == null)
			return;
		
		if (world.getSpawnLocation() == null && world.getBackupSpawnLocation() == null)
			return;
		
		event.setRespawnLocation(world.getSpawnLocation() != null ? world.getSpawnLocation() : world.getBackupSpawnLocation());
		
	}
	
	@EventHandler
	public void event(PlayerFishEvent event) {
		
		/**
		 *  This stops fishing rods from pulling players.
		 */
		if (event.getState() == State.CAUGHT_ENTITY && event.getCaught() instanceof Player) {
			
			Vector velocity = event.getCaught().getVelocity();
			
			BukkitUtils.runLater(() -> event.getCaught().setVelocity(velocity));
			
		}
		
	}
	
	@EventHandler
	public void event(FleXPlayerDisguisedEvent event) {
		
		FleXPlayer player = event.getPlayer();
		
		if (event.getResult() == org.fukkit.event.player.FleXPlayerDisguisedEvent.Result.UNDISGUISE) {
			
			Rank mask = player.getMask();
			
			if (mask == null)
				return;
			
			FleXPlayerMaskEvent call = new FleXPlayerMaskEvent(player, mask, org.fukkit.event.player.FleXPlayerMaskEvent.Result.UNMASK);
			
			Fukkit.getEventFactory().call(call);
			
			if (call.isCancelled())
				return;
			
			event.getPlayer().setMask(null);
			
		}
		
	}
	
	private static void disconnect(AsyncPlayerPreLoginEvent event, String reason) {
		
		if (event.getLoginResult() != Result.ALLOWED)
			return;
		
		event.setLoginResult(Result.KICK_OTHER);
		event.setKickMessage(disconnectMessage(ServerConnectException.SERVER_ERROR + ": " + reason));
		
		FleXHumanEntity fp = Memory.PLAYER_CACHE.getFromCache(event.getUniqueId());
		
		if (fp != null)
			Memory.PLAYER_CACHE.remove(fp);
		
	}
	
	private static void disconnect(Player player, String reason) {
		
		if (player == null || !player.isOnline())
			return;
		
		player.kickPlayer(disconnectMessage(reason));
		
	}
	
	private static String disconnectMessage(String reason) {
		return ChatColor.RED + "FleXPlayer failed to login:\n\n" + ChatColor.RED + reason;
	}
	
}
			