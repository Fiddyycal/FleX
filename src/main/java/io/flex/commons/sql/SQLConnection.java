package io.flex.commons.sql;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.fukkit.Fukkit;
import org.fukkit.config.Configuration;

import io.flex.FleX;
import io.flex.FleX.Task;
import io.flex.commons.Nullable;
import io.flex.commons.Severity;
import io.flex.commons.console.Console;
import io.flex.commons.utils.NumUtils;
import io.flex.commons.utils.StringUtils;

public class SQLConnection {
	
	/**
	 *  This is so that even if another SQLConnection
	 *  instance is made with the same variables/credentials
	 *  it will not be treated as the initial connection.
	 */
	private static Set<String> attempted = new HashSet<String>();
	
	private java.sql.Connection connection;
	
	private String
	
	ip = null,
	port = null,
	database = null,
	username = null,
	password = null,
	sqlite = FleX.EXE_PATH.replace(File.separator, "/") + "/flex/data/sqlite";

	private boolean alternateEncoding = false, available = true;
	
	private long
	
	creation,
	lifeSpan = 21600000/*6 hours*/; // Lower than typical database timeout (ms).
	
	private SQLDriverType driver;
	
	public SQLConnection(String ip, int port, String database, String username, String password, String sqlite) {
		this(ip, port, database, username, password, null, null, SQLDriverType.SQLITE, sqlite);
	}
	
	public SQLConnection(String ip, int port, String database, String username, String password, SQLDriverType driver) {
		this(ip, port, database, username, password, null, null, driver);
	}
	
	/**
	 * 
     * @deprecated Use the constructor that doesn't use the <code>admin</code>
     * parameters instead. The use of this constructor is discouraged as it is not
     * the most trust worthy.
     * 
     * </br>
     * </br>
     * 
     * Attempts to establish a connection to an sql database using the parameters provided.
     * 
     * <br>
     * <br>
     * 
     * <b>Note:</b> If you are still not able to connect
     * using the recommended constructor it could be that you need
     * to provide an admin and adminPassword as parameters.
     * 
     * @param host The host ip of the database.
     * @param database The database that the given values will attempt to connect to.
     * @param username {@link DriverManager#getConnection(String, String, String)} username parameter.
     * @param password {@link DriverManager#getConnection(String, String, String)} password parameter.
     * 
     * </br>
     * </br>
     * 
     * @see {@link DriverManager#getConnection(String url, String username, String password)}
	 * 
	 */
	@Deprecated
	public SQLConnection(String host, String database, String username, String password, String admin, String adminPassword, String sqlite) {
		this(host.split(":")[0], Integer.parseInt(host.split(":")[1]), database, username, password, admin, adminPassword, SQLDriverType.SQLITE, sqlite);
	}
	
	/**
	 * 
     * @deprecated Use the constructor that doesn't use the <code>admin</code>
     * parameters instead. The use of this constructor is discouraged as it is not
     * the most trust worthy.
     * 
     * </br>
     * </br>
     * 
     * Attempts to establish a connection to an sql database using the parameters provided.
     * 
     * <br>
     * <br>
     * 
     * <b>Note:</b> If you are still not able to connect
     * using the recommended constructor it could be that you need
     * to provide an admin and adminPassword as parameters.
     * 
     * @param host The host ip of the database.
     * @param database The database that the given values will attempt to connect to.
     * @param username {@link DriverManager#getConnection(String, String, String)} username parameter.
     * @param password {@link DriverManager#getConnection(String, String, String)} password parameter.
     * 
     * </br>
     * </br>
     * 
     * @see {@link DriverManager#getConnection(String url, String username, String password)}
	 * 
	 */
	@Deprecated
	public SQLConnection(String host, String database, String username, String password, String admin, String adminPassword, SQLDriverType driver) {
		this(host.split(":")[0], Integer.parseInt(host.split(":")[1]), database, username, password, admin, adminPassword, driver);
	}
	
	/**
	 * 
     * @deprecated Use the constructor that doesn't use the <code>admin</code>
     * parameters instead. The use of this constructor is discouraged as it is not
     * the most trust worthy.
     * 
     * </br>
     * </br>
     * 
     * Attempts to establish a connection to an sql database using the parameters provided.
     * 
     * <br>
     * <br>
     * 
     * <b>Note:</b> If you are still not able to connect
     * using the recommended constructor it could be that you need
     * to provide an admin and adminPassword as parameters.
     * 
     * @param host The host ip of the database.
     * @param database The database that the given values will attempt to connect to.
     * @param username {@link DriverManager#getConnection(String, String, String)} username parameter.
     * @param password {@link DriverManager#getConnection(String, String, String)} password parameter.
     * 
     * </br>
     * </br>
     * 
     * @see {@link DriverManager#getConnection(String url, String username, String password)}
	 * 
	 */
	@Deprecated
	public SQLConnection(String ip, int port, String database, String user, String password, String admin, String adminPassword, String sqlite) {
		this(ip, port, database, user, password, admin, adminPassword, SQLDriverType.SQLITE, sqlite);
	}
	
	/**
	 * 
     * @deprecated Use the constructor that doesn't use the <code>admin</code>
     * parameters instead. The use of this constructor is discouraged as it is not
     * the most trust worthy.
     * 
     * </br>
     * </br>
     * 
     * Attempts to establish a connection to an sql database using the parameters provided.
     * 
     * <br>
     * <br>
     * 
     * <b>Note:</b> If you are still not able to connect
     * using the recommended constructor it could be that you need
     * to provide an admin and adminPassword as parameters.
     * 
     * @param host The host ip of the database.
     * @param database The database that the given values will attempt to connect to.
     * @param username {@link DriverManager#getConnection(String, String, String)} username parameter.
     * @param password {@link DriverManager#getConnection(String, String, String)} password parameter.
     * 
     * </br>
     * </br>
     * 
     * @see {@link DriverManager#getConnection(String url, String username, String password)}
	 * 
	 */
	@Deprecated
	public SQLConnection(String ip, int port, String database, String user, String password, String admin, String adminPassword, SQLDriverType driver) {
		this(ip, port, database, user, password, admin, adminPassword, driver, null);
	}
	
	private SQLConnection(String ip, int port, String database, String user, String password, String admin, String adminPassword, SQLDriverType driver, @Nullable String sqlite) {
		
        this.ip = ip;
        this.port = String.valueOf(port);
        
        this.database = database;
        this.username = user;
        this.password = password;
        
        if (sqlite != null)
        	this.sqlite = sqlite.replace(File.separator, "/");
        
        this.driver = driver;
        
        if (attempted.contains(this.toString())) {
        	
        	this.testConnection(1);
        	
        	attempted.add(this.toString());
        	
        }
        
        try {
			this.connect(this.driver == SQLDriverType.SQLITE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void setAlternateEncoding(boolean alternateEncoding) {
		this.alternateEncoding = alternateEncoding;
	}
	
	private void connect(boolean lite) throws SQLException {
		this.creation = System.currentTimeMillis();
		this.connection = DriverManager.getConnection("jdbc:" + this.driver + ":" + (lite ? "/" : "//") + this.getHost() + "/" + this.database + (lite ? ".db" : "?allowMultiQueries=true&autoReconnect=true" + (this.alternateEncoding ? "&characterEncoding=latin1&useConfigs=maxPerformance" : "")), this.username, this.password);
	}

	public String getHost() {
		
		if (this.driver == SQLDriverType.SQLITE)
			return this.sqlite;
		
		return this.ip + ":" + this.port;
		
	}

	public String getIp() {
		return this.ip;
	}

	public int getPort() {
		return Integer.parseInt(this.port);
	}
	
	public String getDatabase() {
		return this.database;
	}
	
	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}
	
	public java.sql.Connection getDriverConnection() {
		return this.connection;
	}
	
	public boolean isStale() {
		
		try {
			
			if (this.connection == null)
				return true;
			
			// Expire after 6 hours (below MySQL's 8-hour timeout)
	        return (System.currentTimeMillis() - this.creation > this.lifeSpan) || !this.connection.isValid(2);
			
		} catch (SQLException e) {
			Task.error("SQL (" + Severity.ERROR.name() + ")", "Failed to check if connection is stale: " + e.getMessage());
		}
		return true;
    }
	
	public boolean isAvailable() {
		return this.available;
	}
	
	UUID uid = UUID.randomUUID();
	
	public void open() {
		
		this.available = false;
		
		try {
			
			boolean stale = this.isStale();
			
			if (this.connection != null) {
				
				if (!this.connection.isClosed() && !stale)
					return;
				
				if (stale)
					this.connection.close();
				
			}
			
			this.connect(this.driver == SQLDriverType.SQLITE);
			
		} catch (SQLException e) {
			Task.error("SQL (" + Severity.ERROR.name() + ")", "Failed to open connection: " + e.getMessage());
		}
		
	}
	
	public void release() {
		Task.debug("SQL", "Closing connection.");
		this.available = true;
	}

	public boolean isEstablished() {
		return this.connection != null;
	}
	
	public boolean isValid() {
		try {
			return this.connection != null && !this.connection.isClosed();
		} catch (SQLException e) {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return this.ip + ":" + this.port + "/" + this.database;
	}
	
	private void testConnection(int attempt) {
    	
    	int limit = 2;
		
		Task.print("SQL", "Attempting initial connection" + (attempt < 0 ? " (Local)" : (attempt > 1 ? " (" + attempt + "/" + limit + ")" + (this.alternateEncoding ? "" : " using alternate encoding") : "")) + "...",
				
				"[" + (this.driver == SQLDriverType.SQLITE ? "DRIVE:PATH" : "HOST:PORT") + "/DATABASE] " + this.getHost() + "/" + this.database,
				"[USER] " + this.username,
				"[PASSWORD] " + StringUtils.repeat("*", this.password.length()),
				"[DRIVER] " + this.driver.name());
		
		if (attempt > 1)
			this.alternateEncoding = true;
		
		if (this.driver == SQLDriverType.SQLITE)
			try {
				Class.forName("org.sqlite.JDBC");
				this.connect(true);
			} catch (SQLException | ClassNotFoundException e) {
				Task.error("SQL (" + Severity.EMERG + ")", "An error occurred whilst creating an SQLite database.");
		    	Console.log("SQL", Severity.EMERG, e);
			}
		
		else try {
			
			this.connect(false);
			
		} catch (SQLException e) {
			
			Task.error("SQL (" + Severity.NOTICE + ")", "There was a problem connecting to " + this.getHost() + "/" + this.database + ".");
			Task.error("SQL (" + Severity.NOTICE + ")", this.getHost() + "/" + this.database + ": " + e.getMessage());
			
	    	this.connection = null;
	    	
	    	if (attempt < limit) {
		        this.testConnection(attempt + 1);
	    		return;
	    	}
	    	
	    	this.ip = "localhost";
	    	this.port = String.valueOf(3306);
	    	
	        this.username = "root";
	        this.password = new String[]{ "foo", "bar", "aa", "foobar" }[NumUtils.getRng().getInt(0, 3)];
	        
			Task.error("SQL (" + Severity.NOTICE + ")", "Connection attempt limit met. (" + attempt + "/" + limit + ")");
	    	Task.error("SQL (" + Severity.NOTICE + ")", "Check your credentials and try again.");
	    	
			if (Fukkit.getResourceHandler().getYaml(Configuration.ENGINE).getBoolean("allow-startup-with-sqlite", false)) {
				
				Task.print("SQL (" + Severity.NOTICE + ")", "Setting connection driver to SQLITE...");
		        
		        this.driver = SQLDriverType.SQLITE;
		    	
		    	this.testConnection(-1);
				
			} else throw new UnsupportedOperationException("Could not connect to database and sqlite-on-connection-fail is false, please review credentials and try again. Alternatively set sqlite-on-connection-fail to true in the aFleX directory config.yml to allow for server startup with an sqlite local database.");
	    	
			return;
			
		}
		
		Task.print("SQL", "Connection settings successfully established.");
		
		try {
			this.connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
}
	