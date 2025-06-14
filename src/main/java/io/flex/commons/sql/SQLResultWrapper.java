package io.flex.commons.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import io.flex.FleX.Task;
import io.flex.commons.Severity;
import io.flex.commons.console.Console;

public abstract class SQLResultWrapper {
	
	// Delegation
	private ResultSet result;
	
	public abstract Statement getStatement();
	
	public abstract ResultSet asSet();
	
	public ResultSet asSetAsFirst() {
		
		if (this.result != null)
			return this.result;
		
		try {
			
			ResultSet set = this.asSet();
			
			this.result = set;
			
			if (set != null && !set.isClosed()) {
				
				ResultSetMetaData meta = set.getMetaData();
				
				if (meta.getColumnCount() > 0 && set.isBeforeFirst() && set.getType() != ResultSet.TYPE_FORWARD_ONLY)
					set.first();
				
			}
			
			return set;
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			
			return null;
			
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public synchronized <T> T get(String columnLabel) throws SQLException {
		try {
			return (T) this.asSetAsFirst().getObject(columnLabel);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public synchronized <T> T get(String columnLabel, Class<T> cls) throws SQLException {
		return this.get(columnLabel, SQLDataType.valueOf(cls));
	}

	@SuppressWarnings("unchecked")
	public synchronized <T> T get(String columnLabel, SQLDataType dataType) throws SQLException {
		
		if (dataType == SQLDataType.VARCHAR)
			return (T) this.asSetAsFirst().getString(columnLabel);
		
		if (dataType == SQLDataType.NUMERIC)
			return (T) this.asSetAsFirst().getBigDecimal(columnLabel);
		
		if (dataType == SQLDataType.BOOLEAN)
			throw new UnsupportedOperationException("Cannot cast from boolean to T. Please use ResultSet#getBoolean instead.");
		
		if (dataType == SQLDataType.TINYINT)
			throw new UnsupportedOperationException("Cannot cast from byte to T. Please use ResultSet#getByte instead.");
		
		if (dataType == SQLDataType.SMALLINT)
			throw new UnsupportedOperationException("Cannot cast from short to T. Please use ResultSet#getShort instead.");
		
		if (dataType == SQLDataType.INTEGER)
			throw new UnsupportedOperationException("Cannot cast from int to T. Please use ResultSet#getInteger instead.");
		
		if (dataType == SQLDataType.BIGINT)
			throw new UnsupportedOperationException("Cannot cast from long to T. Please use ResultSet#getLong instead.");
		
		if (dataType == SQLDataType.REAL)
			throw new UnsupportedOperationException("Cannot cast from float to T. Please use ResultSet#getFloat instead.");
		
		if (dataType == SQLDataType.DOUBLE)
			throw new UnsupportedOperationException("Cannot cast from double to T. Please use ResultSet#getDouble instead.");
		
		if (dataType == SQLDataType.LONGVARBINARY)
			return (T) this.asSetAsFirst().getBytes(columnLabel);
		
		if (dataType == SQLDataType.DATE)
			return (T) this.asSetAsFirst().getDate(columnLabel);
		
		if (dataType == SQLDataType.TIME)
			return (T) this.asSetAsFirst().getTime(columnLabel);
		
		if (dataType == SQLDataType.TIMESTAMP)
			return (T) this.asSetAsFirst().getTimestamp(columnLabel);
		
		if (dataType == SQLDataType.CLOB)
			return (T) this.asSetAsFirst().getClob(columnLabel);
		
		if (dataType == SQLDataType.BLOB)
			return (T) this.asSetAsFirst().getBlob(columnLabel);
		
		if (dataType == SQLDataType.ARRAY)
			return (T) this.asSetAsFirst().getArray(columnLabel);
		
		if (dataType == SQLDataType.REF)
			return (T) this.asSetAsFirst().getRef(columnLabel);
		
		return (T) this.asSetAsFirst().getObject(columnLabel);
		
	}

	public synchronized Array getArray(String columnLabel) throws SQLException {
		try {
			return this.asSetAsFirst().getArray(columnLabel);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public synchronized InputStream getAsciiStream(String columnLabel) throws SQLException {
		try {
			return this.asSetAsFirst().getAsciiStream(columnLabel);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public synchronized BigDecimal getBigDecimal(String columnLabel) throws SQLException {
		try {
			return this.asSetAsFirst().getBigDecimal(columnLabel);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("deprecation")
	public synchronized BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
		try {
			return this.asSetAsFirst().getBigDecimal(columnLabel, scale);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public synchronized InputStream getBinaryStream(String columnLabel) throws SQLException {
		try {
			return this.asSetAsFirst().getBinaryStream(columnLabel);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public synchronized Blob getBlob(String columnLabel) throws SQLException {
		try {
			return this.asSetAsFirst().getBlob(columnLabel);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public synchronized boolean getBoolean(String columnLabel) throws SQLException {
		try {
			return this.asSetAsFirst().getBoolean(columnLabel);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public synchronized byte getByte(String columnLabel) throws SQLException {
		try {
			return this.asSetAsFirst().getByte(columnLabel);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public synchronized byte[] getBytes(String columnLabel) throws SQLException {
		try {
			return this.asSetAsFirst().getBytes(columnLabel);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public synchronized Reader getCharacterStream(String columnLabel) throws SQLException {
		try {
			return this.asSetAsFirst().getCharacterStream(columnLabel);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public synchronized Clob getClob(String columnLabel) throws SQLException {
		try {
			return this.asSetAsFirst().getClob(columnLabel);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public synchronized Date getDate(String columnLabel) throws SQLException {
		try {
			return this.asSetAsFirst().getDate(columnLabel);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public synchronized Date getDate(String columnLabel, Calendar cal) throws SQLException {
		try {
			return this.asSetAsFirst().getDate(columnLabel, cal);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public synchronized double getDouble(String columnLabel) throws SQLException {
		try {
			return this.asSetAsFirst().getDouble(columnLabel);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public synchronized float getFloat(String columnLabel) throws SQLException {
		try {
			return this.asSetAsFirst().getFloat(columnLabel);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public synchronized int getInt(String columnLabel) throws SQLException {
		//try {
		return this.asSetAsFirst().getInt(columnLabel);
		/*} catch (SQLException e) {
					
			if (e.getMessage().contains("Before start of result set")) {
						
				try {
							
					ResultSet set = this.asSetAsFirst();
							
					set.first();
							
					return set.getInt(columnIndex);
							
				} catch (SQLException e2) {
					e2.printStackTrace();
				}
						
			}
			
			e.printStackTrace();
					
			return -1;
					
		}*/
	}

	public synchronized long getLong(String columnLabel) throws SQLException {
		return this.asSetAsFirst().getLong(columnLabel);
	}

	public synchronized Reader getNCharacterStream(String columnLabel) throws SQLException {
		return this.asSetAsFirst().getNCharacterStream(columnLabel);
	}

	public synchronized NClob getNClob(String columnLabel) throws SQLException {
		return this.asSetAsFirst().getNClob(columnLabel);
	}

	public synchronized String getNString(String columnLabel) throws SQLException {
		return this.asSetAsFirst().getNString(columnLabel);
	}

	public synchronized Object getObject(String columnLabel) throws SQLException {
		return this.asSetAsFirst().getObject(columnLabel);
	}

	public synchronized Ref getRef(String columnLabel) throws SQLException {
		return this.asSetAsFirst().getRef(columnLabel);
	}

	public synchronized RowId getRowId(String columnLabel) throws SQLException {
		return this.asSetAsFirst().getRowId(columnLabel);
	}

	public synchronized SQLXML getSQLXML(String columnLabel) throws SQLException {
		return this.asSetAsFirst().getSQLXML(columnLabel);
	}

	public synchronized short getShort(String columnLabel) throws SQLException {
		return this.asSetAsFirst().getShort(columnLabel);
	}

	public synchronized String getString(String columnLabel) throws SQLException {
		return this.asSetAsFirst().getString(columnLabel);
	}

	public synchronized Time getTime(String columnLabel) throws SQLException {
		return this.asSetAsFirst().getTime(columnLabel);
	}

	public synchronized Time getTime(String columnLabel, Calendar cal) throws SQLException {
		return this.asSetAsFirst().getTime(columnLabel, cal);
	}

	public synchronized Timestamp getTimestamp(String columnLabel) throws SQLException {
		return this.asSetAsFirst().getTimestamp(columnLabel);
	}

	public synchronized Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
		return this.asSetAsFirst().getTimestamp(columnLabel, cal);
	}

	public synchronized URL getURL(String columnLabel) throws SQLException {
		return this.asSetAsFirst().getURL(columnLabel);
	}
	
	@SuppressWarnings("deprecation")
	public synchronized InputStream getUnicodeStream(String columnLabel) throws SQLException {
		return this.asSetAsFirst().getUnicodeStream(columnLabel);
	}
	
	@SuppressWarnings("unchecked")
	public synchronized <T> T get(int columnIndex) throws SQLException {
		return (T) this.asSetAsFirst().getObject(columnIndex);
	}
	
	public synchronized <T> T get(int columnIndex, Class<T> cls) throws SQLException {
		return this.get(columnIndex, SQLDataType.valueOf(cls));
	}

	@SuppressWarnings("unchecked")
	public synchronized <T> T get(int columnIndex, SQLDataType dataType) throws SQLException {
		
		if (dataType == SQLDataType.VARCHAR)
			return (T) this.asSetAsFirst().getString(columnIndex);
		
		if (dataType == SQLDataType.NUMERIC)
			return (T) this.asSetAsFirst().getBigDecimal(columnIndex);
		
		if (dataType == SQLDataType.BOOLEAN)
			throw new UnsupportedOperationException("Cannot cast from boolean to T. Please use ResultSet#getBoolean instead.");
		
		if (dataType == SQLDataType.TINYINT)
			throw new UnsupportedOperationException("Cannot cast from byte to T. Please use ResultSet#getByte instead.");
		
		if (dataType == SQLDataType.SMALLINT)
			throw new UnsupportedOperationException("Cannot cast from short to T. Please use ResultSet#getShort instead.");
		
		if (dataType == SQLDataType.INTEGER)
			throw new UnsupportedOperationException("Cannot cast from int to T. Please use ResultSet#getInteger instead.");
		
		if (dataType == SQLDataType.BIGINT)
			throw new UnsupportedOperationException("Cannot cast from long to T. Please use ResultSet#getLong instead.");
		
		if (dataType == SQLDataType.REAL)
			throw new UnsupportedOperationException("Cannot cast from float to T. Please use ResultSet#getFloat instead.");
		
		if (dataType == SQLDataType.DOUBLE)
			throw new UnsupportedOperationException("Cannot cast from double to T. Please use ResultSet#getDouble instead.");
		
		if (dataType == SQLDataType.LONGVARBINARY)
			return (T) this.asSetAsFirst().getBytes(columnIndex);
		
		if (dataType == SQLDataType.DATE)
			return (T) this.asSetAsFirst().getDate(columnIndex);
		
		if (dataType == SQLDataType.TIME)
			return (T) this.asSetAsFirst().getTime(columnIndex);
		
		if (dataType == SQLDataType.TIMESTAMP)
			return (T) this.asSetAsFirst().getTimestamp(columnIndex);
		
		if (dataType == SQLDataType.CLOB)
			return (T) this.asSetAsFirst().getClob(columnIndex);
		
		if (dataType == SQLDataType.BLOB)
			return (T) this.asSetAsFirst().getBlob(columnIndex);
		
		if (dataType == SQLDataType.ARRAY)
			return (T) this.asSetAsFirst().getArray(columnIndex);
		
		if (dataType == SQLDataType.REF)
			return (T) this.asSetAsFirst().getRef(columnIndex);
		
		return (T) this.asSetAsFirst().getObject(columnIndex);
		
	}

	public synchronized Array getArray(int columnIndex) throws SQLException {
		return this.asSetAsFirst().getArray(columnIndex);
	}

	public synchronized InputStream getAsciiStream(int columnIndex) throws SQLException {
		return this.asSetAsFirst().getAsciiStream(columnIndex);
	}

	public synchronized BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return this.asSetAsFirst().getBigDecimal(columnIndex);
	}

	@SuppressWarnings("deprecation")
	public synchronized BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		return this.asSetAsFirst().getBigDecimal(columnIndex, scale);
	}

	public synchronized InputStream getBinaryStream(int columnIndex) throws SQLException {
		return this.asSetAsFirst().getBinaryStream(columnIndex);
	}

	public synchronized Blob getBlob(int columnIndex) throws SQLException {
		return this.asSetAsFirst().getBlob(columnIndex);
	}

	public synchronized boolean getBoolean(int columnIndex) throws SQLException {
		return this.asSetAsFirst().getBoolean(columnIndex);
	}

	public synchronized byte getByte(int columnIndex) throws SQLException {
		return this.asSetAsFirst().getByte(columnIndex);
	}

	public synchronized byte[] getBytes(int columnIndex) throws SQLException {
		return this.asSetAsFirst().getBytes(columnIndex);
	}

	public synchronized Reader getCharacterStream(int columnIndex) throws SQLException {
		return this.asSetAsFirst().getCharacterStream(columnIndex);
	}

	public synchronized Clob getClob(int columnIndex) throws SQLException {
		return this.asSetAsFirst().getClob(columnIndex);
	}

	public synchronized Date getDate(int columnIndex) throws SQLException {
		return this.asSetAsFirst().getDate(columnIndex);
	}

	public synchronized Date getDate(int columnIndex, Calendar cal) throws SQLException {
		return this.asSetAsFirst().getDate(columnIndex, cal);
	}

	public synchronized double getDouble(int columnIndex) throws SQLException {
		return this.asSetAsFirst().getDouble(columnIndex);
	}

	public synchronized float getFloat(int columnIndex) throws SQLException {
		return this.asSetAsFirst().getFloat(columnIndex);
	}

	public synchronized int getInt(int columnIndex) throws SQLException {
		//try {
		return this.asSetAsFirst().getInt(columnIndex);
		/*} catch (SQLException e) {
			
			if (e.getMessage().contains("Before start of result set")) {
				
				try {
					
					ResultSet set = this.asSetAsFirst();
					
					set.first();
					
					return set.getInt(columnIndex);
					
				} catch (SQLException e2) {
					e2.printStackTrace();
				}
				
			}
			
			e.printStackTrace();
			
			return -1;
			
		}*/
	}

	public synchronized long getLong(int columnIndex) throws SQLException {
		return this.asSetAsFirst().getLong(columnIndex);
	}

	public synchronized Reader getNCharacterStream(int columnIndex) throws SQLException {
		return this.asSetAsFirst().getNCharacterStream(columnIndex);
	}

	public synchronized NClob getNClob(int columnIndex) throws SQLException {
		return this.asSetAsFirst().getNClob(columnIndex);
	}

	public synchronized String getNString(int columnIndex) throws SQLException {
		return this.asSetAsFirst().getNString(columnIndex);
	}

	public synchronized Object getObject(int columnIndex) throws SQLException {
		return this.asSetAsFirst().getObject(columnIndex);
	}

	public synchronized Ref getRef(int columnIndex) throws SQLException {
		return this.asSetAsFirst().getRef(columnIndex);
	}

	public synchronized RowId getRowId(int columnIndex) throws SQLException {
		return this.asSetAsFirst().getRowId(columnIndex);
	}

	public synchronized SQLXML getSQLXML(int columnIndex) throws SQLException {
		return this.asSetAsFirst().getSQLXML(columnIndex);
	}

	public synchronized short getShort(int columnIndex) throws SQLException {
		return this.asSetAsFirst().getShort(columnIndex);
	}

	public synchronized String getString(int columnIndex) throws SQLException {
		return this.asSetAsFirst().getString(columnIndex);
	}

	public synchronized Time getTime(int columnIndex) throws SQLException {
		return this.asSetAsFirst().getTime(columnIndex);
	}

	public synchronized Time getTime(int columnIndex, Calendar cal) throws SQLException {
		return this.asSetAsFirst().getTime(columnIndex, cal);
	}

	public synchronized Timestamp getTimestamp(int columnIndex) throws SQLException {
		return this.asSetAsFirst().getTimestamp(columnIndex);
	}

	public synchronized Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		return this.asSetAsFirst().getTimestamp(columnIndex, cal);
	}

	public synchronized URL getURL(int columnIndex) throws SQLException {
		return this.asSetAsFirst().getURL(columnIndex);
	}
	
	@SuppressWarnings("deprecation")
	public synchronized InputStream getUnicodeStream(int columnIndex) throws SQLException {
		return this.asSetAsFirst().getUnicodeStream(columnIndex);
	}
	
	public synchronized void closeAll() {
		try {
			
			this.getStatement().close();
			this.asSetAsFirst().close();
			
		} catch (SQLException e) {
			
			Task.error("SQL (" + Severity.CRITICAL + ")", "Failed to close io.flex.commons.sql.SQLResultStore.");
	    	Console.log("SQL", Severity.CRITICAL, e);
			
		}
	}
	
}
