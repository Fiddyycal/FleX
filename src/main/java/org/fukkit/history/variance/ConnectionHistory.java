package org.fukkit.history.variance;

import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.history.History;

import net.md_5.fungee.ConnectionReason;

public class ConnectionHistory extends History<String> {

	public static final String TABLE_NAME = "flex_history_connection";
	
	public ConnectionHistory(FleXHumanEntity player) {
		super(player, "flex_history_connection");
	}
	
	public Map<Long, String> connectionMap() {
		return this.log.entrySet().stream().filter(e -> !e.getValue().contains(":")).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
	}
	
	public void onConnect(ConnectionReason reason) {
		this.add("-> " + Bukkit.getServer().getName() + " [" + reason + "]");
	}
	
	public void onDisconnect(ConnectionReason reason) {
		this.add("-X " + Bukkit.getServer().getName() + " [" + reason + "]");
	}

}
