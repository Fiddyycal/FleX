package org.fukkit.listeners;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.fukkit.Fukkit;
import org.fukkit.WorldSetting;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.FleXEventListener;
import org.fukkit.utils.VersionUtils;
import org.fukkit.world.FleXWorld;

public class BlockListeners extends FleXEventListener {
	
	@SuppressWarnings("unchecked")
	@EventHandler(priority = EventPriority.HIGH)
    public void event(BlockBreakEvent event) {
    	
		if (event.isCancelled())
			return;
		
    	Player player = event.getPlayer();
    	
    	if (player.getGameMode() == GameMode.CREATIVE)
    		return;
		
    	if (player.hasMetadata("mode.build"))
    		return;
    	
    	FleXPlayer fp = Fukkit.getPlayer(player.getUniqueId());
    	FleXWorld fw = fp.getWorld();
    	
    	if (fp.getState().isImpervious()) {
    		event.setCancelled(true);
    		return;
    	}
    	
    	if (fw == null)
    		return;
    	
    	if (!(boolean)fw.getSetting(WorldSetting.BLOCK_BREAK)) {
    		event.setCancelled(true);
    		return;
    	}
    	
    	Block block = event.getBlock();
    	
    	Material type = block.getType();
    	
		List<String> whitelist = (List<String>) fw.getSetting(WorldSetting.BLOCK_WHITELIST);
		List<String> blacklist = (List<String>) fw.getSetting(WorldSetting.BLOCK_BLACKLIST);
    	
		if (whitelist.contains("*"))
			event.setCancelled(false);
		
		else {
			
	    	List<Material> wlMaterials = whitelist.stream().map(m -> VersionUtils.material(m)).filter(m -> m != null).collect(Collectors.toList());
	    	
	    	event.setCancelled(wlMaterials.contains(type));
	    	
		}
    	
		if (blacklist.contains("*"))
			event.setCancelled(true);
		
		else {

	    	List<Material> blMaterials = blacklist.stream().map(m -> VersionUtils.material(m)).filter(m -> m != null).collect(Collectors.toList());
	    	
			if (blMaterials.contains(type))
				event.setCancelled(true);
			
		}
		
    }
	
	@SuppressWarnings("unchecked")
	@EventHandler(priority = EventPriority.HIGH)
    public void event(BlockPlaceEvent event) {
		
		if (event.isCancelled())
			return;
		
    	Player player = event.getPlayer();
    	
    	if (player.getGameMode() == GameMode.CREATIVE)
    		return;
		
    	if (player.hasMetadata("mode.build"))
    		return;
    	
    	FleXPlayer fp = Fukkit.getPlayer(player.getUniqueId());
    	FleXWorld fw = fp.getWorld();
    	
    	if (fp.getState().isImpervious()) {
    		event.setCancelled(true);
    		return;
    	}
    	
    	if (fw == null)
    		return;
    	
    	if (!(boolean)fw.getSetting(WorldSetting.BLOCK_PLACE)) {
    		event.setCancelled(true);
    		return;
    	}
    	
    	Block block = event.getBlock();
    	
    	Material type = block.getType();
    	
		List<String> whitelist = (List<String>) fw.getSetting(WorldSetting.BLOCK_WHITELIST);
		List<String> blacklist = (List<String>) fw.getSetting(WorldSetting.BLOCK_BLACKLIST);
		
		if (whitelist.contains("*"))
			event.setCancelled(false);
		
		else {
			
	    	List<Material> wlMaterials = whitelist.stream().map(m -> VersionUtils.material(m)).filter(m -> m != null).collect(Collectors.toList());
	    	
	    	event.setCancelled(wlMaterials.contains(type));
	    	
		}
		
		if (blacklist.contains("*"))
			event.setCancelled(true);
		
		else {

	    	List<Material> blMaterials = blacklist.stream().map(m -> VersionUtils.material(m)).filter(m -> m != null).filter(m -> m != null).collect(Collectors.toList());
	    	
			if (blMaterials.contains(type))
				event.setCancelled(true);
			
		}
		
    }
	
}
