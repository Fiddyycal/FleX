package io.flex.commons.sql;

import java.util.LinkedHashMap;
import java.util.Map;

public class SQLMap<K, V> extends LinkedHashMap<K, V> {

	private static final long serialVersionUID = 2951819978325854299L;
	
	@SafeVarargs
    public static <K, V> SQLMap<K, V> of(Map.Entry<K, V>... entries) {
		
		SQLMap<K, V> map = new SQLMap<K, V>();
        
        for (Map.Entry<K, V> entry : entries)
            map.put(entry.getKey(), entry.getValue());
        
        return map;
        
    }

    public static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return new LinkedHashMap.SimpleEntry<K, V>(key, value);
    }
	
}
