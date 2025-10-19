package org.fukkit;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.fukkit.Fukkit;
import org.fukkit.api.helper.ConfigHelper;
import org.fukkit.cache.WorldCache;
import org.fukkit.combat.CombatFactory;
import org.fukkit.command.CommandFactory;
import org.fukkit.config.Configuration;
import org.fukkit.craftfukkit.Implementation;
import org.fukkit.disguise.DisguiseFactory;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.entity.FleXLivingEntity;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.entity.PlayerFactory;
import org.fukkit.event.EventFactory;
import org.fukkit.event.FleXFinalizeEvent;
import org.fukkit.handlers.BridgeHandler;
import org.fukkit.handlers.ConnectionHandler;
import org.fukkit.handlers.FlowLineEnforcementHandler;
import org.fukkit.handlers.ServerHandler;
import org.fukkit.handlers.ResourceHandler;
import org.fukkit.hologram.HologramFactory;
import org.fukkit.scoreboard.playerlist.tab.TabFactory;
import org.fukkit.utils.BukkitUtils;
import org.fukkit.world.FleXWorld;
import org.fukkit.world.FleXWorldCreator;
import org.fukkit.world.WorldFactory;

import io.flex.FleX;
import io.flex.FleX.Task;
import io.flex.commons.Severity;
import io.flex.commons.StopWatch;
import io.flex.commons.cache.cell.BiCell;
import io.flex.commons.console.Console;
import io.flex.commons.file.DataFile;
import io.flex.commons.utils.NumUtils;
import io.flex.commons.utils.StringUtils;
import net.md_5.fungee.server.ServerVersion;

public final class Fukkit extends JavaPlugin {
	
	private static Plugin plugin;
	
	private static Fukkit fukkit;
	
	// Handlers
	private static BridgeHandler bridgeHandler;
	private static ServerHandler serverHandler;
	private static ResourceHandler resourceHandler;
	private static ConnectionHandler connectHandler;
	private static FlowLineEnforcementHandler fleHandler;
	
	// Factories
	private static TabFactory tabFactory;
	private static EventFactory eventFactory;
	private static WorldFactory worldFactory;
	private static PlayerFactory playerFactory;
	private static CombatFactory combatFactory;
	private static CommandFactory commandFactory;
	private static DisguiseFactory disguiseFactory;
	private static HologramFactory hologramFactory;
	
	private static Implementation implementation;
	
	public void onEnable() {
		
		StopWatch timer = new StopWatch();
		
		Task.print(FleX.FLEX);
		
		Fukkit.fukkit = this;
		
		/**
		 * 
		 * This handler needs to be registered first so the
		 * required plugins are loaded before bridging from FleX.
		 * 
		 */
		Fukkit.bridgeHandler = new BridgeHandler();
		
		String fukkitVer = fukkit.getDescription().getVersion();
		String bukkitVer = fukkit.getDescription().getVersion();
		
		String into = plugin != null && plugin != fukkit ? plugin.getName() + " version " + plugin.getDescription().getVersion() : null;
		
	    Fukkit.fukkit.getLogger().info(String.format("This server is running %s version %s "
	    		+ "(Injecting API version %s" + (into != null ? " into %s" : "") + ")", "CraftFukkit", fukkitVer, bukkitVer, into));
	    
	    /**
	     * Some handlers need channels to be registered.
	     */
		net.md_5.fungee.Memory.CHANNEL_CACHE.load();
	    
		/**
		 * 
		 * Handlers need resources and/or a connection to work and must be prioritized.
		 * However, ResourceHandler, ServerHandler and ConnectionHandler need to be initialized in that order.
		 * FlowlineEnforcementHandler relies on cross-platform data sending and must be last.
		 *
		 * The ConnectionHandler can contain large amounts of local data.
		 * It may take some time to load initially.
		 * 
		 */
	    
		try {
			
			Task.enableDebugMode((resourceHandler = (ResourceHandler) Class.forName("org.fukkit.handlers.ResourceHandler").newInstance())
					
					.getYaml(Configuration.ENGINE)
					.getConfig().getBoolean("Debug", true));
			
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		if (resourceHandler.getPluginYaml().getBoolean("flex.stand-alone", true))
			Task.print("FleX-Bukkit",
				
				"FleX is being used as a stand alone plugin.",
				"Development can stop FleX from generating Fukkit defaults by setting stand-alone to false in the FleX-Bukkit plugin.yml.");
		
		serverHandler = new ServerHandler();
		connectHandler = new ConnectionHandler();
		fleHandler = new FlowLineEnforcementHandler();
		
		/*
		 * All Factory classes are server version specific.
		 * As a result the controllers must be initiated last.
		 * 
		 * Once the handlers and factory instances have been initialized,
		 * FleX can safely move foward with version specific tasks.
		 * 
		 */
		
		/*
		 * Load partition files as objects.
		 */
		try {
			
			serverHandler.load();
			
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			
			Task.error("FleX-Bukkit", "A fatal error occurred preventing FleX to start up properly.");
			
			BiCell<Throwable, String> biCell = Console.log("FleX-Bukkit", Severity.EMERG, e);
			
			Task.print("-O_O-", "FleX Bot: I've noticed that not all of my gestures may be registered yet...");
			Task.print("-O_O-", "FleX Bot: I'll force the important parts of the stacktrace to print for you.");
			
			Console.print(biCell);
			
		}
		
		/*
		 * Register ranks, themes, commands etc.
		 * Partitions are useless without them.
		 */
		Memory.load();
		
		/*
		 * Now all logic is loaded, start network.
		 */
		serverHandler.start();
		
		this.locateProductKey();
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				if (Memory.WORLD_CACHE.isEmpty() || Memory.WORLD_CACHE.stream().anyMatch(w -> !w.isJoinable()))
					return;
				
				Bukkit.getWorlds().forEach(w -> {
					w.getEntities().forEach(e -> {
						
						if (e.hasMetadata("hologram") || e.hasMetadata("npc"))
							e.remove();
						
						if (serverHandler.getServerVersion().ordinal() > ServerVersion.v1_7_R4.ordinal()) {
							try {
								
								if (e instanceof org.bukkit.entity.ArmorStand && ((org.bukkit.entity.ArmorStand)e).isInvisible())
									e.remove();
								
							} catch (Exception ignore) {}
						}
						
					});
				});
				
				eventFactory.call(new FleXFinalizeEvent());
				
			    Task.print("-O_O-",
			    		
			    		"FleX Bot: Fukkit, FleX bukkit engine startup complete in " + NumUtils.asString(timer.getTime(TimeUnit.MILLISECONDS)).toLowerCase() + ".",
						"FleX Bot: Or " + timer.getTime(TimeUnit.MILLISECONDS) + " milliseconds to be more precise.",
						"FleX Bot: In " + timer.getTime(TimeUnit.MICROSECONDS) + " microseconds... and " + timer.getTime(TimeUnit.NANOSECONDS) + " nanoseconds?",
						"FleX Bot: Sorry, it gets boring around here when the server is offline.",
						"FleX Bot: I'm FleX Bot, if you didn't already notice; I like to talk.",
						"FleX Bot: If you need any help operating FleX, just ask!");
			    
			    this.cancel();
			    
			}
			
		}.runTaskTimer(fukkit, 0L, 0L);
		
	}
	
	@Override
	public void onDisable() {
		
		// TODO Unregister enchantments
		
		Bukkit.getWorlds().forEach(w -> {
			w.getEntities().forEach(e -> {
				
				if (e.hasMetadata("hologram") || e.hasMetadata("npc"))
					e.remove();
				
				if (serverHandler != null && serverHandler.getServerVersion() != null && serverHandler.getServerVersion().ordinal() > ServerVersion.v1_7_R4.ordinal()) {
					try {
						
						if (e instanceof org.bukkit.entity.ArmorStand && ((org.bukkit.entity.ArmorStand)e).isInvisible())
							e.remove();
						
					} catch (Exception ignore) {}
				}
				
			});
		});
		/*
		if (connectHandler != null && connectHandler.getLocalData() != null) {
			try {
				connectHandler.getLocalData().kill();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		*/
		fukkit = null;
		plugin = null;
		
	}
	
	public static Plugin getPlugin() {
		return plugin != null ? plugin : fukkit;
	}
	
	public static Fukkit getInstance() {
		return fukkit;
	}
	
	public static BridgeHandler getBridgeHandler() {
		return bridgeHandler;
	}
	
	@Deprecated
	public static ServerHandler getNetworkHandler() {
		return serverHandler;
	}
	
	public static ServerHandler getServerHandler() {
		return serverHandler;
	}
	
	public static ResourceHandler getResourceHandler() {
		return resourceHandler;
	}
	
	public static ConnectionHandler getConnectionHandler() {
		return connectHandler;
	}
	
	public static FlowLineEnforcementHandler getFlowLineEnforcementHandler() {
		return fleHandler;
	}
	
	public static TabFactory getTabFactory() {
		return tabFactory;
	}
	
	public static EventFactory getEventFactory() {
		return eventFactory;
	}
	
	public static WorldFactory getWorldFactory() {
		return worldFactory;
	}
	
	public static PlayerFactory getPlayerFactory() {
		return playerFactory;
	}
	
	public static CombatFactory getCombatFactory() {
		return combatFactory;
	}
	
	public static CommandFactory getCommandFactory() {
		return commandFactory;
	}
	
	public static DisguiseFactory getDisguiseFactory() {
		return disguiseFactory;
	}
	
	public static HologramFactory getHologramFactory() {
		return hologramFactory;
	}
	
	public static Implementation getImplementation() {
		return implementation;
	}
	
	public static void setTabFactory(TabFactory tabFactory) {
		Fukkit.tabFactory = tabFactory;
	}
	
	public static void setEventFactory(EventFactory eventFactory) {
		Fukkit.eventFactory = eventFactory;
	}
	
	public static void setWorldFactory(WorldFactory worldFactory) {
		Fukkit.worldFactory = worldFactory;
	}
	
	public static void setPlayerFactory(PlayerFactory playerFactory) {
		Fukkit.playerFactory = playerFactory;
	}
	
	public static void setCombatFactory(CombatFactory combatFactory) {
		Fukkit.combatFactory = combatFactory;
	}
	
	public static void setCommandFactory(CommandFactory commandFactory) {
		Fukkit.commandFactory = commandFactory;
	}
	
	public static void setDisguiseFactory(DisguiseFactory disguiseFactory) {
		Fukkit.disguiseFactory = disguiseFactory;
	}
	
	public static void setHologramFactory(HologramFactory hologramFactory) {
		Fukkit.hologramFactory = hologramFactory;
	}
	
	public static void setImplementation(Implementation implementation) {
		Fukkit.implementation = implementation;
	}
	
	public static void inject(Plugin plugin) throws FukkitStartupException, InvalidPluginException {
		
		if (plugin == null) throw new InvalidPluginException(
				"FleX (Fukkit) cannot be injected into a null plugin.");
		
		if (Fukkit.plugin != null) throw new FukkitStartupException(
				"FleX (Fukkit) has already been injected into a plugin on your system: " + Fukkit.plugin.getName());
		
		Fukkit.plugin = plugin;
		
	}
	
	public static WorldCache getWorlds() {
		return Memory.WORLD_CACHE;
	}
	
	/**
	 * 
	 * This will create a new FleXPlayer object and load
	 * it if no FleXPlayer player object exists in the cache.
	 * 
	 * @param name
	 * @return FleXPlayer object from cache, create new one if player exists but is offline.
	 */
	public static FleXPlayer getPlayer(String name) {
		return (FleXPlayer) Memory.PLAYER_CACHE.getByName(name);
	}
	
	/**
	 * 
	 * This will create a new FleXPlayer object and load
	 * it if no FleXPlayer player object exists in the cache.
	 * 
	 * @param uuid
	 * @return FleXPlayer object from cache, create new one if player exists but is offline.
	 */
	public static FleXPlayer getPlayer(UUID uuid) {
		return (FleXPlayer) Memory.PLAYER_CACHE.getByUniqueId(uuid);
	}

	/**
	 * 
	 * Unlike other player fetching methods this one will NOT
	 * create a new object and load it if the player is offline.
	 * 
	 * @param uuid
	 * @return FleXPlayer object from cache, null if none exists.
	 */
	public static FleXPlayer getCachedPlayer(UUID uuid) {
		return (FleXPlayer) Memory.PLAYER_CACHE.getFromCache(uuid);
	}

	public static FleXPlayer getPlayerExact(String name) {
		
		Player player = Bukkit.getPlayer(name);
		
		if (player != null)
			return (FleXPlayer) Memory.PLAYER_CACHE.get(player);
		
		return (FleXPlayer) Memory.PLAYER_CACHE.getByName(name);
		
	}

	public static FleXPlayer getPlayerExact(HumanEntity player) {
		return (FleXPlayer) Memory.PLAYER_CACHE.get(player);
	}

	public static void getPlayerAsync(UUID uuid, Consumer<FleXHumanEntity> callback) {
		Memory.PLAYER_CACHE.getAsync(uuid, callback);
	}

	public static void getPlayerAsync(String name, Consumer<FleXHumanEntity> callback) {
		Memory.PLAYER_CACHE.getAsync(name, callback);
	}
	
	public static Collection<? extends FleXPlayer> getOnlinePlayers() {
		return serverHandler.getOnlinePlayersUnsafe();
	}
	
	@Deprecated
	public static Collection<? extends FleXLivingEntity> getOnlineEntities() {
		return serverHandler.getOnlineEntitiesUnsafe();
	}

	public static FleXWorld createWorld(World existing) {
		return worldFactory.createWorld(existing);
	}

	public static FleXWorld createWorld(FleXWorldCreator creator) {
		return worldFactory.createWorld(creator);
	}

	public static FleXWorld getWorld(UUID uid) {
		return Memory.WORLD_CACHE.get(uid);
	}

	public static FleXWorld getWorld(String name) {
		return Memory.WORLD_CACHE.getByName(name);
	}
	
	private void locateProductKey() {
		BukkitUtils.runLater(() -> {
			
			Task.print("FleX-Bukkit", "Locating product key...");

		    SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
			
			Date now = new Date(System.currentTimeMillis());
			
			String key = StringUtils.generate(5, false).toUpperCase();
			
			for (int i = 0; i < 4; i++)
				key = key + "-" + StringUtils.generate(5, false).toUpperCase();
			
			DataFile<String> product;
			
			File flexDir = new File(ConfigHelper.world_container_path_absolute + File.separator + "flex");
			File fpk = flexDir.exists() ? Arrays.stream(flexDir.listFiles()).filter(f -> f.getName().endsWith(".fpk")).findFirst().orElse(null) : null;
			
			if (!flexDir.exists() || fpk == null)
				product = new DataFile<String>(ConfigHelper.world_container_path_absolute + File.separator + "flex", date.format(now) + ".fpk", key, false);
		    
			else product = new DataFile<String>(fpk.getPath().replace(File.separator + fpk.getName(), ""), fpk.getName());
			
	    	Task.print("FleX-Bukkit", "Reading FPK " + product.getName() + "...");
			
		    key = product.read();
		    
		    boolean disable = false;
		    
		    if (FleX.isProductKey(key, false))
		    	Task.print("FleX-Bukkit", "Product key found: " + key);
		    
		    else {
		    	
		    	Task.print("FleX-Bukkit", "Product key not found.");
		    	disable = true;
		    	
		    }
		    
			if (!disable) {
				
		    	if (product.isFresh() || !FleX.isProductKey(key, true)) {
					
					Task.print("FleX-Bukkit (PRO)", "Invalid product key (auto generated).");
					
					disable = true;
					
				}
		    	
		    } else {
		    	
		    	try {

			    	String host = InetAddress.getLocalHost().getHostName();
			    	String ip = Bukkit.getIp();
			    	
			    	Task.print("FleX-Bukkit", "Handshake start.");
			    	Task.print("FleX-Bukkit", "Host: " + host);
			    	Task.print("FleX-Bukkit", "Internet Protocol: " + (ip.equals("") ? "Empty (Local)" : ip));
			    	
				    if (
				    		
				    		!host.startsWith("DESKTOP-") &&
				    		
				    		!(host.startsWith("s1-") && host.contains("-syd")) &&
				    		
				    		!ip.equals("51.161.132.6")) {
				    	
				    	Task.print("FleX-Bukkit", "This node is not authorised to use FleX.");
				    	disable = true;
				    	
				    }
				    
			    	Task.print("FleX-Bukkit", "Handshake end. -" + (disable ? "x" : ">"));
				    
				} catch (UnknownHostException e) {
					
					e.printStackTrace();
					
					disable = true;
					
				}
		    	
		    }
		    
		    if (disable) {
		    	
		    	Fukkit.this.getServer().getPluginManager().disablePlugin(Fukkit.this);
		    	return;
		    	
		    } else Task.print("FleX-Bukkit (PRO)", "Fukkit (FleX: Bukkit Engine) v" + fukkit.getDescription().getVersion() + ".", "Enjoy the engine! -5Ocal");
			
		});
	}
	
}
