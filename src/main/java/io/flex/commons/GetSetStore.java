package io.flex.commons;

public interface GetSetStore {
	
	public <T> T get(String key);
	
	public byte getByte(String key);
	
	public short getShort(String key);
	
	public int getInt(String key);
	
	public long getLong(String key);
	
	public float getFloat(String key);
	
	public double getDouble(String key);
	
	public String getString(String key);
	
	public <T> T get(String key, T def);
	
	public byte getByte(String key, byte def);
	
	public short getShort(String key, short def);
	
	public int getInt(String key, int def);
	
	public long getLong(String key, long def);
	
	public float getFloat(String key, float def);
	
	public double getDouble(String key, double def);
	
	public String getString(String key, String def);
	
	public <T> void set(String key, T value);
	
	public <T> void setByte(String key, byte value);
	
	public <T> void setShort(String key, short value);
	
	public <T> void setInt(String key, int value);
	
	public <T> void setLong(String key, long value);
	
	public <T> void setFloat(String key, float value);
	
	public <T> void setDouble(String key, double value);
	
	public <T> void setString(String key, String value);

}
