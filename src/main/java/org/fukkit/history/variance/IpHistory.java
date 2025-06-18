package org.fukkit.history.variance;

import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Collectors;

import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.history.History;

public class IpHistory extends History<String> {

	public static final String TABLE_NAME = "flex_history_ip";
	
	public IpHistory(FleXHumanEntity player) throws SQLException {
		super(player, "flex_history_ip");
	}

	@Override
	public void add(String log) {
		
		String test = this.log.values().stream().skip(this.log.size()-1).findFirst().orElse(null);
		
		if (test == null || !test.equals(log))
			super.add(log);
		
	}
	
	public Set<String> ipSet() {
		return this.log.values().stream().collect(Collectors.toSet());
	}

}
