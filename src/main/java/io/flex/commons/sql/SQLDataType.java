/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package io.flex.commons.sql;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.SQLType;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * <P>Defines the constants that are used to identify generic
 * SQL types, called JDBC types.
 * <p>
 * @see SQLType
 * @since 1.8
 */
public enum SQLDataType implements SQLType {
	
    /**
     * Identifies the generic SQL type {@code BIT}.
     */
    BIT(Types.BIT),
    /**
     * Identifies the generic SQL type {@code TINYINT}.
     */
    TINYINT(Types.TINYINT),
    /**
     * Identifies the generic SQL type {@code SMALLINT}.
     */
    SMALLINT(Types.SMALLINT),
    /**
     * Identifies the generic SQL type {@code INTEGER}.
     */
    INTEGER(Types.INTEGER),
    /**
     * Identifies the generic SQL type {@code BIGINT}.
     */
    BIGINT(Types.BIGINT),
    /**
     * Identifies the generic SQL type {@code FLOAT}.
     */
    FLOAT(Types.FLOAT),
    /**
     * Identifies the generic SQL type {@code REAL}.
     */
    REAL(Types.REAL),
    /**
     * Identifies the generic SQL type {@code DOUBLE}.
     */
    DOUBLE(Types.DOUBLE),
    /**
     * Identifies the generic SQL type {@code NUMERIC}.
     */
    NUMERIC(Types.NUMERIC),
    /**
     * Identifies the generic SQL type {@code DECIMAL}.
     */
    DECIMAL(Types.DECIMAL),
    /**
     * Identifies the generic SQL type {@code CHAR}.
     */
    CHAR(Types.CHAR),
    /**
     * Identifies the generic SQL type {@code VARCHAR}.
     */
    VARCHAR(Types.VARCHAR, 255),
    /**
     * Identifies the generic SQL type {@code LONGVARCHAR}.
     */
    LONGVARCHAR(Types.LONGVARCHAR, 255),
    /**
     * Identifies the generic SQL type {@code DATE}.
     */
    DATE(Types.DATE),
    /**
     * Identifies the generic SQL type {@code TIME}.
     */
    TIME(Types.TIME),
    /**
     * Identifies the generic SQL type {@code TIMESTAMP}.
     */
    TIMESTAMP(Types.TIMESTAMP),
    /**
     * Identifies the generic SQL type {@code BINARY}.
     */
    BINARY(Types.BINARY, 255),
    /**
     * Identifies the generic SQL type {@code VARBINARY}.
     */
    VARBINARY(Types.VARBINARY, 255),
    /**
     * Identifies the generic SQL type {@code LONGVARBINARY}.
     */
    LONGVARBINARY(Types.LONGVARBINARY, 255),
    /**
     * Identifies the generic SQL value {@code NULL}.
     */
    NULL(Types.NULL),
    /**
     * Indicates that the SQL type
     * is database-specific and gets mapped to a Java object that can be
     * accessed via the methods getObject and setObject.
     */
    OTHER(Types.OTHER),
    /**
     * Indicates that the SQL type
     * is database-specific and gets mapped to a Java object that can be
     * accessed via the methods getObject and setObject.
     */
    JAVA_OBJECT(Types.JAVA_OBJECT),
    /**
     * Identifies the generic SQL type {@code DISTINCT}.
     */
    DISTINCT(Types.DISTINCT),
    /**
     * Identifies the generic SQL type {@code STRUCT}.
     */
    STRUCT(Types.STRUCT),
    /**
     * Identifies the generic SQL type {@code ARRAY}.
     */
    ARRAY(Types.ARRAY),
    /**
     * Identifies the generic SQL type {@code BLOB}.
     */
    BLOB(Types.BLOB),
    /**
     * Identifies the generic SQL type {@code CLOB}.
     */
    CLOB(Types.CLOB),
    /**
     * Identifies the generic SQL type {@code REF}.
     */
    REF(Types.REF),
    /**
     * Identifies the generic SQL type {@code DATALINK}.
     */
    DATALINK(Types.DATALINK),
    /**
     * Identifies the generic SQL type {@code BOOLEAN}.
     */
    BOOLEAN(Types.BOOLEAN),

    /* JDBC 4.0 Types */

    /**
     * Identifies the SQL type {@code ROWID}.
     */
    ROWID(Types.ROWID),
    /**
     * Identifies the generic SQL type {@code NCHAR}.
     */
    NCHAR(Types.NCHAR, 255),
    /**
     * Identifies the generic SQL type {@code NVARCHAR}.
     */
    NVARCHAR(Types.NVARCHAR, 255),
    /**
     * Identifies the generic SQL type {@code LONGNVARCHAR}.
     */
    LONGNVARCHAR(Types.LONGNVARCHAR, 255),
    /**
     * Identifies the generic SQL type {@code NCLOB}.
     */
    NCLOB(Types.NCLOB),
    /**
     * Identifies the generic SQL type {@code SQLXML}.
     */
    SQLXML(Types.SQLXML),

    /* JDBC 4.2 Types */

    /**
     * Identifies the generic SQL type {@code REF_CURSOR}.
     */
    REF_CURSOR(Types.REF_CURSOR),

    /**
     * Identifies the generic SQL type {@code TIME_WITH_TIMEZONE}.
     */
    TIME_WITH_TIMEZONE(Types.TIME_WITH_TIMEZONE),
    /**
     * Identifies the generic SQL type {@code TIMESTAMP_WITH_TIMEZONE}.
     */
    TIMESTAMP_WITH_TIMEZONE(Types.TIMESTAMP_WITH_TIMEZONE),
    /**
     * Identifies the generic MySQL type {@code ENUM}.
     */
    
    /* MySQL 8.0 Types */
    
    /**
     * Identifies the generic MySQL type {@code GEOMETRY}.
     */
	@Deprecated
    GEOMETRY(-1),
    
    /**
     * Identifies the generic MySQL type {@code DATETIME}.
     */
	@Deprecated
    DATETIME(-1),
    
    /**
     * Identifies the generic MySQL type {@code YEAR}.
     */
	@Deprecated
	YEAR(-1),
    
    /**
     * Identifies the generic MySQL type {@code MEDIUMINT}.
     */
	@Deprecated
	MEDIUMINT(-1),
    
    /* MySQL 8.0.20-0ubuntu0.20.04.1 Types */

    /**
     * Identifies the generic MySQL type {@code INT}.
     */
    INT(Types.INTEGER),
    /**
     * Identifies the generic MySQL type {@code TINYTEXT}.
     */
    TINYTEXT(Types.VARCHAR, 255),
    /**
     * Identifies the generic MySQL type {@code TEXT}.
     */
    TEXT(Types.LONGVARCHAR, 255),
    /**
     * Identifies the generic MySQL type {@code MEDIUMTEXT}.
     */
	@Deprecated
    MEDIUMTEXT(Types.LONGVARCHAR, 255),
    /**
     * Identifies the generic MySQL type {@code LONGTEXT}.
     */
	@Deprecated
    LONGTEXT(Types.LONGVARCHAR, 255),
    /**
     * Identifies the generic MySQL type {@code TINYBLOB}.
     */
    TINYBLOB(Types.BLOB),
    /**
     * Identifies the generic MySQL type {@code MEDIUMBLOB}.
     */
	@Deprecated
    MEDIUMBLOB(Types.BLOB),
    /**
     * Identifies the generic MySQL type {@code LONGBLOB}.
     */
	@Deprecated
    LONGBLOB(Types.BLOB),
    /**
     * Identifies the generic MySQL type {@code ENUM}.
     */
	@Deprecated
	ENUM(Types.VARCHAR, 255),
    /**
     * Identifies the generic MySQL type {@code SET}.
     */
	@Deprecated
	SET(Types.TINYINT),
    /**
     * Identifies the generic MySQL type {@code JSON}.
     */
	@Deprecated
	JSON(Types.VARCHAR, 255);
    
    /**
     * The Integer value for the JDBCType. It maps to a value in
     * {@code Types.java}
     */
    private Integer type;
    
    private int length;

    /**
     * Constructor to specify the data type value from {@code Types) for
     * this data type.
     * @param type The value from {@code Types) for this data type
     */
    private SQLDataType(final Integer type) {
    	this(type, -1);
    }

    /**
     * Constructor to specify the data type value from {@code Types) for
     * this data type.
     * @param type The value from {@code Types) for this data type
     * @param length The default length used in structured queries for this data type
     */
    private SQLDataType(final Integer type, int length) {
        this.type = type;
        this.length = length < 0 ? -1 : length;
    }
    
    /**
     *{@inheritDoc}
     * @return The name of this {@code SQLType}.
     */
    public String getName() {
        return name();
    }
    
    /**
     * Returns the name of the vendor that supports this data type.
     * @return  The name of the vendor for this data type which is
     * {@literal java.sql} for JDBCType.
     */
    public String getVendor() {
        return "java.sql";
    }
    
    /**
     * Returns the vendor specific type number for the data type.
     * @return  An Integer representing the data type. For {@code JDBCType},
     * the value will be the same value as in {@code Types} for the data type.
     */
    public Integer getVendorTypeNumber() {
        return this.type;
    }

    /**
     * Returns the default length used in structured queries for the data type.
     * @return  An Integer representing the data length.
     */
    public int getLength() {
		return this.length;
	}
    
    public Object wrap(String value) {
    	
    	switch (this) {
		
		case CHAR:
		case VARCHAR:
		case LONGVARCHAR:
			return value;

		case NUMERIC:
		case DECIMAL:
			return new BigDecimal(value);
			
		case BIT:
			return Boolean.parseBoolean(value);
			
		case TINYINT:
			return Byte.parseByte(value);
			
		case SMALLINT:
			return Short.parseShort(value);
			
		case INTEGER:
			return Integer.parseInt(value);
			
		case BIGINT:
			return Long.parseLong(value);
			
		case FLOAT:
			return Float.parseFloat(value);
			
		case REAL:
		case DOUBLE:
			return Double.parseDouble(value);
			
		case BINARY:
		case VARBINARY:
		case LONGVARBINARY:
			return value.getBytes();
			
		case DATE:
			return Date.valueOf(value);
			
		case TIME:
			return Time.valueOf(value);
			
		case TIMESTAMP:
			return Timestamp.valueOf(value);
			
		default:
			return value;
			
		}
    	
    }
    
    /**
     * Returns the {@code JDBCType} that corresponds to the specified
     * {@code Types} value
     * @param type {@code Types} value
     * @return The {@code JDBCType} constant
     * @throws IllegalArgumentException if this enum type has no constant with
     * the specified {@code Types} value
     * @see Types
     */
    public static SQLDataType valueOf(int type) {
        for(SQLDataType sqlType : SQLDataType.class.getEnumConstants()) {
            if(type == sqlType.type)
                return sqlType;
        }
        throw new IllegalArgumentException("Type:" + type + " is not a valid "
                + "Types.java value.");
    }

    public static SQLDataType valueOf(Class<?> cls) {
    	
		if (cls == null)
			return SQLDataType.NULL;
		
		if (cls == String.class)
			return SQLDataType.VARCHAR;
		
		if (cls == BigDecimal.class)
			return SQLDataType.NUMERIC;
		
		if (cls == Boolean.class || cls == boolean.class)
			return SQLDataType.BOOLEAN;
		
		if (cls == Byte.class || cls == byte.class)
			return SQLDataType.TINYINT;
		
		if (cls == Short.class || cls == short.class)
			return SQLDataType.SMALLINT;
		
		if (cls == Integer.class || cls == int.class)
			return SQLDataType.INTEGER;
		
		if (cls == Long.class || cls == long.class)
			return SQLDataType.BIGINT;
		
		if (cls == Float.class || cls == float.class)
			return SQLDataType.REAL;
		
		if (cls == Double.class || cls == double.class)
			return SQLDataType.DOUBLE;
		
		if (cls == Byte[].class || cls == byte[].class)
			return SQLDataType.LONGVARBINARY;
		
		if (cls == Date.class)
			return SQLDataType.DATE;
		
		if (cls == Time.class)
			return SQLDataType.TIME;
		
		if (cls == Timestamp.class)
			return SQLDataType.TIMESTAMP;
		
		if (cls == Clob.class)
			return SQLDataType.CLOB;
		
		if (cls == Blob.class)
			return SQLDataType.BLOB;
		
		if (cls == Array.class)
			return SQLDataType.ARRAY;
		
		if (cls == Struct.class)
			return SQLDataType.STRUCT;
		
		if (cls == Ref.class)
			return SQLDataType.REF;
		
		return SQLDataType.JAVA_OBJECT;
		
    }
    
    public boolean hasLengthSyntax() {
		return this.length > 0;
	}
	
	public static final String STRING_QUOTE = "'";
	public static final String IDENTIFIER_QUOTE = "`";
    
}