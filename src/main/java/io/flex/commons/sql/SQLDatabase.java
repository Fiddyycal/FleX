package io.flex.commons.sql;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.flex.FleX;
import io.flex.FleX.Task;
import io.flex.commons.Nullable;
import io.flex.commons.Severity;
import io.flex.commons.StopWatch;
import io.flex.commons.console.Console;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

import static io.flex.commons.utils.ClassUtils.is;
import static io.flex.commons.utils.ClassUtils.isString;

import static io.flex.commons.sql.SQLDataType.IDENTIFIER_QUOTE;

public class SQLDatabase implements Serializable {
	
	private static final long
	
	serialVersionUID = -736068188882394525L,
	max_connections = 5;
	
	int port;
	
	private String
	
	ip, database, username, password, sqlite = FleX.EXE_PATH.replace(File.separator, "/") + "/flex/data/sqlite";
	
	private Set<SQLConnectionListener> listeners = new HashSet<SQLConnectionListener>();

	private List<SQLConnection> pool = new ArrayList<SQLConnection>();
	
	private SQLDriverType driver;
	
	public SQLDatabase(String ip, int port, String database, String username, String password, SQLDriverType driver) {
		this(ip, port, database, username, password, driver, null);
	}
	
	public SQLDatabase(String ip, int port, String database, String username, String password, @Nullable String sqlite) {
		this(ip, port, database, username, password, SQLDriverType.SQLITE, sqlite);
	}
	
	private SQLDatabase(String ip, int port, String database, String username, String password, SQLDriverType driver, @Nullable String sqlite) {
		
		this.driver = driver;
		
		this.ip = ip;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
		this.sqlite = sqlite;
		
		for (int i = 0; i < max_connections; i++)
			this.pool.add(this.connect());
		
	}

	public String getHost() {
		
		if (this.driver == SQLDriverType.SQLITE)
			return this.sqlite;
		
		return this.ip + ":" + this.port;
		
	}
	
	public SQLDriverType getDriver() {
		return this.driver;
	}
	
	public SQLConnection open() throws SQLException {
		
		Task.debug("SQL", "Opening connection, connections in use: " + this.pool.size());
		
		for (SQLConnection connection : this.pool) {
			
			if (connection.isAvailable()) {
				connection.open();
				return connection;
			}
				
		}
		/*
		Task.print("SQL",
				
				"No available connections in connection pool, creating new connection...",
				"Consider making the max_connections variable higher.");
		*/
		SQLConnection connection = this.connect();
		
		connection.open();
		
		this.pool.add(connection);
		
		return connection;
		
	}
	
	private SQLConnection connect() {
		return this.driver == SQLDriverType.SQLITE ?
				
				new SQLConnection(this.ip, this.port, this.database, this.username, this.password, this.sqlite) :
				new SQLConnection(this.ip, this.port, this.database, this.username, this.password, this.driver);
	}
	
	/**
	 * Connection safe, but may still throw SQLException.
	 * Will attempt to open a connection, utilize it, then close the connection.
	 * @throws SQLException
	 */
	public SQLRowWrapper getRow(String table, @Nullable SQLCondition<?>... conditions) throws SQLException {
		return this.getRows(table, conditions).stream().findFirst().orElse(null);
	}
	
	/**
	 * Connection safe, but may still throw SQLException.
	 * Will attempt to open a connection, utilize it, then close the connection.
	 * @throws SQLException
	 */
	public Set<SQLRowWrapper> getRows(String table, @Nullable SQLCondition<?>... conditions) throws SQLException {

		StringBuilder query = new StringBuilder("SELECT * FROM " + table);
		
		Set<SQLRowWrapper> rows = new HashSet<SQLRowWrapper>();
		
		SQLConnection connection = this.open();
		
		PreparedStatement statement = null;
		
		try {
			
			List<Object> params = new ArrayList<>();
			
			if (conditions != null && conditions.length > 0) {
			    for (SQLCondition<?> condition : conditions) {
			    	
			        if (condition != null) {
			        	
			            query.append(query.toString().contains(" WHERE ") ? " AND " : " WHERE ");
			            query.append(IDENTIFIER_QUOTE).append(condition.key()).append(IDENTIFIER_QUOTE).append(" = ?");
			            
			            params.add(simplify(condition.value()));
			            
			        }
			    }
			}
			
			statement = connection.getDriverConnection().prepareStatement(query.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			for (int i = 0; i < params.size(); i++)
				bind(statement, i+1, params.get(i));
			
		    ResultSet result = statement.executeQuery();
			
			String primary = null;
			
	        try (ResultSet rs = connection.getDriverConnection().getMetaData().getPrimaryKeys(null, null, table)) {
	            if (rs.next())
	            	primary = rs.getString("COLUMN_NAME");
	        }
	        
			ResultSetMetaData meta = result.getMetaData();
	        
			while(result.next()) {
				
				Map<String, Object> entries = new HashMap<String, Object>();
				
				for (int i = 1; i <= meta.getColumnCount(); i++) {
					
					String column = meta.getColumnName(i);
			        Object value = result.getObject(i);
			        
			        entries.put(column, value);
			        
			    }
		        
				rows.add(new SQLRowWrapper(this, table, primary, entries));
				
			}
			
		} catch (SQLException e) {
			
			Task.error("SQL (" + Severity.ERROR.name() + ")", "Failed to execute row retrieval query: " + query.toString() + ": " + e.getMessage());
	    	Console.log("SQL", Severity.ERROR, e);
	        throw e;
	        
	    } finally {
	    	
	        if (statement != null) {
	            try {
	            	statement.close();
	            } catch (SQLException ignore) {}
	        }

    		connection.release();
            
	    }
		
		return rows;
	    
	}
	
	/**
	 * Connection safe, but may still throw SQLException.
	 * Will attempt to open a connection, utilize it, then close the connection.
	 * @throws SQLException
	 */
	public int getTableSize(String table) throws SQLException {
		
		SQLConnection connection = this.open();
		
		PreparedStatement statement = null;
		
		try {
			
			String query = "SELECT COUNT(*) FROM " + table;
			
		    statement = connection.getDriverConnection().prepareStatement(query);
		    	
		    ResultSet result = statement.executeQuery();
		    	
		    if (result.next())
		    	return result.getInt(1);
			
		} catch (SQLException e) {
    	
			Console.log("SQL", Severity.ERROR, e);
			throw e;
        
		} finally {
    	
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException ignore) {}
			}
			
			connection.release();
			
		}
		
	    return 0;
	    
	}
	
	/**
	 * Connection safe, but may still throw SQLException.
	 * Will attempt to open a connection, utilize it, then close the connection.
	 * @throws SQLException
	 */
	public List<String> getColumns(String table) throws SQLException {
		
		List<String> columns = new LinkedList<String>();
	    
	    SQLConnection connection = this.open();
		
		PreparedStatement statement = null;
		
		try {
			
			String query = "SELECT * FROM " + table + " LIMIT 1";
			
		    statement = connection.getDriverConnection().prepareStatement(query);

	        ResultSet result = statement.executeQuery();
	        
	        ResultSetMetaData meta = result.getMetaData();
	        
	        int count = meta.getColumnCount();
	        
	        for (int i = 1; i <= count; i++)
	        	columns.add(meta.getColumnName(i));
	        
		} catch (SQLException e) {
    	
			Console.log("SQL", Severity.ERROR, e);
			throw e;
        
		} finally {
    	
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException ignore) {}
			}
			
			connection.release();
			
		}
		
		return columns;
		
	}

	public void addListener(SQLConnectionListener listener) {
		this.listeners.add(listener);
	}
	
	/**
	 * Connection safe, but may still throw SQLException.
	 * Will attempt to open a connection, utilize it, then close the connection.
	 * @throws SQLException
	 */
	public SQLRowWrapper addRow(String table, LinkedHashMap<String, Object> entries) throws SQLException {
		return this.addRow(table, null, entries);
	}
	
	/**
	 * Connection safe, but may still throw SQLException.
	 * Will attempt to open a connection, utilize it, then close the connection.
	 * @throws SQLException
	 */
	public SQLRowWrapper addRow(String table, @Nullable String identifier, LinkedHashMap<String, Object> entries) throws SQLException {
		 
	    if (entries == null || entries.isEmpty())
	        throw new SQLException("Cannot insert an empty row into table " + table + ", please provide values.");
 
	    SQLConnection connection = this.open();
	    PreparedStatement statement = null;
 
	    try {
 
	        if (identifier == null) {
 
	            DatabaseMetaData databaseMeta = connection.getDriverConnection().getMetaData();
 
	            try (ResultSet rs = databaseMeta.getPrimaryKeys(null, null, table)) {
	                if (rs.next())
	                    identifier = rs.getString("COLUMN_NAME");
	            }
 
	        }
 
	        List<String> columns = this.getColumns(table);
 
	        Map<String, Object> filtered = new LinkedHashMap<>();
 
	        for (String col : columns) {
	            if (entries.containsKey(col)) {
	                Object val = entries.get(col);
	                if (val instanceof UUID) {
	                    filtered.put(col, val.toString());
	                } else {
	                    filtered.put(col, val);
	                }
	            }
	        }
 
	        if (filtered.isEmpty())
	            throw new SQLException("No valid columns provided for table " + table + ".");
 
	        StringBuilder query = new StringBuilder("INSERT INTO ").append(table).append(" (");
	        StringBuilder placeholders = new StringBuilder();
 
	        for (String col : filtered.keySet()) {
	            query.append(col).append(", ");
	            placeholders.append("?, ");
	        }
 
	        query.setLength(query.length() - 2);
	        placeholders.setLength(placeholders.length() - 2);
 
	        query.append(") VALUES (").append(placeholders).append(")");
 
	        statement = connection.getDriverConnection().prepareStatement(query.toString());
 
	        int index = 1;
 
	        for (Object value : filtered.values()) {
	            if (value instanceof File) {
 
	            	File file = (File) value;
 
	                try (FileInputStream fis = new FileInputStream((File) value)) {
	                    statement.setBinaryStream(index++, fis, (int) file.length());
	                } catch (IOException e) {
						throw new SQLException("Exception occured writing file to database: (" + e.getCause().getClass().getSimpleName() + ") " + e.getMessage());
					}
 
	            } else statement.setObject(index++, value);
	        }
 
	        statement.executeUpdate();
 
	        return new SQLRowWrapper(this, table, identifier, filtered);
 
	    } catch (SQLException e) {
	        Console.log("SQL", Severity.ERROR, e);
	        throw e;
	    } finally {
	        if (statement != null) {
	            try {
	                statement.close();
	            } catch (SQLException ignore) {}
	        }
	        connection.release();
	    }
	}
	
	/**
	 * Connection safe, but may still throw SQLException.
	 * Will attempt to open a connection, utilize it, then close the connection.
	 * @throws SQLException
	 */
	public void createTable(String table, LinkedHashMap<String, SQLDataType> columns) throws SQLException {
		this.createTable(table, null, columns);
	}
	
	/**
	 * Connection safe, but may still throw SQLException.
	 * Will attempt to open a connection, utilize it, then close the connection.
	 * @throws SQLException
	 */
	public void createTable(String table, @Nullable String primary, LinkedHashMap<String, SQLDataType> columns) throws SQLException {
		
	    SQLConnection connection = this.open();
		
		PreparedStatement statement = null;
		
	    try {
	    	
	    	if (columns == null || columns.isEmpty())
		        throw new SQLException("Column definitions must not be empty.");
		    
		    DatabaseMetaData meta = connection.getDriverConnection().getMetaData();
		    
	    	Task.debug("SQL", "Creating table " + table + "...");
	    	
			StopWatch timer = new StopWatch();
		    
			/**
			 * 
			 * } catch (SQLException e) {
			
					if (!e.getMessage().equals("Table '" + table + "' already exists"))
						throw e;
						
				}
			 * 
			 */
			
		    try (ResultSet tables = meta.getTables(null, null, table, new String[]{ "TABLE" })) {
		    	
		        if (tables.next()) {
		        	
			    	Task.debug("SQL", "Returning pre-existing " + table + " table.");
		            return;
		            
		        }
		        
		    }
		    
			Task.print("SQL", "Builing structured query language table " + table + "...");
		    
		    StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS `" + table + "` (");

		    for (Entry<String, SQLDataType> entry : columns.entrySet()) {
		    	
		        String column = entry.getKey();
		        
		        SQLDataType type = entry.getValue();

		        query.append(IDENTIFIER_QUOTE + column + IDENTIFIER_QUOTE + " " + type);
		        
		        if (type.getLength() > 0)
		        	query.append("(" + type.getLength() + ")");
		        
		        if (primary != null && column.equals(primary))
			        query.append(" PRIMARY KEY");
		        
		        query.append(", ");
		        
		    }
		    
		    int length = query.length();
		    
		    if (query.substring(length - 2).equals(", "))
		        query.setLength(length - 2);
		    
		    query.append(");");
		    
			Task.print("SQL", "Attempting CREATE -> base; " + query.toString());
			
		    statement = connection.getDriverConnection().prepareStatement(query.toString());
		    statement.execute();
			
			for (SQLConnectionListener listener : this.listeners)
				listener.onCreate(table);
		    
			Task.print("SQL", "Table created. (" + timer.getTime(TimeUnit.MILLISECONDS) + "ms)");

	    } catch (SQLException e) {
	    	
	    	Console.log("SQL", Severity.ERROR, e);
	        throw e;
	        
	    } finally {
	    	
	    	if (statement != null) {
				try {
					statement.close();
				} catch (SQLException ignore) {}
			}
	    	
	    	connection.release();
				
		}
		
	}
	
	/**
	 * Connection safe, but may still throw SQLException.
	 * Will attempt to open a connection, utilize it, then close the connection.
	 *  
	 * @param query Structured Query Language to parse to database.
	 * @return If query was successfully executed.
	 * @throws SQLException
	 */
	public boolean execute(String query) throws SQLException {
		
	    SQLConnection connection = this.open();
		
		PreparedStatement statement = null;
		
		try {
			
			if (this.driver == SQLDriverType.SQLITE)
				query = query.replace(IDENTIFIER_QUOTE, "");
			
			Task.debug("SQL", "Attempting EXECUTE -|- base; " + query);
			
			statement = connection.getDriverConnection().prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			boolean resultSet = statement.execute();
			
			if (resultSet)
				Task.debug("SQL", "Execute first(); returned java.sql.ResultSet.");
			
			for (SQLConnectionListener listener : listeners)
				listener.onExecute(query, resultSet);
			
			Task.debug("SQL", "EXECUTE successful.");
			return true;
			
		} catch (SQLException e) {
			
			Task.error("SQL (" + Severity.ERROR.name() + ")", "Failed to execute query: " + query + ": " + e.getMessage());
	    	Console.log("SQL", Severity.ERROR, e);
			return false;
			
		} finally {
	    	
	    	if (statement != null) {
				try {
					statement.close();
				} catch (SQLException ignore) {}
			}
	    	
	    	connection.release();
				
		}
	}

	/**
	 * Connection safe, but may still throw SQLException.
	 * Will attempt to open a connection, utilize it, then close the connection.
	 *  
	 * @param query Structured Query Language to parse to database.
	 * @param objects Objects to parse to the prepared statement.
	 * @return Number of affected rows.
	 * @throws SQLException
	 */
	public int update(String query, Object... objects) throws SQLException {
		
	    SQLConnection connection = this.open();
		
		PreparedStatement statement = null;
		
		try {
			
			if (this.driver == SQLDriverType.SQLITE)
				query = query.replace(IDENTIFIER_QUOTE, "");
			
			Task.debug("SQL", "Attempting UPDATE -> base; " + query);
			
			statement = connection.getDriverConnection().prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			if (objects != null && objects.length > 0) {
				for (int i = 0; i < objects.length; i++) {
					
					Object object = objects[i];
					int index = i + 1;
					
					bind(statement, index, object);
					
				}
			}
			
			int affected = statement.executeUpdate();
			
			for (SQLConnectionListener listener : listeners)
				listener.onUpdate(query, affected);
			
			Task.debug("SQL", "UPDATE successful.");
			
			return affected;
			
		} catch (SQLException e) {
			
			Task.error("SQL (" + Severity.ERROR.name() + ")", "Failed to execute update query: " + query + ": " + e.getMessage());
	    	Console.log("SQL", Severity.ERROR, e);
			return -1;
			
		} finally {
	    	
	    	if (statement != null) {
				try {
					statement.close();
				} catch (SQLException ignore) {}
			}
	    	
	    	connection.release();
				
		}
		
	}

	/**
	 * Connection safe, but may still throw SQLException.
	 * Will attempt to open a connection, utilize it, then close the connection.
	 *  
	 * @param query Structured Query Language to parse to database.
	 * @param objects Objects to parse to the prepared statement.
	 * @return Number of affected rows.
	 * @throws SQLException
	 */
	public Set<SQLRowWrapper> result(String query, Object... objects) throws SQLException {

		Set<SQLRowWrapper> rows = new HashSet<SQLRowWrapper>();
		
	    SQLConnection connection = this.open();
	    
		PreparedStatement statement = null;
		
		try {
			
			if (this.driver == SQLDriverType.SQLITE)
				query = query.replace(IDENTIFIER_QUOTE, "");
			
			Task.debug("SQL", "Attempting RESULT -> base; " + query);
			
			statement = connection.getDriverConnection().prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			if (objects != null && objects.length > 0) {
				for (int i = 0; i < objects.length; i++) {
					
					Object object = objects[i];
					int index = i + 1;
					
					bind(statement, index, object);
					
				}
			}
			
			String table = null;
			String primary = null;
			
        	try (ResultSet result = statement.executeQuery()) {
        		
        		for (SQLConnectionListener listener : listeners)
    				listener.onResult(query, result);
    			
    			Task.debug("SQL", "RESULT successful.");
    			
    			Pattern pattern = Pattern.compile("(?i)\\bFROM\\b\\s+([a-zA-Z0-9_]+)");
    	        Matcher matcher = pattern.matcher(query);
    	        
    	        if (matcher.find())
    	            table = matcher.group(1);
    			
    	        if (table != null) {
    	        	try (ResultSet rs = connection.getDriverConnection().getMetaData().getPrimaryKeys(null, null, table)) {
    		            if (rs.next())
    		            	primary = rs.getString("COLUMN_NAME");
    		        }
    	        }
    	        
    			ResultSetMetaData meta = result.getMetaData();
    	        
    			while(result.next()) {
    				
    				Map<String, Object> entries = new HashMap<String, Object>();
    				
    				for (int i = 1; i <= meta.getColumnCount(); i++) {
    					
    					String column = meta.getColumnName(i);
    			        Object value = result.getObject(i);
    			        
    			        entries.put(column, value);
    			        
    			    }
    		        
    				rows.add(new SQLRowWrapper(this, table, primary, entries));
    				
    			}
        		
        	}
        	
			return rows;
			
		} catch (SQLException e) {
			
			Task.error("SQL (" + Severity.ERROR.name() + ")", "Failed to execute result query: " + query + ": " + e.getMessage());
	    	Console.log("SQL", Severity.ERROR, e);
			return Collections.emptySet();
			
		} finally {
	    	
	    	if (statement != null) {
				try {
					statement.close();
				} catch (SQLException ignore) {}
			}
	    	
	    	connection.release();
				
		}
		
	}
	
	public boolean hasConnection() {
		return !this.pool.isEmpty();
	}
	
	/*
	private int attempt = 0;
	
	/**
	 * Connection safe, but may still throw SQLException.
	 * Will attempt to open a connection, utilize it, then close the connection.
	 *  
	 * @param query Structured Query Language to parse to database.
	 * @return SQLResultWrapper with pointer at first row.
	 * @throws SQLException
	 *
	public SQLResultWrapper result(String query) throws SQLException {
		
	    SQLConnection connection = this.open();
		
		PreparedStatement statement = null;
		
		try {
			
			this.attempt++;
			
			if (this.driver == SQLDriverType.SQLITE)
				query = query.replace(SQLDataType.IDENTIFIER_QUOTE, "");
			
			Task.debug("SQL", "Attempting RESULT <- base; " + query);
			
			PreparedStatement statement = this.connection.prepareStatement(query, this.driver == SQLDriverType.SQLITE ? ResultSet.TYPE_FORWARD_ONLY : ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet result = statement.executeQuery();
			
			for (SQLConnectionListener listener : listeners)
				listener.onResult(query, result);
			
			Task.debug("SQL", "RESULT successful.");
			
			SQLResultWrapper wrapper = new SQLResultWrapper() {
				
				@Override
				public Statement getStatement() {
					return statement;
				}
				
				@Override
				public ResultSet asSet() {
					return result;
				}
				
			};
			
			wrapper.asSetAsFirst();
			
			return wrapper;
			
		} catch (SQLSyntaxErrorException e) {
			
			Task.error("SQL (" + Severity.NOTICE.name() + ")", "Failed to fetch result: " + e.getMessage());
	    	Console.log("SQL", Severity.NOTICE, e);
			return null;
			
		} catch (SQLException e) {

			Task.error("SQL (" + Severity.ERROR.name() + ")", "Failed to fetch result: " + query);
			
			if (this.attempt == 1 && e.getMessage().contains("You should consider either expiring and/or testing connection validity before use in your application, increasing the server configured values for client timeouts, or using the Connector/J connection property 'autoReconnect=true' to avoid this problem.")) {
				
				Task.debug("SQL", "Attempting RESULT <-x (re-connect) base; " + query);
				
				// Reconnect
				this.close();
				this.open();
				
				return this.result(query);
				
			}
			
	    	Console.log("SQL", Severity.ERROR, e);
			return null;
			
		} finally {
			
			if (this.attempt > 1)
				this.attempt = 0;
			
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException ignore) {}
			}
	    	
			if (connection.isOpen())
				connection.close();
				
		}
		
	}*/
	
	public void print(String table, String... columns) throws SQLException {
		
	    if (columns == null || columns.length == 0)
	        throw new IllegalArgumentException("No columns specified to print.");

	    SQLConnection connection = this.open();
	    String cols = String.join(", ", columns);
	    String query = "SELECT " + cols + " FROM " + table;
	    
	    try (PreparedStatement stmt = connection.getDriverConnection().prepareStatement(query);
	         ResultSet rs = stmt.executeQuery()) {
	    	
	        ResultSetMetaData meta = rs.getMetaData();
	        int colCount = meta.getColumnCount();
	        
	        // Determine max width per column for formatting
	        int[] maxWidths = new int[colCount];
	        
	        for (int i = 0; i < colCount; i++)
	            maxWidths[i] = meta.getColumnLabel(i + 1).length();
	        
	        // Buffer rows for formatting
	        List<String[]> rows = new ArrayList<String[]>();
	        
	        while (rs.next()) {
	            String[] row = new String[colCount];
	            for (int i = 0; i < colCount; i++) {
	                String val = rs.getString(i + 1);
	                if (val == null) val = "NULL";
	                row[i] = val;
	                maxWidths[i] = Math.max(maxWidths[i], val.length());
	            }
	            rows.add(row);
	        }
	        
	        // Print header
	        for (int i = 0; i < colCount; i++)
	            System.out.print(padRight(meta.getColumnLabel(i + 1), maxWidths[i]) + " | ");
	        
	        System.out.println();
	        
	        // Print separator line
	        for (int i = 0; i < colCount; i++)
	            System.out.print(repeat("-", maxWidths[i]) + "-+-");
	        
	        System.out.println();
	        
	        // Print rows
	        for (String[] row : rows) {
	        	
	            for (int i = 0; i < colCount; i++)
	                System.out.print(padRight(row[i], maxWidths[i]) + " | ");
	            
	            System.out.println();
	            
	        }
	        
	    } finally {
	    	
	    	connection.release();
	        
	    }
	}

	private String padRight(String s, int n) {
	    return String.format("%-" + n + "s", s);
	}

	private String repeat(String s, int n) {
	    return new String(new char[n]).replace("\0", s);
	}
	
	@Override
	public String toString() {
		return this.getHost() + "/" + this.database;
	}
	
	private static void bind(PreparedStatement statement, int index, Object object) throws SQLException {
		
		if (object == null)
		    statement.setNull(index, Types.VARCHAR);
		
		else if (object instanceof String)
		    statement.setString(index, (String)object);
		
		else if (object instanceof Integer)
		    statement.setInt(index, (Integer)object);
		
		else if (object instanceof Long)
		    statement.setLong(index, (Long)object);
		
		else if (object instanceof Boolean)
		    statement.setBoolean(index, (Boolean)object);
		
		else if (object instanceof Double)
		    statement.setDouble(index, (Double)object);
		
		else if (object instanceof UUID)
		    statement.setString(index, object.toString());
		
		else throw new UnsupportedOperationException("The type " + object.getClass() + " is not supported.");
		
	}
	
	private static Object simplify(Object value) {
		
		if (value != null && String.valueOf(value).equalsIgnoreCase("null"))
			value = "NULL";
		
		return isString(value) || is(UUID.class, value) ? value.toString() : (value != null ? value : "NULL");
		
	}
	
}
