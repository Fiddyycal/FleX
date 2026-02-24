package org.fukkit.history.variance;

import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Collectors;

import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.history.History;

public class IPHistory extends History<String> {
	
	public static final String TABLE_NAME = "flex_history_ip";
	
	public IPHistory(FleXHumanEntity player) throws SQLException {
		super(player, TABLE_NAME);
	}

	@Override
	public void add(String log) {
		
	    String last = this.getLastest();
		
		if (last == null || !last.equals(log))
			super.add(log);
		
	}
	
	public Set<String> ipSet() {
		return this.log.values().stream().collect(Collectors.toSet());
	}

}
