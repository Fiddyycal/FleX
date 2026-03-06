package io.flex.commons.sql;

import java.util.LinkedHashMap;
import java.util.Map;

public class SQLMap extends LinkedHashMap<String, Object> {

	private static final long serialVersionUID = 2951819978325854299L;
	
	@SafeVarargs
    public static SQLMap of(Map.Entry<String, Object>... entries) {
		
		SQLMap map = new SQLMap();
        
        for (Map.Entry<String, Object> entry : entries)
            map.put(entry.getKey(), entry.getValue());
        
        return map;
        
    }

    public static Map.Entry<String, Object> entry(String key, Object value) {
        return new LinkedHashMap.SimpleEntry<String, Object>(key, value);
    }
	
}
