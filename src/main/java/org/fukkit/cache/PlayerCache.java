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
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.PlayerState;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.FleXEventListener;
import org.fukkit.reward.Rank;
import org.fukkit.utils.BukkitUtils;

import io.flex.FleX.Task;
import io.flex.commons.cache.LinkedCache;
import io.flex.commons.sql.SQLCondition;
import io.flex.commons.sql.SQLRowWrapper;

import net.md_5.fungee.ProtocolVersion;

public class PlayerCache extends LinkedCache<FleXHumanEntity, HumanEntity> {

	/**
	 * 
	 * This is where all bungeecord related information is stored as a player is logging in.
	 * It's also how FleXPlayer objects are able to load ranks and names on the main thread without blocking.
	 *
	 */
	public static class PlayerCacheMeta extends FleXEventListener {
		
		private UUID uuid;
		private String name, rank, domain;
		
		private ProtocolVersion version = ProtocolVersion.UNSPECIFIED;
		
		public PlayerCacheMeta(UUID uniqueId) {
			this.uuid = uniqueId;
		}
		
		public String getName() {
			return this.name;
		}
		
		public Rank getRank() {
			return Memory.RANK_CACHE.getOrDefault(this.rank, Memory.RANK_CACHE.getDefaultRank());
		}
		
		public ProtocolVersion getVersion() {
			return this.version;
		}
		
		public String getDomain() {
			return this.domain;
		}
		
		public void setName(String name) {
			this.name = name;
			micro_cache.put(this.uuid, this);
		}
		
		public void setRank(Rank rank) {
			this.rank = rank != null ? rank.getName() : null;
			micro_cache.put(this.uuid, this);
		}
		
		public void setVersion(ProtocolVersion version) {
			this.version = version;
			micro_cache.put(this.uuid, this);
		}
		
		public void setDomain(String domain) {
			this.domain = domain;
			micro_cache.put(this.uuid, this);
		}
		
	}
	
	static {
		
		new FleXEventListener() {
			
			@EventHandler
			public void event(AsyncPlayerPreLoginEvent event) {
				
				try {
					
					UUID uid = event.getUniqueId();
					
					SQLRowWrapper row = Fukkit.getConnectionHandler().getDatabase().getRow("flex_user", SQLCondition.where("uuid").is(uid));
					
					if (row != null) {
						
						String r = row.getString("rank");
						
						if (r == null)
							return;
						
						String name = event.getName();
						Rank rank = Memory.RANK_CACHE.getOrDefault(r, Memory.RANK_CACHE.getDefaultRank());
						
						PlayerCacheMeta meta = getCachedAttributes(uid);
						
						meta.setName(name);
						meta.setRank(rank);
						
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
			
		};
		
	}
	
	private static final Map<UUID, PlayerCacheMeta> micro_cache = new HashMap<UUID, PlayerCacheMeta>();
	
	public static int getMemberAmount() {
		return micro_cache.size();
	}
	
	private static final long serialVersionUID = 898674831597070205L;

	public PlayerCache() {
		super((fp, player) -> fp.getUniqueId().equals(player.getUniqueId()));
	}
	
	public static PlayerCacheMeta getCachedAttributes(UUID uniqueId) {
		return micro_cache.getOrDefault(uniqueId, new PlayerCacheMeta(uniqueId));
	}
	
	@Override
	public boolean add(FleXHumanEntity... args) {
		
		for (FleXHumanEntity entity : args)
			getCachedAttributes(entity.getUniqueId()).setRank(entity.getRank());
		
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
	
	public void getAsync(UUID uuid, Consumer<FleXHumanEntity> callback) {
		BukkitUtils.asyncThread(() -> {
			
			FleXHumanEntity player = this.getByUniqueId(uuid);
			
			BukkitUtils.mainThread(() -> callback.accept(player));
			
		});
	}
	
	public void getAsync(String name, Consumer<FleXHumanEntity> callback) {
		BukkitUtils.asyncThread(() -> {
			
			FleXHumanEntity player = this.getByName(name);
			
			BukkitUtils.mainThread(() -> callback.accept(player));
			
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
		
		if (pl == null) {
			
			PlayerCacheMeta meta = micro_cache.get(uuid);
			
			if (meta != null)
				pl = Fukkit.getPlayerFactory().createFukkitSafe(uuid, meta.getName());
			
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
			
			for (Entry<UUID, PlayerCacheMeta> entry : micro_cache.entrySet()) {
				
				UUID u = entry.getKey();
				String n = entry.getValue().name;
				
				if (n != null && n.equalsIgnoreCase(name)) {
					
					pl = this.getByUniqueId(u);
					
					if (pl == null)
						pl = Fukkit.getPlayerFactory().createFukkitSafe(u, n);
					
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
					
					String uid = row.getString("uuid");
					String name = row.getString("name");
					String rank = row.getString("rank");
					
					UUID uuid = UUID.fromString(uid);
					
					Task.print("Players", "Caching " + rank + " \"" + name + "\" (" + uid.substring(0, 8) + "...) [" + (i++) + "/" + amount + "]");
					
					PlayerCacheMeta meta = getCachedAttributes(uuid);
					
					meta.setName(name);
					meta.setRank(Memory.RANK_CACHE.getOrDefault(rank, Memory.RANK_CACHE.getDefaultRank()));
					
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
