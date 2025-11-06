package org.fukkit.handlers;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.NetworkSetting;
import org.fukkit.WorldSaver;
import org.fukkit.combat.CombatFactory;
import org.fukkit.command.CommandFactory;
import org.fukkit.config.Configuration;
import org.fukkit.config.YamlConfig;
import org.fukkit.craftfukkit.Implementation;
import org.fukkit.disguise.DisguiseFactory;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.entity.FleXLivingEntity;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.entity.PlayerFactory;
import org.fukkit.event.EventFactory;
import org.fukkit.event.entity.EntityCleanEvent.CleanType;
import org.fukkit.hologram.HologramFactory;
import org.fukkit.scoreboard.playerlist.tab.TabFactory;
import org.fukkit.world.FleXWorld;
import org.fukkit.world.WorldFactory;

import io.flex.FleX.Task;
import io.flex.commons.sql.SQLDriverType;

import net.md_5.fungee.ProtocolVersion;
import net.md_5.fungee.server.ServerRegion;
import net.md_5.fungee.server.ServerVersion;

import static org.fukkit.Fukkit.setTabFactory;
import static org.fukkit.Fukkit.setEventFactory;
import static org.fukkit.Fukkit.setWorldFactory;
import static org.fukkit.Fukkit.setPlayerFactory;
import static org.fukkit.Fukkit.setCombatFactory;
import static org.fukkit.Fukkit.setCommandFactory;
import static org.fukkit.Fukkit.setDisguiseFactory;
import static org.fukkit.Fukkit.setHologramFactory;
import static org.fukkit.Fukkit.setImplementation;

@SuppressWarnings("unchecked")
public class ServerHandler {
	
	private static boolean registered = false, proxy;
	
	private static String name = Bukkit.getName();
	
	private WorldSaver saver = new WorldSaver();
	
	private SQLDriverType driver = SQLDriverType.SQLITE;
	private ServerVersion version = ServerVersion.UNSPECIFIED;
	
	private Map<NetworkSetting, Object> settings = new HashMap<NetworkSetting, Object>();
	
	public ServerHandler() {

		Task.print("Version", "Detecting server version...");
		
		@SuppressWarnings("deprecation")
		FileConfiguration sqlConf = Fukkit.getResourceHandler().getYaml(Configuration.SQL).asFileConfiguration();
		
		Task.try_(() -> {
			this.driver = SQLDriverType.valueOf(sqlConf.getString("Driver", SQLDriverType.SQLITE.name()));
		});

		String ver = "Undetected";
		
		try {
			
			String version = Bukkit.getServer().getVersion();
			
			Task.debug("Version", "Server version: " + version);
			
			String split = version.split("MC: ")[1];
			
			ver = split.substring(0, split.length()-1);
			
		} catch (NoSuchMethodError | Exception e) {
			
			Task.error("Version", "Exception caught attempting to use getVersion method: " + e.getMessage());
			
			String pack = Bukkit.getServer().getClass().getPackage().getName();
			
			Task.debug("Version", "Attempting to strip server version from package name: " + pack);
			
			ver = pack.substring(pack.lastIndexOf('.') + 1);
			
		}
		
		try {
			this.version = ServerVersion.valueOf(ver, false);
		} catch (IllegalArgumentException | NoSuchElementException e) {
			
			Task.error("Version", "This server version (" + ver + ") isn't supported by FleX.");
			
	        throw new UnsupportedOperationException("Supported server versions: " + Arrays.stream(ServerVersion.values()).filter(v -> v.isSupported()).collect(Collectors.toList()));
	        
		}
		
		FileConfiguration c = Fukkit.getResourceHandler().getYaml(Configuration.NETWORK).asFileConfiguration();

		this.settings.put(NetworkSetting.SERVER_REGION, ServerRegion.fromString(c.getString(NetworkSetting.SERVER_REGION.getKey(), NetworkSetting.SERVER_REGION.getDefault().toString())));
		this.settings.put(NetworkSetting.CLEAN_TYPE, CleanType.valueOf(c.getString(NetworkSetting.CLEAN_TYPE.getKey(), NetworkSetting.CLEAN_TYPE.getDefault().toString())));
		this.settings.put(NetworkSetting.JOIN_TELEPORT, c.getBoolean(NetworkSetting.JOIN_TELEPORT.getKey(), (boolean)NetworkSetting.JOIN_TELEPORT.getDefault()));
		this.settings.put(NetworkSetting.RESET_WORLDS, c.getBoolean(NetworkSetting.RESET_WORLDS.getKey(), (boolean)NetworkSetting.RESET_WORLDS.getDefault()));
		this.settings.put(NetworkSetting.BACKUP_WORLDS, c.getBoolean(NetworkSetting.BACKUP_WORLDS.getKey(), (boolean)NetworkSetting.BACKUP_WORLDS.getDefault()));
		this.settings.put(NetworkSetting.MESSAGES_JOIN_LEAVE, c.getBoolean(NetworkSetting.MESSAGES_JOIN_LEAVE.getKey(), (boolean)NetworkSetting.MESSAGES_JOIN_LEAVE.getDefault()));
		this.settings.put(NetworkSetting.VERSION_BLACKLIST, c.getList(NetworkSetting.VERSION_BLACKLIST.getKey(), (List<?>)NetworkSetting.VERSION_BLACKLIST.getDefault()));
		this.settings.put(NetworkSetting.UNREGISTER_COMMANDS, c.getList(NetworkSetting.UNREGISTER_COMMANDS.getKey(), (List<?>)NetworkSetting.VERSION_BLACKLIST.getDefault()));
		
		registered = true;
		
	}
	
	public SQLDriverType getDataDriver() {
		return this.driver;
	}
	
	public ServerRegion getServerRegion() {
		return (ServerRegion) this.settings.get(NetworkSetting.SERVER_REGION);
	}
	
	public ServerVersion getServerVersion() {
		return this.version;
	}
	
	public ProtocolVersion[] getBlockedProtocols() {
		
		List<ProtocolVersion> versions = (List<ProtocolVersion>)this.settings.get(NetworkSetting.VERSION_BLACKLIST);
		
		return versions.toArray(new ProtocolVersion[versions.size()]);
		
	}
	
	public WorldSaver getAutoSaver() {
		return this.saver;
	}
	
	public FleXWorld getDefaultWorld() {
		
		YamlConfig yml = Fukkit.getResourceHandler().getYaml(Configuration.NETWORK);
		
		FleXWorld world = Memory.WORLD_CACHE.getByName(yml.getString("World", "void"));
		
		return world != null ? world : Memory.WORLD_CACHE.stream().findFirst().orElse(null);
		
	}
	
	public <T> T getSetting(NetworkSetting setting) {
		return (T) this.settings.get(setting);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		ServerHandler.name = name;
	}
	
	public boolean isLocalHost() {
		return false;
	}
	
	public Collection<? extends FleXPlayer> getOnlinePlayersUnsafe() {
		
		Collection<FleXPlayer> players = new HashSet<FleXPlayer>();
		
		for (FleXHumanEntity player : Memory.PLAYER_CACHE)
			if (player instanceof FleXPlayer)
				players.add((FleXPlayer)player);
		
		return players;
		
	}
	
	@Deprecated
	/**
	 * TODO Get all players including bots and other flex entities.
	 * @return
	 */
	public Collection<? extends FleXLivingEntity> getOnlineEntitiesUnsafe() {
		return new HashSet<FleXLivingEntity>();
	}
	
	public boolean isRunningOnProxy() {
		return proxy;
	}
	
	public boolean load() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		try {
			
			String version = this.version.name();
			
			setTabFactory((TabFactory)Class.forName("org.fukkit.craftfukkit." + version + ".controller.TabController").newInstance());
			setEventFactory((EventFactory)Class.forName("org.fukkit.craftfukkit." + version + ".controller.EventController").newInstance());
			setWorldFactory((WorldFactory)Class.forName("org.fukkit.craftfukkit." + version + ".controller.WorldController").newInstance());
			setPlayerFactory((PlayerFactory)Class.forName("org.fukkit.craftfukkit." + version + ".controller.PlayerController").newInstance());
			setCombatFactory((CombatFactory)Class.forName("org.fukkit.craftfukkit." + version + ".controller.CombatController").newInstance());
			setCommandFactory((CommandFactory)Class.forName("org.fukkit.craftfukkit." + version + ".controller.CommandController").newInstance());
			setDisguiseFactory((DisguiseFactory)Class.forName("org.fukkit.craftfukkit." + version + ".controller.DisguiseController").newInstance());
			setHologramFactory((HologramFactory)Class.forName("org.fukkit.craftfukkit." + version + ".controller.HologramController").newInstance());
			setImplementation((Implementation)Class.forName("org.fukkit.craftfukkit." + version + ".CraftFukkit").newInstance());
			
		} catch (InstantiationException | ClassNotFoundException e) {
			
			Task.error("Version", "Implementation not found: " + e.getMessage());
			Task.error("Version", "Make sure you have the correct implementation in your plugins folder.");
			
	        throw new UnsupportedOperationException("Is the correct craftfukkit jar in the plugins folder? (craftfukkit-" + this.version + ".jar)");
	        
		}
		
		return true;
		
	}
	
	public boolean start() {
		
		/**
		 * Might need this for something in the future, it's the last thing that's called before the FleXFinalizeEvent.
		 */
		return true;
		
	}
	
	public static boolean isRegistered() {
		return registered;
	}
	
}
