package io.flex.commons.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class CollectionUtils {

	public static <T> boolean remove(Collection<T> collection, T t) {
		
		boolean result = false;
		Iterator<T> it = collection.iterator();
		
		while(result = it.hasNext())
			if (it.next() == t) {
				it.remove();
				break;
			}
		
		return result;
		
	}
	
	public static Map<String, String> toMap(String s) {
		
		Map<String, String> map = new LinkedHashMap<String, String>();
		
		if (s == null)
			return map;
		
		if (s.startsWith("{"))
			s = s.substring(1, s.length());
		
		if (s.endsWith("}"))
			s = s.substring(0, s.length() - 1);
		
		if (s.length() == 0 || !s.contains("="))
			return map;
		
		String[] pairs = s.split(", ");
		
		for (int i = 0 ; i < pairs.length; i++) {
			
		    String[] pair = pairs[i].split("=");
		    
		    map.put(pair[0], pair[1]);
		    
		}
		
		return map;
		
	}
	
	public static Collection<String> toCollection(String s) {
		
		if (s == null || s.equals("[]"))
			return new LinkedList<String>();
		
		if (s != null && s.length() > 2 && s.charAt(0) == '[' && s.charAt(s.length() - 1) == ']')
			s = s.substring(0, s.length() - 1).substring(1);
		
		/** Arrays asList method is immutable, new ArrayList makes list mutable. */
		return new LinkedList<String>(Arrays.asList(s != null ? s.split(", ") : new String[0]));
		
	}
	
}
