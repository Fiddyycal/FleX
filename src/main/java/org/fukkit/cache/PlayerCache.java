package org.fukkit.cache;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.PlayerState;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.reward.Rank;
import org.fukkit.utils.BukkitUtils;

import io.flex.FleX.Task;
import io.flex.commons.cache.LinkedCache;
import io.flex.commons.sql.SQLRowWrapper;

public class PlayerCache extends LinkedCache<FleXHumanEntity, HumanEntity> {

	public class Meta {
		
		private String name;
		private Rank rank;
		
		public Meta(String name, String rank) {
			this.name = name;
			this.rank = Memory.RANK_CACHE.getOrDefault(rank, Memory.RANK_CACHE.getDefaultRank());
		}
		
		public String getName() {
			return this.name;
		}
		
		public Rank getRank() {
			return this.rank;
		}
		
	}
	
	private static final Map<UUID, Meta> micro_cache = new HashMap<UUID, Meta>();
	
	public static int getMemberAmount() {
		return micro_cache.size();
	}
	
	private static final long serialVersionUID = 898674831597070205L;

	public PlayerCache() {
		super((fp, player) -> fp.getUniqueId().equals(player.getUniqueId()));
	}
	
	public static Map<UUID, Meta> getMicroCache() {
		return micro_cache;
	}
	
	@Override
	public boolean add(FleXHumanEntity... args) {
		
		for (FleXHumanEntity entity : args)
			micro_cache.put(entity.getUniqueId(), new Meta(entity.getName(), entity.getRank().getName()));
		
		return super.add(args);
		
	}
	
	@Override
	public boolean remove(FleXHumanEntity... args) {
		
		/**
		 * Must allow onDisconnect some time for any async logic to finish up.
		 */
		BukkitUtils.runLater(() -> this.remove(args), 5L, true);
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
	
	public void getAsync(UUID uuid, Consumer<FleXHumanEntity> callback) {
		// TODO Make get async method in PlayerController
	}
	
	public FleXHumanEntity getSafe(UUID uuid) {
		return this.stream().filter(p -> p.getUniqueId() != null && p.getUniqueId().equals(uuid)).findFirst().orElse(null);
	}
	
	public FleXHumanEntity getByUniqueId(UUID uuid) {
		
		Player player = Bukkit.getPlayer(uuid);
		FleXHumanEntity pl = null;
		
		if (player != null)
			pl = this.getByMeta(player);
		
		if (pl == null) {
			pl = this.getSafe(uuid);
			
			if (pl != null && player != null && player.isOnline() && pl.getState() != PlayerState.DISCONNECTING)
				player.setMetadata("flex.player", new FixedMetadataValue(Fukkit.getInstance(), pl));
				
		}
		
		if (pl == null) {
			
			String name = micro_cache.get(uuid).getName();
			
			if (name != null)
				pl = Fukkit.getPlayerFactory().createFukkitSafe(uuid, name, null);
			
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
		
		if (pl == null)
			
			for (Entry<UUID, Meta> entry : micro_cache.entrySet()) {
				
				UUID u = entry.getKey();
				String n = entry.getValue().name;
				
				if (n.equalsIgnoreCase(name)) {
					
					pl = this.getByUniqueId(u);
					
					if (pl == null)
						pl = Fukkit.getPlayerFactory().createFukkitSafe(u, n, null);
					
					break;
					
				}
				
			}
		
		return pl;
		
	}
	
	public FleXPlayer getByMeta(Player player) {
		return player.hasMetadata(null) ? (FleXPlayer) player.getMetadata("flex.player").get(0).value() : null;
	}
	
	@Override
	public boolean load() {
	
		Task.print("Players", "Loading player data...");
		
		try {
			
			Set<SQLRowWrapper> rows = Fukkit.getConnectionHandler().getDatabase().getRows("flex_user");
			
			int amount = rows.size();
			
			int i = 1;
			
			for (SQLRowWrapper row : rows) {
				
				try {
					
					String uuid = row.get("uuid").toString();
					String name = row.getString("name");
					String rank = row.getString("rank");
					
					Task.print("Players", "Caching " + rank + " \"" + row.getString("name") + "\" (" + row.getString("uuid").substring(0, 8) + "...) [" + (i++) + "/" + amount + "]");
					
					micro_cache.put(UUID.fromString(uuid), new Meta(name, rank));
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
		} catch (NullPointerException | SQLException e) {
			e.printStackTrace();
		}
		
		Task.print("Players", "Done!");
		return true;
		
	}

}
