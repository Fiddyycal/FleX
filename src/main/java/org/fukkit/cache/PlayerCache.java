package org.fukkit.cache;

import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.fukkit.Fukkit;
import org.fukkit.PlayerState;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.utils.BukkitUtils;

import io.flex.FleX.Task;
import io.flex.commons.cache.LinkedCache;
import io.flex.commons.sql.SQLCondition;
import io.flex.commons.sql.SQLRowWrapper;

public class PlayerCache extends LinkedCache<FleXHumanEntity, HumanEntity> {
	
	private static int members = 0;
	
	public static int getMemberAmount() {
		
		if (Bukkit.isPrimaryThread())
			throw new IllegalStateException("This method cannot be called from the primary thread.");
		
		try {
			
			Task.debug("FleX", "Counting members... (async)");
			
			Set<SQLRowWrapper> rows = Fukkit.getConnectionHandler().getDatabase().result("SELECT COUNT(*) AS total FROM flex_user");
			
			if (!rows.isEmpty())
				members = rows.stream().findFirst().orElse(null).getInt("total");
			
			rows.clear();
			
			Task.print("FleX", "Done!");
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return members;
		
	}
	
	private static final long serialVersionUID = 898674831597070205L;

	public PlayerCache() {
		super((fp, player) -> fp.getUniqueId().equals(player.getUniqueId()));
	}
	
	@Override
	public boolean add(FleXHumanEntity... args) {
		return super.add(args);
	}
	
	@Override
	public boolean remove(FleXHumanEntity... args) {
		for (FleXHumanEntity entity : args) this.removeIf(e -> entity.getUniqueId().equals(e.getUniqueId()));
		return true;
	}
	
	/**
	 * Because there will be a lot to go through,
	 * let's override with something more efficient.
	 */
	@Override
	public FleXHumanEntity get(HumanEntity player) {
		return player != null ? this.getByUniqueId(player.getUniqueId()) : null;
	}
	
	public void getFromDatabaseAsync(UUID uuid, Consumer<FleXHumanEntity> callback) {
		BukkitUtils.asyncThread(() -> {
			
			FleXHumanEntity player = this.getByUniqueId(uuid);
			
			if (player == null) {
				
				try {
					
					SQLRowWrapper row = Fukkit.getConnectionHandler().getDatabase().getRow("flex_user", SQLCondition.where("uuid").is(uuid));
					
					if (row != null)
						player = Fukkit.getPlayerFactory().createFukkitSafe(uuid, row.getString("name"), PlayerState.OFFLINE);
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
			
			callback.accept(player);
			
		});
	}
	
	public void getFromDatabaseAsync(String name, Consumer<FleXHumanEntity> callback) {
		BukkitUtils.asyncThread(() -> {
			
			FleXHumanEntity player = this.getByName(name);
			
			if (player == null) {
				
				try {
					
					SQLRowWrapper row = Fukkit.getConnectionHandler().getDatabase().getRow("flex_user", SQLCondition.where("name").is(name));
					
					if (row != null)
						player = Fukkit.getPlayerFactory().createFukkitSafe(UUID.fromString(row.getString("uuid")), name, PlayerState.OFFLINE);
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
			
			callback.accept(player);
			
		});
	}
	
	public FleXHumanEntity getFromCache(UUID uuid) {
		return this.stream().filter(p -> p.getUniqueId() != null && p.getUniqueId().equals(uuid)).findFirst().orElse(null);
	}
	
	public FleXHumanEntity getByUniqueId(UUID uuid) {
		
		Player player = Bukkit.getPlayer(uuid);
		FleXHumanEntity pl = null;
		
		if (player != null)
			pl = this.getByMeta(player);
		
		if (pl == null) {
			
			pl = this.getFromCache(uuid);
			
			if (pl != null && player != null && player.isOnline() && player.isValid() && pl.getState() != PlayerState.DISCONNECTING)
				player.setMetadata("flex.player", new FixedMetadataValue(Fukkit.getInstance(), pl));
				
		}
		
		return pl;
		
	}
	
	public FleXHumanEntity getByName(String name) {
		
		Player player = Bukkit.getPlayer(name);
		FleXHumanEntity pl = null;
		
		if (player != null)
			pl = this.getByMeta(player);
		
		if (pl == null) {
			
			pl = this.stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
			
			if (pl != null && player != null && player.isOnline() && pl.getState() != PlayerState.DISCONNECTING)
				player.setMetadata("flex.player", new FixedMetadataValue(Fukkit.getInstance(), pl));
				
		}
		
		return pl;
		
	}
	
	public FleXPlayer getByMeta(Player player) {
		return player.hasMetadata(null) ? (FleXPlayer) player.getMetadata("flex.player").get(0).value() : null;
	}
	
	@Override
	public boolean load() {
		return true;
	}

}