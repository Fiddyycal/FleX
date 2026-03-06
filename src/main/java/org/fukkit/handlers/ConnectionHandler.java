package org.fukkit.handlers;

import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.fukkit.Fukkit;
import org.fukkit.config.Configuration;
import org.fukkit.config.YamlConfig;
import org.fukkit.data.BukkitLocalDataServer;
import org.fukkit.history.variance.BadgeHistory;
import org.fukkit.history.variance.ChatCommandHistory;
import org.fukkit.history.variance.ConnectionHistory;
import org.fukkit.history.variance.DisguiseHistory;
import org.fukkit.history.variance.IPHistory;
import org.fukkit.history.variance.NameHistory;
import org.fukkit.history.variance.RankHistory;

import io.flex.FleX.Task;
import io.flex.commons.Severity;
import io.flex.commons.console.Console;
import io.flex.commons.socket.DataServer;
import io.flex.commons.sql.SQLDatabase;
import io.flex.commons.sql.SQLColumn;
import io.flex.commons.sql.SQLDataType;

public class ConnectionHandler {
	
	private static boolean registered = false;
	
	private SQLDatabase database;
	
	private DataServer server;
	
	public ConnectionHandler() {
		
		if (registered)
			return;
		
		@SuppressWarnings("deprecation")
		YamlConfig sql = Fukkit.getResourceHandler().getYaml(Configuration.SQL);
		
		String hostname = sql.getString("Credentials.Host", "localhost");
		
		int port = sql.getInt("Credentials.Port", 3306);
		
		String database = sql.getString("Credentials.Database", "flex_db");
		String username = sql.getString("Credentials.Username", "root");
		String password = sql.getString("Credentials.Password", "foobar");
		
		try {
				
			this.database = new SQLDatabase(hostname, port, database, username, password);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (this.database == null || !this.database.hasConnection())
			return;
		
		try {
			
			this.database.addTable("flex_user",
					
					SQLColumn.of("uuid", SQLDataType.VARCHAR, 36).primary(),
					SQLColumn.of("name", SQLDataType.VARCHAR, 16),
					SQLColumn.of("version", SQLDataType.INT),
					SQLColumn.of("domain", SQLDataType.VARCHAR, 255),
					SQLColumn.of("currency", SQLDataType.BIGINT),
					SQLColumn.of("play_time", SQLDataType.BIGINT),
					SQLColumn.of("last_seen", SQLDataType.BIGINT),
					SQLColumn.of("rank", SQLDataType.VARCHAR, 36),
					SQLColumn.of("badge", SQLDataType.VARCHAR, 36),
					SQLColumn.of("theme", SQLDataType.VARCHAR, 36),
					SQLColumn.of("language", SQLDataType.VARCHAR, 36),
					SQLColumn.of("skin", SQLDataType.TEXT))
			
			.createIfNotExists();
			
			this.database.addTable("flex_punishment",
					
					SQLColumn.of("id", SQLDataType.BIGINT).primary().autoIncrement(),
					SQLColumn.of("uuid", SQLDataType.VARCHAR, 36),
					SQLColumn.of("by", SQLDataType.VARCHAR, 36),
					SQLColumn.of("time", SQLDataType.VARCHAR, 255),
					SQLColumn.of("until", SQLDataType.BIGINT),
					SQLColumn.of("type", SQLDataType.VARCHAR, 36),
					SQLColumn.of("reason", SQLDataType.VARCHAR, 255),
					SQLColumn.of("evidence", SQLDataType.VARCHAR, 255),
					SQLColumn.of("ip", SQLDataType.BOOLEAN),
					SQLColumn.of("silent", SQLDataType.BOOLEAN),
					SQLColumn.of("pardoned", SQLDataType.BOOLEAN))
			
			.createIfNotExists();
			
			this.database.addTable("flex_disguise",
					
					SQLColumn.of("name", SQLDataType.VARCHAR, 32).primary(),
					SQLColumn.of("signature", SQLDataType.TEXT),
					SQLColumn.of("value", SQLDataType.TEXT),
					SQLColumn.of("signed", SQLDataType.BOOLEAN))
			
			.createIfNotExists();
			
			this.database.addTable("flex_recording",
					
					SQLColumn.of("uuid", SQLDataType.VARCHAR, 32),
					SQLColumn.of("context", SQLDataType.VARCHAR, 255).primary(),
					SQLColumn.of("time", SQLDataType.BIGINT),
					SQLColumn.of("duration", SQLDataType.BIGINT),
					SQLColumn.of("state", SQLDataType.VARCHAR, 16),
					SQLColumn.of("world", SQLDataType.VARCHAR, 32),
					SQLColumn.of("players", SQLDataType.VARCHAR, 2550),
					SQLColumn.of("data", SQLDataType.BLOB))
			
			.createIfNotExists();
			
			this.createHistoryTable(BadgeHistory.TABLE_NAME);
			this.createHistoryTable(ChatCommandHistory.TABLE_NAME);
			this.createHistoryTable(ConnectionHistory.TABLE_NAME);
			this.createHistoryTable(DisguiseHistory.TABLE_NAME);
			this.createHistoryTable(IPHistory.TABLE_NAME);
			this.createHistoryTable(NameHistory.TABLE_NAME);
			this.createHistoryTable(RankHistory.TABLE_NAME);
			
		} catch (SQLException e) {
			
			Task.error("SQL (" + Severity.EMERG + ")", "Failed to build local tables. (" + this.database + ")");
	    	Console.log("SQL", Severity.EMERG, e);
	    	return;
	    	
		}
		
		int p = Bukkit.getPort();
        
		if (p > 35535)
			throw new UnsupportedOperationException("Server ports cannot exceed 35535 due to data port allocation rules, please change the server port to continue.");
		
		int data = p + 35535;
		
		try {
			
			this.server = new BukkitLocalDataServer(data);
			this.server.start();
			
		} catch (IOException e) {
			throw new UnsupportedOperationException("An error occurred while attempting to create local data server: " + e.getMessage());
		}
		
		registered = true;
		
	}
	
	public SQLDatabase getDatabase() {
		return this.database;
	}
	
	public DataServer getLocalData() {
		return this.server;
	}
	
	public static boolean isRegistered() {
		return registered;
	}
	
	private void createHistoryTable(String table) throws SQLException {
		
		this.database.addTable(table,
				
				SQLColumn.of("uuid", SQLDataType.VARCHAR).primary(),
				SQLColumn.of("time", SQLDataType.BIGINT),
				SQLColumn.of("log", SQLDataType.VARCHAR, 255));
		
	}
	
}
