package org.fukkit.cache;

import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.fukkit.Fukkit;
import org.fukkit.PlayerState;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.utils.BukkitUtils;

import io.flex.FleX.Task;
import io.flex.commons.cache.ConcurrentPerformanceCache;
import io.flex.commons.sql.SQLCondition;
import io.flex.commons.sql.SQLRow;
import io.flex.commons.utils.StringUtils;

public class PlayerCache extends ConcurrentPerformanceCache<FleXHumanEntity, UUID> {
	
	private static int members = -1;
	
	public static int getMemberAmount() {
		
		if (Bukkit.isPrimaryThread())
			throw new IllegalStateException("This method cannot be called from the primary thread.");
		
		if (members == 0) {
			try {
				
				Task.debug("FleX", "Counting members... (async)");
				
				Set<SQLRow> rows = Fukkit.getConnectionHandler().getDatabase().result("SELECT COUNT(*) FROM flex_user");
				
				if (!rows.isEmpty())
					members = rows.stream().findFirst().orElse(null).getInt("COUNT(*)");
				
				rows.clear();
				
				Task.print("FleX", "Done!");
			
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return members;
		
	}
	
	private static final long serialVersionUID = 898674831597070205L;
	
	public PlayerCache() {
		super(fp -> fp.getUniqueId());
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
	
	public void getFromDatabaseAsync(UUID uuid, Consumer<FleXHumanEntity> callback) {
		BukkitUtils.asyncThread(() -> {
			
			FleXHumanEntity player = this.getByUniqueId(uuid);
			
			if (player == null) {
				
				try {
					
					SQLRow row = Fukkit.getConnectionHandler().getDatabase().getRow("flex_user", SQLCondition.where("uuid").is(uuid));
					
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
			
			boolean uid = StringUtils.isUUID(name);
			
			FleXHumanEntity player = uid ? this.getByUniqueId(UUID.fromString(name)) : this.getByName(name, true);
			
			if (player == null) {
				
				try {
					
					SQLRow row = Fukkit.getConnectionHandler().getDatabase().getRow("flex_user", SQLCondition.where(uid ? "uuid" : "name").is(name));
					
					if (row != null)
						player = Fukkit.getPlayerFactory().createFukkitSafe(UUID.fromString(row.getString("uuid")), name, PlayerState.OFFLINE);
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
			
			callback.accept(player);
			
		});
	}
	
	public FleXHumanEntity getFromDatabase(String name) {
		
		if (Bukkit.isPrimaryThread())
			throw new IllegalStateException("This method cannot be called from the primary thread.");
		
		if (StringUtils.isUUID(name))
			return this.getFromDatabase(UUID.fromString(name));
		
		FleXHumanEntity player = this.getByName(name, true);
		
		if (player == null) {
			
			try {
				
				SQLRow row = Fukkit.getConnectionHandler().getDatabase().getRow("flex_user", SQLCondition.where("name").is(name));
				
				if (row != null)
					player = Fukkit.getPlayerFactory().createFukkitSafe(UUID.fromString(row.getString("uuid")), name, PlayerState.OFFLINE);
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		
		return player;
		
	}
	
	public FleXHumanEntity getFromDatabase(UUID uuid) {
		
		if (Bukkit.isPrimaryThread())
			throw new IllegalStateException("This method cannot be called from the primary thread.");
		
		FleXHumanEntity player = this.getByUniqueId(uuid);
		
		if (player == null) {
			
			try {
				
				SQLRow row = Fukkit.getConnectionHandler().getDatabase().getRow("flex_user", SQLCondition.where("uuid").is(uuid));
				
				if (row != null)
					player = Fukkit.getPlayerFactory().createFukkitSafe(uuid, row.getString("name"), PlayerState.OFFLINE);
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		
		return player;
		
	}
	
	public FleXHumanEntity getByUniqueId(UUID uuid) {
		
		FleXHumanEntity fh = this.get(uuid);
		
		if (fh == null) {

			Player player = Bukkit.getPlayer(uuid);
			
			if (player != null)
				fh = this.getByMeta(player);
			
		}
		
		return fh;
		
	}
	
	public FleXHumanEntity getByName(String name, boolean ignoreDisguise) {
		
	    FleXHumanEntity fh = null;
	    
	    for (FleXHumanEntity human : this) {
	    	
	        if (!ignoreDisguise && human instanceof FleXPlayer) {
	        	
	            FleXPlayer fp = (FleXPlayer) human;
	            
	            if (fp.isDisguised() && fp.getDisguise() != null && fp.getDisguise().getName().equalsIgnoreCase(name)) {

	                fh = human;
	                break;
	            }
	            
	        }
	        
	        if (human.getName().equalsIgnoreCase(name)) {
	        	
	            if (human instanceof FleXPlayer) {
	            	
	                FleXPlayer fp = (FleXPlayer) human;
	                
	                if (!ignoreDisguise && fp.isDisguised() && fp.getDisguise() != null && !fp.getDisguise().getName().equalsIgnoreCase(name))
	                    continue;
	                
	            }
	            
	            fh = human;
	            break;
	            
	        }
	        
	    }
	    
	    if (fh == null) {
	    	
	        Player player = Bukkit.getPlayer(name);
	        
	        if (player != null)
	            fh = this.getByMeta(player);
	        
	    }
	    
	    return fh;
	    
	}
	
	public FleXPlayer getByMeta(Player player) {
		return player.hasMetadata(null) ? (FleXPlayer) player.getMetadata("flex.player").get(0).value() : null;
	}
	
	@Override
	public boolean load() {
		return true;
	}

}