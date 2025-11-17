package org.fukkit.cache;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.PlayerState;
import org.fukkit.entity.FleXBot;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.FleXEventListener;
import org.fukkit.event.player.PlayerMicroCacheUpdateEvent;
import org.fukkit.reward.Rank;
import org.fukkit.theme.Theme;
import org.fukkit.utils.BukkitUtils;

import com.google.common.base.Objects;

import io.flex.FleX.Task;
import io.flex.commons.cache.LinkedCache;
import io.flex.commons.socket.Data;
import io.flex.commons.sql.SQLCondition;
import io.flex.commons.sql.SQLRowWrapper;
import io.flex.commons.utils.CollectionUtils;

import net.md_5.fungee.ProtocolVersion;
import net.md_5.fungee.event.AsyncDataReceivedEvent;

public class PlayerCache extends LinkedCache<FleXHumanEntity, HumanEntity> {

	/**
	 * 
	 * This is where all bungeecord related information is stored as a player is logging in.
	 * It's also how FleXPlayer objects are able to load ranks, names and other important objects on the main thread without blocking.
	 *
	 */
	public static class PlayerCacheMeta {
		
		public static final String CACHE_BRIDGE_KEY = "PLAYER_CACHE_META";
		
		public enum PlayerCacheMetaType {
			
		    NAME(o -> o),
		    RANK(o -> o != null ? (o instanceof String ? (String)o : ((Rank)o).getName()) : null),
		    THEME(o -> o != null ? o instanceof String ? (String)o : ((Theme)o).getName() : null),
		    DOMAIN(o -> o),
		    VERSION(o -> {
		    	
		        if (o instanceof ProtocolVersion)
		            return ((ProtocolVersion) o).toRecommendedProtocol();
		        
		        else if (o instanceof Number)
		            return ((Number)o).intValue();
		        
		        else if (o instanceof String)
		            return Integer.parseInt((String)o);
		        
		        return -1;
		        
		    });
			
		    private final Function<Object, ?> cachedValue;
		    
		    PlayerCacheMetaType(Function<Object, ?> cachedValue) {
		        this.cachedValue = cachedValue;
		    }
		    
		    @SuppressWarnings("unchecked")
		    public <R> R cachedValueOf(Object obj) {
		        return (R) this.cachedValue.apply(obj);
		    }
		    
		}
		
		private UUID uuid;
		private String name, rank, theme, domain;
		private int version = -1;
		
		public PlayerCacheMeta(UUID uniqueId) {
			this.uuid = uniqueId;
		}
		
		public UUID getUniquId() {
			return uuid;
		}
		
		public String getName() {
			return this.name;
		}
		
		public Rank getRank() {
			return Memory.RANK_CACHE.getOrDefault(this.rank, Memory.RANK_CACHE.getDefaultRank());
		}
		
		public ProtocolVersion getVersion() {
			return ProtocolVersion.fromProtocol(this.version);
		}
		
		public String getDomain() {
			return this.domain;
		}
		
		public Theme getTheme() {
			return Memory.THEME_CACHE.getOrDefault(this.theme, Memory.THEME_CACHE.getDefaultTheme());
		}
		
		public void setName(String name) {
			this.update(PlayerCacheMetaType.NAME, name);
		}
		
		public void setRank(Rank rank) {
			this.update(PlayerCacheMetaType.RANK, rank);
		}
		
		public void setTheme(Theme theme) {
			this.update(PlayerCacheMetaType.THEME, theme);
		}
		
		public void setVersion(ProtocolVersion version) {
			this.update(PlayerCacheMetaType.VERSION, version);
		}
		
		public void setDomain(String domain) {
			this.update(PlayerCacheMetaType.DOMAIN, domain);
		}
		
		public void update(PlayerCacheMetaType type, Object obj) {
			
			if (this.uuid == null)
				throw new IllegalStateException("uuid cannot be null.");
			
			/**
			 * Each null condition makes sure PlayerMicroCacheUpdateEvent isn't recursively called.
			 */
			switch (type) {
			case NAME:
				
				String name = type.cachedValueOf(obj);
				
				if (Objects.equal(this.name, name))
					return;
				
				this.name = name;
				break;
				
			case RANK:
				
				String rank = type.cachedValueOf(obj);
				
				if (Objects.equal(this.rank, rank))
					return;
				
				this.rank = rank;
				break;
				
			case THEME:
				
				String theme = type.cachedValueOf(obj);
				
				if (Objects.equal(this.theme, theme))
					return;
				
				this.theme = theme;
				break;
				
			case DOMAIN:
				
				String domain = type.cachedValueOf(obj);
				
				if (Objects.equal(this.domain, domain))
					return;
				
				this.domain = domain;
				break;
				
			case VERSION:
				
				int check = type.cachedValueOf(obj);
				
				if (this.version == check)
					return;
				
				this.version = check;
				break;
				
			default:
				return;
			}
			
			micro_cache.put(this.uuid, this);
			
			Fukkit.getEventFactory().call(new PlayerMicroCacheUpdateEvent(this, type, obj));
			
		}
		
		// This is so new meta data doesn't spam other servers with update events.
		public static PlayerCacheMeta cache(SQLRowWrapper row) {
			
			String uid = row.getString("uuid");
			
			UUID uuid = UUID.fromString(uid);
			
			PlayerCacheMeta meta = new PlayerCacheMeta(uuid);
			
			String name = row.getString("name");
			String rank = row.getString("rank");
			
			meta.setName(name);
			meta.setRank(Memory.RANK_CACHE.getOrDefault(rank, Memory.RANK_CACHE.getDefaultRank()));
			
			return meta;
			
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
			
			@EventHandler
			public void event(AsyncDataReceivedEvent event) {
				
				Data data = event.getData();
				
				if (!data.getKey().equalsIgnoreCase(PlayerCacheMeta.CACHE_BRIDGE_KEY))
					return;
				
				Map<String, String> map = CollectionUtils.toMap(data.getValue());
				
				String uid = map.get("uuid");
				
				if (uid == null)
					return;
				
				UUID uuid = UUID.fromString(map.get("uuid"));
				
				if (uuid == null)
					return;
				
				PlayerCacheMeta meta = getCachedAttributes(uuid);
				
				map.forEach((k, v) -> meta.update(PlayerCacheMeta.PlayerCacheMetaType.valueOf(k), v));
				
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
			if (entity instanceof FleXBot == false)
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
			
			if (meta != null) {
				
				if (meta.getName() == null) {
					
					if (player != null)
						meta.setName(player.getName());
					
					else return null;
					
				}
				
				pl = Fukkit.getPlayerFactory().createFukkitSafe(uuid, meta.getName());
				
			}
			
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
					
					PlayerCacheMeta meta = PlayerCacheMeta.cache(row);
					
					Task.print("Players", "Cached " + meta.getRank().getName() + " \"" + meta.getName() + "\" (" + meta.getUniquId().toString().substring(0, 8) + "...) [" + (i++) + "/" + amount + "]");
					
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