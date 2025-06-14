package io.flex.commons.utils;

import java.lang.annotation.Annotation;

import io.flex.commons.Nullable;
import io.flex.commons.NullObject;

public class ClassUtils {
	
	public static <T extends Annotation> T getSuperAnnotation(Class<?> clazz, Class<T> annotation) {
        while (clazz != null) {
        	
        	if (clazz.getDeclaredAnnotation(annotation) != null)
        		return clazz.getDeclaredAnnotation(annotation);
        	
            clazz = clazz.getSuperclass();
            
        }
        return null;
    }
	
	public static <T> boolean isFound(String clazz) {
		try {
			Class.forName(clazz);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public static <T> boolean isNull(T obj) {
		return obj == null || obj instanceof NullObject || obj.getClass().getDeclaredAnnotation(Nullable.class) != null;
	}
	
	public static <T> T is(Class<?> clazz, T obj, String message) {
		if (is(clazz, obj)) return obj;
		else throw new ClassCastException(message);
	}
	
	public static boolean is(Class<?> cls, Object obj) {
		try {
			
			// Primitives
			if (cls == int.class)
				return obj instanceof Integer;
			
			if (cls == boolean.class)
				return obj instanceof Boolean;
			
			if (cls == byte.class)
				return obj instanceof Byte;
			
			if (cls == char.class)
				return obj instanceof Character;
			
			if (cls == short.class)
				return obj instanceof Short;
			
			if (cls == double.class)
				return obj instanceof Double;
			
			if (cls == long.class)
				return obj instanceof Long;
			
			if (cls == float.class)
				return obj instanceof Float;
			
			cls.cast(obj);
			
			return obj != null;
			
		} catch (ClassCastException e) {
			return false;
		}
	}
	
	public static boolean isString(Object object) {
		return object instanceof String;
	}
	
	public static boolean isInteger(Object object) {
		return object instanceof Integer;
	}
    
	public static boolean isBoolean(Object object) {
		return object instanceof Boolean;
	}
	
	public static boolean isDouble(Object object) {
		return object instanceof Double;
	}
	
	public static boolean isByte(Object object) {
		return object instanceof Byte;
	}
	
	public static boolean isShort(Object object) {
		return object instanceof Short;
	}
	
	public static boolean isLong(Object object) {
		return object instanceof Long;
	}
	
	public static boolean isFloat(Object object) {
		return object instanceof Float;
	}
	
	public static boolean canParseAsString(Object object) {
		try {
			object.toString();
			return true;
		} catch (NullPointerException e) {
			return false;
		}
	}
	
	public static boolean canParseAsInteger(Object object) {
		try {
			Integer.parseInt(object.toString());
			return true;
		} catch (NullPointerException | NumberFormatException e) {
			return false;
		}
	}
    
	public static boolean canParseAsBoolean(Object object) {
		return object != null && (object.equals("false") || object instanceof Boolean ||  Boolean.parseBoolean(object.toString()));
	}
	
	public static boolean canParseAsDouble(Object object) {
		try {
			Double.parseDouble(object.toString());
			return true;
		} catch (NullPointerException | NumberFormatException e) {
			return false;
		}
	}
	
	public static boolean canParseAsByte(Object object) {
		try {
			Byte.parseByte(object.toString());
			return true;
		} catch (NullPointerException | NumberFormatException e) {
			return false;
		}
	}
	
	public static boolean canParseAsShort(Object object) {
		try {
			Short.parseShort(object.toString());
			return true;
		} catch (NullPointerException | NumberFormatException e) {
			return false;
		}
	}
	
	public static boolean canParseAsLong(Object object) {
		try {
			Long.parseLong(object.toString());
			return true;
		} catch (NullPointerException | NumberFormatException e) {
			return false;
		}
	}
	
	public static boolean canParseAsFloat(Object object) {
		try {
			Float.parseFloat(object.toString());
			return true;
		} catch (NullPointerException | NumberFormatException e) {
			return false;
		}
	}
	
}
