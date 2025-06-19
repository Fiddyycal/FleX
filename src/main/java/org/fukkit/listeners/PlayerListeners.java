package org.fukkit.listeners;

import java.sql.SQLException;
import java.util.Arrays;
import org.bukkit.Bukkit;
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
import org.fukkit.entity.FleXPlayer;
import org.fukkit.entity.FleXPlayerNotLoadedException;
import org.fukkit.event.FleXEventListener;
import org.fukkit.event.hologram.FloatingItemInteractEvent;
import org.fukkit.event.hologram.HologramInteractEvent;
import org.fukkit.event.player.FleXPlayerDisguisedEvent;
import org.fukkit.event.player.FleXPlayerLoginEvent;
import org.fukkit.event.player.FleXPlayerMaskEvent;
import org.fukkit.handlers.ConnectionHandler;
import org.fukkit.hologram.FloatingItem;
import org.fukkit.hologram.Hologram;
import org.fukkit.reward.Rank;
import org.fukkit.utils.BukkitUtils;
import org.fukkit.utils.FormatUtils;
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

import net.md_5.fungee.event.FleXPlayerDeathEvent;
import net.md_5.fungee.server.ServerConnectException;
import net.md_5.fungee.server.ServerVersion;

@SuppressWarnings("deprecation")
public class PlayerListeners extends FleXEventListener {
	
	@EventHandler(priority = EventPriority.LOW)
	public void event(AsyncPlayerPreLoginEvent event) {

	    FleXPlayer fp = (FleXPlayer) Memory.PLAYER_CACHE.getSafe(event.getUniqueId());
		
		try {
			
			if (fp == null)
				Memory.PLAYER_CACHE.add(fp = Fukkit.getPlayerFactory().createFukkitSafe(event.getUniqueId(), event.getName()));
			
			if (fp == null) {
				disconnect(event, FleXPlayerNotLoadedException.class.getName());
				return;
			}
			
		} catch (Exception e) {
			
			disconnect(event, "FleXPlayer failed to login.");
			
			Console.log("FleXPlayer", Severity.CRITICAL, e);
	    	return;
			
		}
	    
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void event(PlayerJoinEvent event) {
		
		event.setJoinMessage(null);
		
		Player player = event.getPlayer();
		
	    FleXPlayer fp = (FleXPlayer) Memory.PLAYER_CACHE.getSafe(player.getUniqueId());
		FleXWorld world = Fukkit.getServerHandler().getDefaultWorld();
		
		Bukkit.getOnlinePlayers().forEach(p -> p.hidePlayer(player));
		
		if (world == null) {
			
			Task.error("World", "The default world could not be found.");
			
			disconnect(player, null);
			return;
			
		}
		
		if (!world.getState().isJoinable()) {
			
			Task.error("World", "The world \"" + world.getName() + "\" is not joinable right now.");
			
			disconnect(player, "This server is not accessible right now, please wait...");
			return;
			
		}
		
		try {
			
			if (world.getSpawnLocation() == null && world.getBackupSpawnLocation() == null) {
				disconnect(player, ConnectTimeoutException.class.getName());
				return;
			}
			
			if (fp == null) {
				disconnect(player, FleXPlayerNotLoadedException.class.getName());
				return;
			}
			
			fp.setState(PlayerState.CONNECTING);
			
			fp.onConnect(player);
			
			PlayerData data = world.getPlayerData(fp);
			
			Location loc = world.getSpawnLocation() != null ? world.getSpawnLocation() : world.getBackupSpawnLocation();
			Location lastSeen = data != null ? data.getLastSeen() : null;
			
			boolean joinTp = (boolean) Fukkit.getServerHandler().getSetting(NetworkSetting.JOIN_TELEPORT);
			
			if (!joinTp && lastSeen != null && lastSeen.getWorld() != null)
				loc = lastSeen;
			
			loc = loc.clone();
			
			try {
				
				if (joinTp) {
					
					int radius = (int)world.getSetting(WorldSetting.SPAWN_RADIUS) - 1;
					
					if (radius < 0)
						radius = 0;
					
					loc.setX(loc.getX() + NumUtils.getRng().getInt(-radius, radius));
					loc.setZ(loc.getZ() + NumUtils.getRng().getInt(-radius, radius));
					
				}
				
				player.teleport(loc);
				
			} catch (Exception e) {
				disconnect(player, PlayerJoinEvent.class.getName() + ": " + e.getMessage());
				return;
			}
			
			fp.clean(Fukkit.getServerHandler().getSetting(NetworkSetting.CLEAN_TYPE), true);
			
			Bukkit.getOnlinePlayers().forEach(p -> p.showPlayer(player));
			
			/**
			 * Have to re-retrieve for some reason, fixes many bugs.
			 */
			(fp = Fukkit.getPlayerExact(player)).setState(PlayerState.IDLE);
			
			Fukkit.getEventFactory().call(new FleXPlayerLoginEvent(fp));
			
		} catch (Exception e) {
			
			disconnect(player, "FleXPlayer failed to login.");
			
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
					
					disconnect(player, "FleXPlayer failed to login. Please contact a staff member if this persists.");
					
					Console.log("FleXPlayer", Severity.CRITICAL, new FleXMissingResourceException("Player was not found in the FleX database: Search returned null in flex_user table."));
			    	return;
			    	
				}
				
			} catch (SQLException e) {
				disconnect(player, "FleXPlayer failed to login: " + e.getMessage());
			}
			
		}, 20L);
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void event(PlayerQuitEvent event) {
		
		FleXPlayer player = Fukkit.getPlayer(event.getPlayer().getUniqueId());
		
		if (player != null) {
			
			if (player.getWorld() instanceof FleXWorld)
				((FleXWorld)player.getWorld()).getPlayerData(player).setLastSeen(player.getLocation());
			
			try {
				player.getHistory().getConnections().add("<- " + Bukkit.getPort());
			} catch (FleXPlayerNotLoadedException ignore) {}
			
			player.onDisconnect(event.getPlayer());
			
			/**
			 * We are removing the FleXPlayer object, so there may be excessive loading when player data is looked up in-game.
			 * 
			 * TODO: Make a low-latency mode, that does not remove players from cache when they disconnect (like it is atm).
			 * 
			 * Example: When low-latency mode is disabled, players that dc are removed from cache and their data has to be retrieved everytime if they are offline.
			 */
			Memory.PLAYER_CACHE.remove(player);
			
		}
		
		event.setQuitMessage(null);
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
    public void event(PlayerTeleportEvent event) {
		
		Player player = event.getPlayer();

		/**
		 * Update player visibility.
		 */
		BukkitUtils.runLater(() -> {
			
			FleXPlayer fp = Fukkit.getPlayer(player.getUniqueId());
			
			if (fp == null || !fp.isOnline())
				return;
			
			fp.setVisibility(fp.getVisibility());
			
		});
		
    }
	
	@EventHandler(priority = EventPriority.MONITOR)
    public void event(PlayerChangedWorldEvent event) {
		
		FleXPlayer player = PlayerHelper.getPlayerSafe(event.getPlayer().getUniqueId());
		
		if (player == null)
			return;
		
		FleXWorld world = player.getWorld() != null ? Fukkit.getWorld(player.getWorld().getUniqueId()) : null;
		
		if (world == null)
			return;
		
		if (!world.getState().isJoinable())
			player.kick(ChatColor.RED + "You cannot enter this world right now...");
		
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
    	
    	Location loc = fw.getSpawnLocation();
    	
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
    	
    	if (fp.getState() == PlayerState.SPECTATING && event.getCause() == DamageCause.PROJECTILE) {
			
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
		
		FleXPlayer player = null;
		FleXWorld world = null;

		Player pl = event.getPlayer();
		
		try {
			
			if (pl.hasMetadata("disguising"))
				return;
			
			player = Fukkit.getPlayerExact(pl);
			world = Fukkit.getWorld(pl.getWorld().getUID());
			
			event.setRespawnLocation(world.getSpawnLocation() != null ? world.getSpawnLocation() : world.getBackupSpawnLocation());
			
		} catch (NullPointerException e) {
			
			String disconnect =
					
					player == null ? "FleXPlayer failed to load." :
					world == null ? "World cannot be null." :
					world.getSpawnLocation() == null && world.getBackupSpawnLocation() == null ? "Spawn cannot be null." : "no further information:";
			
			disconnect(pl, disconnect);
			
		}
		
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
		
		String message = "Disconnected";
		
		StringBuilder sb = new StringBuilder();
		String error = ChatColor.RED + ServerConnectException.FALLBACK_ERROR + ": \n" + ServerConnectException.class.getCanonicalName() + ": ";
		
		Arrays.stream(new String[] { error + (reason != null ? reason : "no further information:") }).forEach(r -> sb.append(FormatUtils.format(r) + "\n"));
		
		message = sb.length() > 0 ? sb.toString() : "You have been kicked from the server.";
		
		event.setLoginResult(Result.KICK_OTHER);
		event.setKickMessage(message);
		
	}
	
	private static void disconnect(Player player, String reason) {
		
		String message = "Disconnected";
		
		StringBuilder sb = new StringBuilder();
		String error = ChatColor.RED + ServerConnectException.FALLBACK_ERROR + ": \n" + ServerConnectException.class.getCanonicalName() + ": ";
		
		Arrays.stream(new String[] { error + (reason != null ? reason : "no further information:") }).forEach(r -> sb.append(FormatUtils.format(r) + "\n"));
		
		message = sb.length() > 0 ? sb.toString() : "You have been kicked from the server.";
		
		player.kickPlayer(message);
		
	}
	
}
