package org.fukkit.history.variance;

import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Collectors;

import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.history.History;

public class IPHistory extends History<String> {

	/**
	 * 
	 * 
	 * 
		// getAddress() is a resource heavy task that blocks, this ensures that it updates async.
		BukkitUtils.asyncThread(() -> this.ip = this.getPlayer().getAddress() != null ? this.getPlayer().getAddress().getHostName() : null);
	 * 
	 */
	
	public static final String TABLE_NAME = "flex_history_ip";
	
	public IPHistory(FleXHumanEntity player) throws SQLException {
		super(player, "flex_history_ip");
	}

	@Override
	public void add(String log) {
		
		String last = this.log.values().stream().skip(this.log.isEmpty() ? 0 : this.log.size()-1).findFirst().orElse(null);
		
		if (last == null || !last.equals(log))
			super.add(log);
		
	}
	
	public Set<String> ipSet() {
		return this.log.values().stream().collect(Collectors.toSet());
	}

}
