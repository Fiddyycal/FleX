package org.fukkit.listeners;

import java.io.File;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.api.helper.ConfigHelper;
import org.fukkit.config.YamlConfig;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.FleXEventListener;
import org.fukkit.event.FleXFinalizeEvent;
import org.fukkit.event.player.PlayerGuiCloseEvent;
import org.fukkit.event.theme.ThemeChangedEvent;
import org.fukkit.theme.Theme;
import org.fukkit.utils.BukkitUtils;
import org.fukkit.utils.VersionUtils;

import io.flex.commons.utils.NumUtils;

public class ThemeListeners extends FleXEventListener {
	
	private static final int[] bytes = new int[] { 1, 2, 3, 4, 5, 6, 10, 11, 13, 14 };
	
	/**
	 * Since a ChunLoadEvent is called like 10 times every second.
	 * This method is SUPER laggy, so I've disabled it temporarily to dicover a workaround.
	 */
	//@EventHandler(priority = EventPriority.HIGHEST
	@SuppressWarnings("deprecation")
	public void event(ChunkLoadEvent event) {
		
		Chunk chunk = event.getChunk();
		
		if (!chunk.isLoaded())
			return;
		
		Fukkit.getServerHandler().getOnlinePlayersUnsafe().forEach(p -> {
			
			Theme theme = p.getTheme();
			
			int[] bytes = theme.getName().contains("Rainbow") || theme.getName().contains("Unicorn") ? new int[] { 1, 2, 3, 4, 5, 6, 10, 11, 13, 14 } : new int[0];
    		
    		Player player = p.getPlayer();
			
			BukkitUtils.runLater(() -> {
				
				Memory.THEME_CACHE.getBlocks().forEach((b, pl) -> {
					
					Location location = b.getLocation();
					
					int bx = location.getChunk().getX();
					int bz = location.getChunk().getZ();
					
					int cx = chunk.getX();
					int cz = chunk.getZ();
					
					if (bx != cx && bz != cz)
						return;
					
		    		byte data = (byte) (theme.getName().contains("Rainbow") || theme.getName().contains("Unicorn") ?
							
							/**
							 * Extremely more efficient than using @param neglect in RandomNumberGenerator#getInt().
							 */
							NumUtils.getRng().getInt(0, bytes.length - 1) : theme.getDecorationData(pl));
		    		
		    		BukkitUtils.runLater(() -> {
		    			player.sendBlockChange(b.getLocation(), VersionUtils.material("STAINED_CLAY", "WHITE_CONCRETE"), data);
					}, true);
		    		
		    	});
				
			});
	    	
		});
		
	}

	@EventHandler
	public void event(FleXFinalizeEvent event) {

		YamlConfig yaml = new YamlConfig(ConfigHelper.flex_path + File.separator + "data", "themed-blocks");
		FileConfiguration conf = yaml.asFileConfiguration();
		
		if (conf == null)
			return;
		
		ConfigurationSection section = conf.getConfigurationSection("Blocks");
		
		if (section == null)
			return;
		
		Set<String> keys = section.getKeys(false);
		
		if (keys == null || keys.isEmpty())
			return;
		
		keys.forEach(c -> {
    		
    		String[] spl = c.split(",");
    		
    		World world = Fukkit.getInstance().getServer().getWorld(spl[0]);
    		
    		if (world == null)
    			return;
    		
    		Location location = new Location(
    				
    				Fukkit.getInstance().getServer().getWorld(spl[0]),
    				Integer.parseInt(spl[1]),
    				Integer.parseInt(spl[2]),
    				Integer.parseInt(spl[3])
    				
    		);
    		
    		Memory.THEME_CACHE.getBlocks().put(world.getBlockAt(location), conf.getBoolean("Blocks." + c + ".Primary", true));
    		
		});
		
	}
	
	@EventHandler
	@SuppressWarnings("deprecation")
	public void event(ThemeChangedEvent event) {
		
		// TODO: Remove null == null
		if (null == null)
			return;
		
		Theme theme = event.getTheme();
		
		String name = theme.getName();
		
		Player player = event.getPlayer().getPlayer();
		
		Memory.THEME_CACHE.getBlocks().forEach((b, p) -> {
			
			byte data = (byte) (name.contains("Rainbow") || name.contains("Unicorn") ?
					
					/**
					 * Extremely more efficient than using @param neglect in RandomNumberGenerator#getInt().
					 */
					NumUtils.getRng().getInt(0, bytes.length - 1) : event.getTheme().getDecorationData(p));
    		
			player.sendBlockChange(b.getLocation(), VersionUtils.material("STAINED_CLAY", "WHITE_CONCRETE"), data);
    		
    	});
		
	}
	
	@EventHandler
	public void event(PlayerGuiCloseEvent event) {
		
		if (Fukkit.getPlayerExact(event.getPlayer()).getTheme() == null)
			event.setCancelled(true);
		
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void event(AsyncPlayerChatEvent event) {
		
		FleXPlayer player = Fukkit.getPlayerExact(event.getPlayer());
		
		if (player.getTheme() != null)
			return;
		
		event.setCancelled(true);
		
		player.sendMessage(ChatColor.RED + "Please select a theme before attempting to use the chat.");
		player.sendMessage(ChatColor.RED + "Use \"/theme flex\" if you are connected on an external device.");
		
	}
	
}
