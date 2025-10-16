package org.fukkit.handlers;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.fukkit.Fukkit;
import org.fukkit.config.Configuration;
import org.fukkit.config.YamlConfig;
import org.fukkit.data.BukkitLocalDataServer;
import org.fukkit.history.variance.BadgeHistory;
import org.fukkit.history.variance.ChatCommandHistory;
import org.fukkit.history.variance.ConnectionHistory;
import org.fukkit.history.variance.DisguiseHistory;
import org.fukkit.history.variance.IpHistory;
import org.fukkit.history.variance.NameHistory;
import org.fukkit.history.variance.RankHistory;

import io.flex.FleX;
import io.flex.FleX.Task;
import io.flex.commons.Severity;
import io.flex.commons.console.Console;
import io.flex.commons.socket.DataServer;
import io.flex.commons.sql.SQLDatabase;
import io.flex.commons.sql.SQLDataType;
import io.flex.commons.sql.SQLDriverType;
import io.flex.commons.sql.SQLMap;

public class ConnectionHandler {
	
	private static boolean registered = false;
	
	private SQLDatabase database;
	
	private DataServer server;
	
	public ConnectionHandler() {
		
		if (registered)
			return;
		
		@SuppressWarnings("deprecation")
		YamlConfig sql = Fukkit.getResourceHandler().getYaml(Configuration.SQL);
		FileConfiguration config = sql.getConfig();
		
		String driver = config.getString("Driver", SQLDriverType.MYSQL.name());
		String hostname = config.getString("Credentials.Host", "localhost");
		
		int port = config.getInt("Credentials.Port", 3306);
		
		String database = config.getString("Credentials.Database", "flex_db");
		String username = config.getString("Credentials.Username", "root");
		String password = config.getString("Credentials.Password", "foobar");
		
		try {
			
			SQLDriverType type = SQLDriverType.valueOf(driver);
			
			if (type == SQLDriverType.SQLITE && config.contains("SQLite-Path")) {
				
				String path = config.getString("SQLite-Path", FleX.EXE_PATH + "/flex/data/sqlite");
				
				path = path.replace("${server_absolute_path}", FleX.EXE_PATH);
				path = path.replace("${volumes_absolute_path}", new File(FleX.EXE_PATH).getParentFile().getAbsolutePath());
				
				this.database = new SQLDatabase(hostname, port, database, username, password, path);
				
			}
				
			else this.database = new SQLDatabase(hostname, port, database, username, password, SQLDriverType.valueOf(driver));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (this.database == null || !this.database.hasConnection())
			return;
		
		try {
			
			LinkedHashMap<String, SQLDataType> user_columns = new LinkedHashMap<String, SQLDataType>();
			
			user_columns.put("uuid", SQLDataType.VARCHAR);
			user_columns.put("name", SQLDataType.VARCHAR);
			user_columns.put("version", SQLDataType.INT);
			user_columns.put("domain", SQLDataType.VARCHAR);
			user_columns.put("currency", SQLDataType.BIGINT);
			user_columns.put("play_time", SQLDataType.BIGINT);
			user_columns.put("last_seen", SQLDataType.BIGINT);
			user_columns.put("rank", SQLDataType.VARCHAR);
			user_columns.put("badge", SQLDataType.VARCHAR);
			user_columns.put("theme", SQLDataType.VARCHAR);
			user_columns.put("language", SQLDataType.VARCHAR);
			user_columns.put("skin", SQLDataType.TEXT);
			
			this.database.createTable("flex_user", "uuid", user_columns);
			
			LinkedHashMap<String, SQLDataType> punishment_columns = new LinkedHashMap<String, SQLDataType>();
			
			punishment_columns.put("id", SQLDataType.BIGINT);
			punishment_columns.put("uuid", SQLDataType.VARCHAR);
			punishment_columns.put("by", SQLDataType.VARCHAR);
			punishment_columns.put("time", SQLDataType.BIGINT);
			punishment_columns.put("until", SQLDataType.BIGINT);
			punishment_columns.put("type", SQLDataType.VARCHAR);
			punishment_columns.put("reason", SQLDataType.VARCHAR);
			punishment_columns.put("evidence", SQLDataType.VARCHAR);
			punishment_columns.put("ip", SQLDataType.BOOLEAN);
			punishment_columns.put("silent", SQLDataType.BOOLEAN);
			punishment_columns.put("pardoned", SQLDataType.BOOLEAN);
			
			this.database.createTable("flex_punishment", "id", punishment_columns);
			
			LinkedHashMap<String, SQLDataType> recording_columns = new LinkedHashMap<String, SQLDataType>();
			
			recording_columns.put("uuid", SQLDataType.VARCHAR);
			recording_columns.put("context", SQLDataType.VARCHAR);
			recording_columns.put("time", SQLDataType.VARCHAR);
			recording_columns.put("state", SQLDataType.VARCHAR);
			recording_columns.put("world", SQLDataType.VARCHAR);
			recording_columns.put("players", SQLDataType.VARCHAR);
			recording_columns.put("data", SQLDataType.BLOB);
			
			this.database.createTable("flex_recording", recording_columns);
			
			try {
				this.database.execute("ALTER TABLE flex_recording MODIFY COLUMN data LONGBLOB");
				this.database.execute("ALTER TABLE flex_recording MODIFY players VARCHAR(2550)");
			} catch (Exception ignore) {}
			
			this.createHistoryTable(BadgeHistory.TABLE_NAME);
			this.createHistoryTable(ChatCommandHistory.TABLE_NAME);
			this.createHistoryTable(ConnectionHistory.TABLE_NAME);
			this.createHistoryTable(DisguiseHistory.TABLE_NAME);
			this.createHistoryTable(IpHistory.TABLE_NAME);
			this.createHistoryTable(NameHistory.TABLE_NAME);
			this.createHistoryTable(RankHistory.TABLE_NAME);
			
		} catch (SQLException e) {
			
			Task.error("SQL (" + Severity.EMERG + ")", "Failed to build local tables. (" + this.database + ")");
	    	Console.log("SQL", Severity.EMERG, e);
	    	return;
	    	
		}
		
		String bukkit = String.valueOf(Bukkit.getPort());
		
		if (bukkit.startsWith("1"))
			throw new UnsupportedOperationException("FleX does not support server ports beginning with 1, please change the server port to continue.");
		
		int data = Integer.valueOf("1" + bukkit.substring(1, bukkit.length()));
		
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
		
		this.database.createTable(table, SQLMap.of(
				
				SQLMap.entry("uuid", SQLDataType.VARCHAR),
				SQLMap.entry("time", SQLDataType.BIGINT),
				SQLMap.entry("log", SQLDataType.VARCHAR)));
		
	}
	
}
