package io.flex.commons.sql;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
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
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
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

import static io.flex.commons.utils.ClassUtils.is;
import static io.flex.commons.utils.ClassUtils.isString;

import static io.flex.commons.sql.SQLDataType.IDENTIFIER_QUOTE;

public class SQLDatabase {
	
	private static final int max_connections = 5;
	
	int port;
	
	private String
	
	ip, database, username, password, sqlite = FleX.EXE_PATH.replace(File.separator, "/") + "/flex/data/sqlite";
	
	private Set<SQLConnectionListener> listeners = new HashSet<SQLConnectionListener>();

	private final List<SQLConnection> pool = new ArrayList<SQLConnection>();
	
	private SQLDriverType driver;
	
	private final ExecutorService executor;

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
		
		this.executor = Executors.newFixedThreadPool(max_connections, r -> {
		    Thread t = new Thread(r, "SQL-Worker");
		    t.setDaemon(true);
		    return t;
		});
		
	}
	
	public <T> void queueAsync(Callable<T> task, Consumer<T> callback) {
		queueAsync(task, callback, null);
    }
	
	public <T> void queueAsync(Callable<T> task, Consumer<T> callback, @Nullable Consumer<SQLException> exception) {
		
		Objects.requireNonNull(callback, "callback cannot be null");

	    this.executor.submit(() -> {
	        try {
	        	
	            T result = task.call();
	            
	            callback.accept(result);

	        } catch (Exception e) {
	        	
	            SQLException s = new SQLException(e);
	            
	            if (exception != null) {
	            	exception.accept(s);
	            
	            } else e.printStackTrace();
	            
	        }
	    });
	    
	}
	
	private <T> T queue(Callable<T> task) throws SQLException {
	    try {
	        return this.executor.submit(task).get();
	    } catch (InterruptedException | ExecutionException e) {
	        throw new SQLException(e);
	    }
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
		
	    synchronized(this.pool) {
			
            Task.debug("SQL", "Current connection pool: " + this.activeConnections() + "/" + this.pool.size());
	    	
			while (true) {
				
	            for (SQLConnection connection : this.pool) {
	                if (connection.isAvailable()) {
	                	
	                    Task.debug("SQL", "Reusing existing connection: " + connection);
	                    
	                    connection.open();
	                    
	                    return connection;
	                    
	                }
	            }
	            
	            if (this.pool.size() < max_connections) {
	            	
	                Task.debug("SQL", "Pool max_connections allows for more connections to open.");
	                Task.debug("SQL", "Creating new connection...");
	                
	                SQLConnection connection = this.connect();
	                
	                this.pool.add(connection);
                	
                	connection.open();
                    
                    return connection;
                    
	            }
	            
	            // No free connections and pool is maxed out, wait until someone releases.
	            Task.debug("SQL", "No available connections, waiting...");
	            Task.debug("SQL", "If you're seeting this alot, consider increasing the max_connections field.");
	            Task.debug("SQL", "IMPORTANT: Make sure the field max_connections is lower or equal to the value in the database.");
	            
	            try {
					this.pool.wait();
				} catch (InterruptedException e) {
					throw new SQLException(e);
				}
	            
	        }
	    	
	    }
	    
	}
	
	public void release(SQLConnection connection) {
	    synchronized(this.pool) {
	    	
	        connection.release();
	        
	        this.pool.notifyAll();
	        
	    }
	}
	
	private int activeConnections() {
		
	    int count = 0;
	    
	    for (SQLConnection conn : this.pool)
	        if (!conn.isAvailable())
	        	count++;
	        
	    return count;
	    
	}
	
	private SQLConnection connect() {Task.debug("SQL", "CONNECT CALLED -> creating new connection. Current pool size: " + pool.size());

		return this.driver == SQLDriverType.SQLITE ?
				
				new SQLConnection(this.ip, this.port, this.database, this.username, this.password, this.sqlite) :
				new SQLConnection(this.ip, this.port, this.database, this.username, this.password, this.driver);
	}
	
	/**
	 * Will attempt to open a connection, utilize it, then close the connection.
	 * Async safe.
	 */
	public void getRowAsync(String table, Consumer<SQLRowWrapper> callback) throws SQLException {
		this.queueAsync(() -> this.row(table, null, null), callback);
	}
	
	/**
	 * Will attempt to open a connection, utilize it, then close the connection.
	 * Async safe.
	 */
	public void getRowsAsync(String table, Consumer<Set<SQLRowWrapper>> callback) throws SQLException {
		this.queueAsync(() -> this.rows(table, -1, null, null), callback);
	}
	
	/**
	 * Will attempt to open a connection, utilize it, then close the connection.
	 * Async safe.
	 */
	public void getRowAsync(String table, @Nullable SQLCondition<?> condition, Consumer<SQLRowWrapper> callback) throws SQLException {
		this.queueAsync(() -> this.row(table, null, condition != null ? Arrays.asList(condition) : null), callback);
	}
	
	/**
	 * Will attempt to open a connection, utilize it, then close the connection.
	 * Async safe.
	 */
	public void getRowsAsync(String table, @Nullable SQLCondition<?> condition, Consumer<Set<SQLRowWrapper>> callback) throws SQLException {
		this.queueAsync(() -> this.rows(table, -1, null, condition != null ? Arrays.asList(condition) : null), callback);
	}
	
	/**
	 * Connection safe, but may still throw SQLException.
	 * Will attempt to open a connection, utilize it, then close the connection.
	 * @throws SQLException
	 */
	public SQLRowWrapper getRow(String table, @Nullable String... columns) throws SQLException {
		return this.queue(() -> this.row(table, columns != null ? Arrays.asList(columns) : null, null));
	}
	
	/**
	 * Connection safe, but may still throw SQLException.
	 * Will attempt to open a connection, utilize it, then close the connection.
	 * @throws SQLException
	 */
	public SQLRowWrapper getRow(String table, @Nullable SQLCondition<?>... conditions) throws SQLException {
		return this.queue(() -> this.row(table, null, conditions != null ? Arrays.asList(conditions) : null));
	}
	
	/**
	 * Connection safe, but may still throw SQLException.
	 * Will attempt to open a connection, utilize it, then close the connection.
	 * @throws SQLException
	 */
	public SQLRowWrapper getRow(String table, @Nullable List<String> columns, @Nullable List<SQLCondition<?>> conditions) throws SQLException {
		return this.queue(() -> this.row(table, columns, conditions));
	}
	
	/**
	 * Connection safe, but may still throw SQLException.
	 * Will attempt to open a connection, utilize it, then close the connection.
	 * @throws SQLException
	 */
	public SQLRowWrapper getRow(String table) throws SQLException {
		return this.queue(() -> this.row(table, null, null));
	}
	
	/**
	 * Connection safe, but may still throw SQLException.
	 * Will attempt to open a connection, utilize it, then close the connection.
	 * @throws SQLException
	 */
	public Set<SQLRowWrapper> getRows(String table) throws SQLException {
		return this.queue(() -> this.rows(table, -1, null, null));
	}
	
	/**
	 * Connection safe, but may still throw SQLException.
	 * Will attempt to open a connection, utilize it, then close the connection.
	 * @throws SQLException
	 */
	public Set<SQLRowWrapper> getRows(String table, @Nullable String... columns) throws SQLException {
		return this.queue(() -> this.rows(table, -1, columns != null ? Arrays.asList(columns) : null, null));
	}
	
	/**
	 * Connection safe, but may still throw SQLException.
	 * Will attempt to open a connection, utilize it, then close the connection.
	 * @throws SQLException
	 */
	public Set<SQLRowWrapper> getRows(String table, @Nullable SQLCondition<?>... conditions) throws SQLException {
		return this.queue(() -> this.rows(table, -1, null, conditions != null ? Arrays.asList(conditions) : null));
	}
	
	/**
	 * Connection safe, but may still throw SQLException.
	 * Will attempt to open a connection, utilize it, then close the connection.
	 * @throws SQLException
	 */
	public Set<SQLRowWrapper> getRows(String table, @Nullable List<String> columns, @Nullable List<SQLCondition<?>> conditions) throws SQLException {
		return this.queue(() -> this.rows(table, -1, columns, conditions));
	}

	/**
	 * Connection safe, but may still throw SQLException.
	 * Will attempt to open a connection, utilize it, then close the connection.
	 * @throws SQLException
	 */
	public Set<SQLRowWrapper> getRows(String table, int limit, @Nullable SQLCondition<?>... conditions) throws SQLException {
		return this.queue(() -> this.rows(table, limit, null, conditions != null ? Arrays.asList(conditions) : null));
	}
	
	/**
	 * Connection safe, but may still throw SQLException.
	 * Will attempt to open a connection, utilize it, then close the connection.
	 * @throws SQLException
	 */
	public Set<SQLRowWrapper> getRows(String table, int limit, @Nullable List<String> columns, @Nullable List<SQLCondition<?>> conditions) throws SQLException {
		return this.queue(() -> this.rows(table, limit, columns, conditions));
	}
	
	/**
	 * Connection safe, but may still throw SQLException.
	 * Will attempt to open a connection, utilize it, then close the connection.
	 * @throws SQLException
	 */
	public int getTableSize(String table) throws SQLException {
		
	    return this.queue(() -> {
	    	
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
		        
				this.release(connection);
				
			}
			
		    return 0;
	    	
	    });
	    
	}
	
	/**
	 * Connection safe, but may still throw SQLException.
	 * Will attempt to open a connection, utilize it, then close the connection.
	 * @throws SQLException
	 */
	public List<String> getColumns(String table) throws SQLException {
		return this.queue(() -> this.columns(table));
	}

	public void addListener(SQLConnectionListener listener) {
		this.listeners.add(listener);
	}
	
	/**
	 * Connection safe, but may still throw SQLException.
	 * Will attempt to open a connection, utilize it, then close the connection.
	 * @throws SQLException
	 */
	public void createTable(String table, LinkedHashMap<String, SQLDataType> columns) throws SQLException {
		
		this.queue(() -> {
	        this.create(table, null, columns);
	        return null;
	    });
	    
	}
	
	/**
	 * Connection safe, but may still throw SQLException.
	 * Will attempt to open a connection, utilize it, then close the connection.
	 * @throws SQLException
	 */
	public void createTable(String table, @Nullable String primary, LinkedHashMap<String, SQLDataType> columns) throws SQLException {
		
		this.queue(() -> {
	        this.create(table, primary, columns);
	        return null;
	    });
	    
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
		return this.queue(() -> this.add(table, identifier, entries));
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
		
	    return this.queue(() -> {
	    	
	    	String q = query;
	    	
	    	SQLConnection connection = this.open();
			
			PreparedStatement statement = null;
			
			try {
				
				if (this.driver == SQLDriverType.SQLITE)
					q = q.replace(IDENTIFIER_QUOTE, "");
				
				Task.debug("SQL", "Attempting EXECUTE -|- base; " + q);
				
				statement = connection.getDriverConnection().prepareStatement(q, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				
				boolean resultSet = statement.execute();
				
				if (resultSet)
					Task.debug("SQL", "Execute first(); returned java.sql.ResultSet.");
				
				for (SQLConnectionListener listener : listeners)
					listener.onExecute(q, resultSet);
				
				Task.debug("SQL", "EXECUTE successful.");
				return true;
				
			} catch (SQLException e) {
				
				Task.error("SQL (" + Severity.ERROR.name() + ")", "Failed to execute query: " + q + ": " + e.getMessage());
		    	Console.log("SQL", Severity.ERROR, e);
				return false;
				
			} finally {
		    	
		    	if (statement != null) {
					try {
						statement.close();
					} catch (SQLException ignore) {}
				}
		        
				this.release(connection);
					
			}
	    	
	    });
	    
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
		
	    return this.queue(() -> {

	    	String q = query;
	    	
	    	SQLConnection connection = this.open();
			
			PreparedStatement statement = null;
			
			try {
				
				if (this.driver == SQLDriverType.SQLITE)
					q = q.replace(IDENTIFIER_QUOTE, "");
				
				Task.debug("SQL", "Attempting UPDATE -> base; " + q);
				
				statement = connection.getDriverConnection().prepareStatement(q, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				
				if (objects != null && objects.length > 0) {
					for (int i = 0; i < objects.length; i++) {
						
						Object object = objects[i];
						int index = i + 1;
						
						bind(statement, index, object);
						
					}
				}
				
				int affected = statement.executeUpdate();
				
				for (SQLConnectionListener listener : listeners)
					listener.onUpdate(q, affected);
				
				Task.debug("SQL", "UPDATE successful.");
				
				return affected;
				
			} catch (SQLException e) {
				
				Task.error("SQL (" + Severity.ERROR.name() + ")", "Failed to execute update query: " + q + ": " + e.getMessage());
		    	Console.log("SQL", Severity.ERROR, e);
				return -1;
				
			} finally {
		    	
		    	if (statement != null) {
					try {
						statement.close();
					} catch (SQLException ignore) {}
				}
		        
				this.release(connection);
					
			}
	    	
	    });
	    
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
		
		return this.queue(() -> {

	    	String q = query;
	    	
			Set<SQLRowWrapper> rows = new HashSet<SQLRowWrapper>();
			
		    SQLConnection connection = this.open();
		    
			PreparedStatement statement = null;
			
			try {
				
				if (this.driver == SQLDriverType.SQLITE)
					q = q.replace(IDENTIFIER_QUOTE, "");
				
				Task.debug("SQL", "Attempting RESULT -> base; " + q);
				
				statement = connection.getDriverConnection().prepareStatement(q, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				
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
	    				listener.onResult(q, result);
	    			
	    			Task.debug("SQL", "RESULT successful.");
	    			
	    			Pattern pattern = Pattern.compile("(?i)\\bFROM\\b\\s+([a-zA-Z0-9_]+)");
	    	        Matcher matcher = pattern.matcher(q);
	    	        
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
				
				Task.error("SQL (" + Severity.ERROR.name() + ")", "Failed to execute result query: " + q + ": " + e.getMessage());
		    	Console.log("SQL", Severity.ERROR, e);
				return Collections.emptySet();
				
			} finally {
		    	
		    	if (statement != null) {
					try {
						statement.close();
					} catch (SQLException ignore) {}
				}
		        
				this.release(connection);
					
			}
			
		});
		
	}
	
	public boolean hasConnection() {
		return !this.pool.isEmpty();
	}
	
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
	        
			this.release(connection);
	        
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
	
	private void create(String table, @Nullable String primary, LinkedHashMap<String, SQLDataType> columns) throws SQLException {

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
	        
			this.release(connection);
				
		}
		
	}
	
	private SQLRowWrapper row(String table, @Nullable List<String> columns, @Nullable List<SQLCondition<?>> conditions) throws SQLException {
		return this.rows(table, 1, columns, conditions).stream().findFirst().orElse(null);
	}
	
	private Set<SQLRowWrapper> rows(String table, int limit, @Nullable List<String> columns, @Nullable List<SQLCondition<?>> conditions) throws SQLException {
		
		StringBuilder select = new StringBuilder();
		
		if (columns != null)
			for (String column : columns)
				select.append((select.length() > 0 ? "," : "") + IDENTIFIER_QUOTE + column + IDENTIFIER_QUOTE);
		
		StringBuilder query = new StringBuilder("SELECT " + (select.length() > 0 ? select.toString() : "*") + " FROM " + IDENTIFIER_QUOTE + table + IDENTIFIER_QUOTE);
		
		Set<SQLRowWrapper> rows = new HashSet<SQLRowWrapper>();
		
		SQLConnection connection = this.open();
		
		PreparedStatement statement = null;
		
		try {
			
			List<Object> params = new ArrayList<>();
			
			if (conditions != null)
			    for (SQLCondition<?> condition : conditions) {
			    	
			        if (condition != null) {
			        	
			            query.append(query.toString().contains(" WHERE ") ? " AND " : " WHERE ");
			            query.append(IDENTIFIER_QUOTE + condition.key() + IDENTIFIER_QUOTE).append(" = ?");
			            
			            params.add(simplify(condition.value()));
			            
			        }
			    }
			
			if (limit > 0)
				query.append(" LIMIT " + limit);
			
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
				
				if (primary == null && conditions != null)
					rows.add(new SQLRowWrapper(this, table, entries, conditions.toArray(new SQLCondition<?>[conditions.size()])));
				
				else rows.add(new SQLRowWrapper(this, table, primary, entries));
				
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
	        
			this.release(connection);
            
	    }
		
		return rows;
	    
	}
	
	private List<String> columns(String table) throws SQLException {
		
		List<String> columns = new LinkedList<String>();
	    
	    SQLConnection connection = this.open();
		
		PreparedStatement statement = null;
		
		try {
			
			String query = "SELECT * FROM " + IDENTIFIER_QUOTE + table + IDENTIFIER_QUOTE + " LIMIT 1";
			
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
			
			this.release(connection);
			
		}
		
		return columns;
		
	}
	
	private SQLRowWrapper add(String table, @Nullable String identifier, LinkedHashMap<String, Object> entries) throws SQLException {
		 
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
 
	        List<String> columns = this.columns(table);
 
	        Map<String, Object> filtered = new LinkedHashMap<>();
 
	        for (String col : columns) {
	        	
	            if (entries.containsKey(col)) {
	            	
	                Object val = entries.get(col);
	                
	                if (val instanceof UUID)
	                    filtered.put(col, val.toString());
	                
	                else filtered.put(col, val);
	                
	            }
	        }
 
	        if (filtered.isEmpty())
	            throw new SQLException("No valid columns provided for table " + table + ".");
 
	        StringBuilder query = new StringBuilder("INSERT INTO ").append(IDENTIFIER_QUOTE + table + IDENTIFIER_QUOTE).append(" (");
	        StringBuilder placeholders = new StringBuilder();
	        
	        for (String column : filtered.keySet()) {
	            query.append(IDENTIFIER_QUOTE + column + IDENTIFIER_QUOTE).append(", ");
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

			this.release(connection);
	        
	    }
	    
	}
	
}
