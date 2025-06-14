package io.flex.commons.sql;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Objects;

public class SQLDataTypeMap {
	
	public static <T> int asInt(Class<T> cls) {
		
		if (cls == null)
			return Types.NULL;
		
		if (cls == String.class)
			return Types.VARCHAR;
		
		if (cls == BigDecimal.class)
			return Types.NUMERIC;
		
		if (cls == Boolean.class || cls == boolean.class)
			return Types.BOOLEAN;
		
		if (cls == Byte.class || cls == byte.class)
			return Types.TINYINT;
		
		if (cls == Short.class || cls == short.class)
			return Types.SMALLINT;
		
		if (cls == Integer.class || cls == int.class)
			return Types.INTEGER;
		
		if (cls == Long.class || cls == long.class)
			return Types.BIGINT;
		
		if (cls == Float.class || cls == float.class)
			return Types.REAL;
		
		if (cls == Double.class || cls == double.class)
			return Types.DOUBLE;
		
		if (cls == Byte[].class || cls == byte[].class)
			return Types.LONGVARBINARY;
		
		if (cls == Date.class)
			return Types.DATE;
		
		if (cls == Time.class)
			return Types.TIME;
		
		if (cls == Timestamp.class)
			return Types.TIMESTAMP;
		
		if (cls == Clob.class)
			return Types.CLOB;
		
		if (cls == Blob.class)
			return Types.BLOB;
		
		if (cls == Array.class)
			return Types.ARRAY;
		
		if (cls == Struct.class)
			return Types.STRUCT;
		
		if (cls == Ref.class)
			return Types.REF;
		
		return Types.JAVA_OBJECT;

	}
	
	public static Class<?> asClass(int type) {
		
		switch (type) {

		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			return String.class;

		case Types.NUMERIC:
		case Types.DECIMAL:
			return BigDecimal.class;

		case Types.BIT:
			return Boolean.class;

		case Types.TINYINT:
			return Byte.class;

		case Types.SMALLINT:
			return Short.class;

		case Types.INTEGER:
			return Integer.class;

		case Types.BIGINT:
			return Long.class;

		case Types.REAL:
		case Types.FLOAT:
			return Float.class;
			
		case Types.DOUBLE:
			return Double.class;

		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
			return Byte[].class;

		case Types.DATE:
			return Date.class;

		case Types.TIME:
			return Time.class;

		case Types.TIMESTAMP:
			return Timestamp.class;

		default:
			return Object.class;

		}

	}

	public static Object wrap(String value, int type) {

		Objects.requireNonNull(value, "value must not be null");
		switch (type) {
		
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			return value;

		case Types.NUMERIC:
		case Types.DECIMAL:
			return new BigDecimal(value);
			
		case Types.BIT:
			return Boolean.parseBoolean(value);
			
		case Types.TINYINT:
			return Byte.valueOf(value);
			
		case Types.SMALLINT:
			return Short.valueOf(value);
			
		case Types.INTEGER:
			return Integer.parseInt(value);
			
		case Types.BIGINT:
			return Long.parseLong(value);
			
		case Types.FLOAT:
			return Float.parseFloat(value);
			
		case Types.REAL:
		case Types.DOUBLE:
			return Double.parseDouble(value);
			
		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
			return value.getBytes();
			
		case Types.DATE:
			return Date.valueOf(value);
			
		case Types.TIME:
			return Time.valueOf(value);
			
		case Types.TIMESTAMP:
			return Timestamp.valueOf(value);
			
		default:
			return value;
			
		}
		
	}

}