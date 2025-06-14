package org.fukkit.history;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.fukkit.Fukkit;
import org.fukkit.entity.FleXHumanEntity;

import io.flex.commons.Nullable;
import io.flex.commons.sql.SQLCondition;
import io.flex.commons.sql.SQLDatabase;
import io.flex.commons.sql.SQLDataType;

public abstract class History<T> {
	
	protected FleXHumanEntity player;
	
	protected Map<Long, T> log = new LinkedHashMap<Long, T>();
	
	private String table;
	
	public History(FleXHumanEntity player) {
		this(player, null);
	}
	
	@SuppressWarnings("unchecked")
	public History(FleXHumanEntity player, @Nullable String table) {
		
		this.player = player;
		
		if ((this.table = table) == null)
			return;
		
		SQLDatabase database = Fukkit.getConnectionHandler().getDatabase();
		
		LinkedHashMap<String, SQLDataType> columns = new LinkedHashMap<String, SQLDataType>();
		
		columns.put("uuid", SQLDataType.VARCHAR);
		columns.put("time", SQLDataType.BIGINT);
		columns.put("log", SQLDataType.VARCHAR);
		
		try {
			database.createTable(table, "uuid", columns);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
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

		SQLDatabase database = Fukkit.getConnectionHandler().getDatabase();
		
		try {
			
			LinkedHashMap<String, Object> entries = new LinkedHashMap<String, Object>();
			
			entries.put("uuid", this.player.getUniqueId());
			entries.put("time", time);
			entries.put("log", log);
			
			database.addRow(this.table, entries);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
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
