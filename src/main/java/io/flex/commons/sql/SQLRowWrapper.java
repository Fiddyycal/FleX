package io.flex.commons.sql;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.flex.commons.Nullable;
import io.flex.commons.cache.Cacheable;

@SuppressWarnings("unchecked")
public class SQLRowWrapper implements Cacheable {
	
	private String table, identifier;
	
	private SQLDatabase database;
	
    private Map<String, Object> entries;
	
    public Map<String, Object> getEntries() {
		return entries;
	}
    
	private SQLCondition<?>[] conditions;
    
    private boolean updated = true;
	
	public SQLRowWrapper(@Nullable SQLDatabase database, String table, Map<String, Object> entries, SQLCondition<?>... conditions) throws SQLException {
		
		this(database, table, null, entries);
		
		if (conditions != null && conditions.length > 0)
			this.conditions = conditions;
		
	}
	
	public SQLRowWrapper(@Nullable SQLDatabase database, String table, Map<String, Object> entries) throws SQLException {
		this(database, table, null, entries);
	}
	
	public SQLRowWrapper(@Nullable SQLDatabase database, String table, @Nullable String identifier, Map<String, Object> entries) throws SQLException {

		this.entries = entries;
		
		if (identifier != null)
			this.setIdentifier(identifier);
		
		this.table = table;
		this.database = database; // If null this entry will remain local only.
		
	}
	
	public String getTable() {
		return this.table;
	}
	
	public String getIdentifier() {
		return this.identifier;
	}
	
	public void setIdentifier(String identifier) throws SQLException {
		
	    if (!this.entries.containsKey(identifier))
	        throw new SQLException("No such column as " + this.identifier + ".");
	    
		this.identifier = identifier;
		
	}
	
	public <V> V get(String key) throws SQLException {
		
		try {
			return (V) this.entries.getOrDefault(key, null);
		} catch (ClassCastException e) {
			try {
				return (V) this.entries.getOrDefault(key, null).toString();
			} catch (NullPointerException fail) {
				return null;
			}
		}
		
	}
	
	public <V> V get(String key, @Nullable V def) throws SQLException {
		
		try {
			return (V) this.entries.getOrDefault(key, def);
		} catch (ClassCastException e) {
			return null;
		}
		
	}
	
	// Primitives
	public int getInt(String key) {
		return this.getInt(key, -1);
	}
	
	public int getInt(String key, int def) {
		
	    Object obj = this.entries.getOrDefault(key, def);
	    
	    if (obj instanceof Number)
	    	return ((Number)obj).intValue();
	    
	    try {
	        return Integer.parseInt(obj.toString());
	    } catch (NumberFormatException e) {
	        return def;
	    }
	    
	}
	
	public boolean getBoolean(String key) {
		return this.getBoolean(key, false);
	}
	
	public boolean getBoolean(String key, boolean def) {
		try {
			return (boolean) this.entries.getOrDefault(key, def);
		} catch (ClassCastException e) {
			try {
				return this.getInt(key, def ? 1 : 0) == 1;
			} catch (Exception fail) {
				return def;
			}
		}
	}
	
	public byte getByte(String key) {
		return this.getByte(key, (byte)-1);
	}
	
	public byte getByte(String key, @Nullable byte def) {
		try {
			return (byte) this.entries.getOrDefault(key, def);
		} catch (ClassCastException e) {
			try {
				Object obj = this.entries.getOrDefault(key, def);
				return (byte) (obj != null ? Byte.parseByte(obj.toString()) : -1);
			} catch (NumberFormatException | NullPointerException fail) {
				return def;
			}
		}
	}
	
	public short getShort(String key) {
		return this.getShort(key, (short)-1);
	}
	
	public short getShort(String key, @Nullable short def) {
		try {
			return (short) this.entries.getOrDefault(key, def);
		} catch (ClassCastException e) {
			try {
				Object obj = this.entries.getOrDefault(key, def);
				return (short) (obj != null ? Short.parseShort(obj.toString()) : def);
			} catch (NumberFormatException | NullPointerException fail) {
				return def;
			}
		}
	}
	
	public double getDouble(String key) {
		return this.getDouble(key, -1D);
	}
	
	public double getDouble(String key, double def) {
		
	    Object obj = this.entries.getOrDefault(key, def);
	    
	    if (obj instanceof Number)
	    	return ((Number)obj).doubleValue();
	    
	    try {
	        return Double.parseDouble(obj.toString());
	    } catch (NumberFormatException e) {
	        return def;
	    }
	    
	}
	
	public long getLong(String key) {
		return this.getLong(key, -1L);
	}
	
	public long getLong(String key, long def) {
		
	    Object obj = this.entries.getOrDefault(key, def);
	    
	    if (obj instanceof Number)
	    	return ((Number)obj).longValue();
	    
	    try {
	        return Long.parseLong(obj.toString());
	    } catch (NumberFormatException e) {
	        return def;
	    }
	    
	}
	
	public float getFloat(String key) {
		return this.getFloat(key, -1f);
	}
	
	public float getFloat(String key, float def) {
		
	    Object obj = this.entries.getOrDefault(key, def);
	    
	    if (obj instanceof Number)
	    	return ((Number)obj).floatValue();
	    
	    try {
	        return Float.parseFloat(obj.toString());
	    } catch (NumberFormatException e) {
	        return def;
	    }
	    
	}

	// Java
	public String getString(String key) {
		return this.getString(key, null);
	}

	public String getString(String key, @Nullable String def) {
		try {
			return (String) this.entries.getOrDefault(key, def);
		} catch (ClassCastException e) {
			Object obj = this.entries.getOrDefault(key, def);
			return obj != null ? obj.toString() : def;
		}
	}

	// File
	public Blob getBlob(String key) {
		return this.getBlob(key, null);
	}
	
	public Blob getBlob(String key, @Nullable Blob def) {
		try {
			return (Blob) this.entries.getOrDefault(key, def);
		} catch (ClassCastException e) {
			return def;
		}
	}

	// File
	public byte[] getByteArray(String key) {
		return this.getByteArray(key, null);
	}
	
	public byte[] getByteArray(String key, @Nullable byte[] def) {
		try {
			return (byte[]) this.entries.getOrDefault(key, def);
		} catch (ClassCastException e) {
			return def;
		}
	}
	
	public <V> void set(String column, @Nullable V value) {
		this.entries.put(column, value); // This even adds as null if value is null, then is removed from entries if update is called.
		this.updated = false;
	}
	
	public int update() throws SQLException {
		return this.update(false);
	}
	
	public int update(boolean force) throws SQLException {

	    // Local entry.
	    if (this.database == null)
	        return 0;

	    int affected = 0;

	    if (!force && this.updated)
	        return affected;

	    if ((this.identifier == null || this.entries.get(this.identifier) == null) && (this.conditions == null || this.conditions.length == 0))
	        throw new SQLException("Cannot update row without a unique identifier or condition(s). Use SQLRowWrapper#setIdentifier(String column) or specify condition(s) in the constructor.");
	    
	    StringBuilder builder = new StringBuilder("UPDATE " + this.table + " SET ");

	    int count = 0;

	    for (String key : this.entries.keySet()) {

	        if (key.equals(this.identifier))
	            continue;

	        if (count++ > 0)
	            builder.append(", ");

	        builder.append(SQLDataType.IDENTIFIER_QUOTE + key + SQLDataType.IDENTIFIER_QUOTE).append("=?");

	    }
	    
	    List<SQLCondition<?>> conditions = new ArrayList<SQLCondition<?>>();
	    
	    if (this.conditions != null && this.conditions.length > 0) {
	    	
	    	for (SQLCondition<?> cond : this.conditions)
		    	conditions.add(cond);
	    	
	    } else conditions.add(SQLCondition.where(this.identifier).is(this.entries.get(this.identifier)));

	    builder.append(" WHERE ");
	    
	    for (int i = 0; i < conditions.size(); i++) {
	    	
	        SQLCondition<?> cond = conditions.get(i);
	        
	        if (i > 0)
	            builder.append(" AND ");
	        
	        builder.append(SQLDataType.IDENTIFIER_QUOTE + cond.key() + SQLDataType.IDENTIFIER_QUOTE).append(" = ?");
	        
	    }

	    String query = builder.toString();

	    SQLConnection connection = this.database.open();

	    PreparedStatement statement = null;
	    
	    if (query.contains("until") && query.contains("by") && query.contains("ip"))
	    	System.out.println(query);
	    
	    try {
	    	
	        statement = connection.getDriverConnection().prepareStatement(query);

	        int index = 1;

	        List<String> remove = new ArrayList<String>();

	        for (Entry<String, Object> entry : this.entries.entrySet()) {

	            if (entry.getKey().equals(this.identifier))
	                continue;

	            Object obj = entry.getValue();

	            statement.setObject(index++, obj);

	            if (obj == null)
	                remove.add(entry.getKey());

	        }
	        
	        for (String key : remove)
	            this.entries.remove(key);
	        
	        for (SQLCondition<?> cond : conditions)
	            statement.setObject(index++, cond.value());
	        
	        affected += statement.executeUpdate();
	        
	        this.updated = true;

	    } catch (SQLException e) {
	        throw e;
	    } finally {

	        if (statement != null) {
	            try {
	                statement.close();
	            } catch (SQLException ignore) {
	            }
	        }

	        connection.release();

	    }

	    return affected;

	}
	
}
