package org.fukkit.history;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.fukkit.Fukkit;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.utils.BukkitUtils;

import io.flex.commons.Nullable;
import io.flex.commons.sql.SQLCondition;
import io.flex.commons.sql.SQLDatabase;
import io.flex.commons.sql.SQLMap;

public abstract class History<T> {
	
	protected FleXHumanEntity player;
	
	protected Map<Long, T> log = new LinkedHashMap<Long, T>();
	
	private String table;
	
	public History(FleXHumanEntity player) throws SQLException {
		this(player, null);
	}
	
	@SuppressWarnings("unchecked")
	public History(FleXHumanEntity player, @Nullable String table) throws SQLException {
		
		this.player = player;
		
		// Table is only used to store local data
		if ((this.table = table) == null)
			return;
		
		SQLDatabase database = Fukkit.getConnectionHandler().getDatabase();
		
		try {
			
			database.getRows(table, SQLCondition.where("uuid").is(player.getUniqueId())).forEach(r -> {
				try {
					this.log.put(r.getLong("time"), (T) r.get("log"));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			});
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public T getLast(int howFar) {
		
		int i = this.log.values().size() - (1 + howFar);
		
		return i > -1 ? this.log.values().stream().skip(i).findFirst().orElse(null) : null;
		
	}

	public T getLastest() {
		return this.getLast(0);
	}
	
	public void add(T log) {
		
		long time = System.currentTimeMillis();
		
		this.log.put(time, log);
		
		if (this.table == null)
			return;
		
		BukkitUtils.asyncThread(() -> {
			
			SQLDatabase database = Fukkit.getConnectionHandler().getDatabase();
			
			try {
				
				database.addRow(this.table, SQLMap.of(
						
						SQLMap.entry("uuid", this.player.getUniqueId()),
						SQLMap.entry("time", time),
						SQLMap.entry("log", log)));
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		});
		
	}

	/**
	 * @deprecated
	 * Can't remove rows yet.
	 */
	@Deprecated
	public void remove(long time) {
		
		if (!this.log.containsKey(time))
			return;
		
		this.log.remove(time);
		
		if (this.table == null)
			return;
		
		// TODO when remove rows is added to SQLDatabase use it here...
		
	}
	
	public String asDateFormat(T log) {
		
		if (!this.log.containsValue(log))
			return null;
		
		Entry<Long, T> entry = this.log.entrySet().stream().filter(e -> e.getValue() == log).findFirst().orElse(null);
		
		SimpleDateFormat date = new SimpleDateFormat("(dd/MM/yy) [HH:mm:ss z]");
		Date now = new Date(entry.getKey());
		
		return date.format(now);
		
	}

	public Map<Long, T> asMap() {
		return this.log;
	}
	
}
