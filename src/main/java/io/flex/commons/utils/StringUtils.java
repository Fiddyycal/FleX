package io.flex.commons.utils;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;
import java.util.stream.Collectors;

import io.flex.commons.Nullable;
import io.flex.commons.RandomNumberGenerator;

public class StringUtils {
	
	public static final String[] PREFIX_STRING_ARRAY = {
			
			"the", "its", "itz", "x", "_", "i", "xX", "strafe", "im", "daddy", "xx", "z"
			
	};
	
	public static final String[] MIDDLE_STRING_ARRAY = { 
			
			"banana", "proo", "strafe", "Skilled", "Skills", "pvp",
			"kitty", "42O", "ping", "Hax0r", "SCRUB", "randy", "pvping",
			"hackker", "clap", "insane", "monkey", "drinking", "gamer", "demon", "lost",
			"average", "baked", "stingy"
			
	};
	
	public static final String[] SUFFIX_STRING_ARRAY = {
			
			"x", "_", "mlg", "Oh", "Xx", "AU", "US", "EU", "aahhh", "2k", "z", "123"
			
	};
	
	public static final String CONSONANTS = "bcdfghjklmnpqrstvwxz";
	
	public static String repeat(String s, int amount) {
		
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < amount; i++)
			builder.append(s);
		
		return builder.toString();
		
	}
	
	public static String capitalize(String s) {
		
		if (s == null || s.length() == 0)
			return s;
		
		if (s.contains(" ")) {
			
			String[] spl = s.split(" ");
			
			for (int i = 0; i < spl.length; i++)
				s = s.replace(spl[i], capitalize(spl[i]));
			
			return s;
			
		}
		
		String cap = s.substring(0, 1).toUpperCase();
		
		if (s.length() == 1)
			return cap;
		
		return cap + s.substring(1);
		
	}
	
	public static <T extends Object> String join(T[] a, String joiner) {
		
		if (a == null || a.length == 0)
			return null;
		
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < a.length; i++) {
			
			builder.append(a[i].toString());
			
			if ((i + 1) != a.length)
				builder.append(joiner);
			
		}
		
		return builder.toString();
		
	}
	
	public static <T> String join(Collection<T> a, String joiner) {
		
		if (a == null || a.isEmpty())
			return null;
		
		StringBuilder builder = new StringBuilder();
		
		@SuppressWarnings("unchecked")
		LinkedList<String> join = (LinkedList<String>) a.stream().collect(Collectors.toCollection(LinkedList::new));
		
		for (int i = 0; i < join.size(); i++) {
			
			builder.append(join.get(i).toString());
			
			if ((i + 1) != join.size())
				builder.append(joiner);
			
		}
		
		return builder.toString();
		
	}
	
	public static String shorten(String s, int trim, int length) {
		return s != null && trim > -1 && length > -1 && s.length() > trim ? (s.length() > length ? s.substring(trim, length) : s.substring(trim)) : "";
	}
	
	public static String generate(int length, boolean intelligible) {
		
		if (intelligible) {
			
			RandomNumberGenerator rng = NumUtils.getRng();
			
			String prefix = PREFIX_STRING_ARRAY[rng.getInt(0, PREFIX_STRING_ARRAY.length-1)];
			String middle = MIDDLE_STRING_ARRAY[rng.getInt(0, MIDDLE_STRING_ARRAY.length-1)];
			String suffix = SUFFIX_STRING_ARRAY[rng.getInt(0, SUFFIX_STRING_ARRAY.length-1)];
			
			int i = rng.getInt(0, 4);
			int j = rng.getInt(0, 1);
			int k = rng.getInt(0, 6);
			
			String dis = i == 0 ? "" : i == 3 ? capitalize(prefix) : prefix;
			String play = j == 0 ? capitalize(middle) : j == 1 ? middle : "";
			String name = k == 0 ? capitalize(suffix) : k == 1 ? suffix : "";
			
			String displayName = dis + play + name;
			String generated = displayName.length() > length ? displayName.substring(0, length) : displayName;
			
			return shorten(generated, 0, 16);
			
		} else {
			
			byte[] array = new byte[256];
			
	        RandomNumberGenerator.RANDOM.nextBytes(array); 
	        
	        String random = new String(array, Charset.forName("UTF-8")); 
	        
	        StringBuffer buffer = new StringBuffer(); 
	        
	        for (int k = 0; k < random.length(); k++) {
	        	
	            char ch = random.charAt(k);
	            
	            if (((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9')) && (length > 0)) {
	            	buffer.append(ch); 
	                length--; 
	            }
	            
	        }
	        
	        return buffer.toString();
			
		}
		
	}
    
    public static String progressBar(double percentage, @Nullable String prefix, @Nullable String suffix, String loaded, String loading, int length) {
    	int repeat = (int) percentage * length / 100;
        return (prefix != null ? prefix : "") + repeat(loaded, repeat) + repeat(loading, length - repeat) + (suffix != null ? suffix : "");
    }
	
	public static boolean equalsIgnoreCaseAny(String s, String... args) {
		return Arrays.asList(args).stream().anyMatch(a -> a.equalsIgnoreCase(s));
	}
	
	public static boolean equalsAny(String s, String... equals) {
		return Arrays.asList(equals).stream().anyMatch(a -> a.equals(s));
	}
	
	public static UUID toUUID(String uuid) throws IllegalArgumentException {
		
    	try {
        	return UUID.fromString(uuid);
		} catch (IllegalArgumentException e) {
        	throw new IllegalArgumentException("Could not convert " + uuid + " to uuid valid format. (<timeLow>-<timeMid>-<timeHighAndVersion>-<clockSeqAndReservedClockSeqLow>-<node>).");
		}
    	
	}
	
	public static boolean isUUID(Object obj) {
		try {
			UUID.fromString(obj.toString());
			return true;
		} catch (IllegalArgumentException | NullPointerException e) {
			return false;
		}
	}
	
	public static boolean isLink(String link) {
		
		return (link.startsWith("http://") || link.startsWith("https://") || link.startsWith("www.")) &&
				
				(link.contains(".co") || link.contains(".net") || link.contains(".gg") ||
				 link.contains(".au") || link.contains(".us") || link.contains(".eu"));
		
	}
	
	public static String buildSentence(String separator, String... args) {
		StringBuilder b = new StringBuilder();
		Arrays.asList(args).stream().forEach(s -> {
			if (b.length() != 0) b.append(separator);
			b.append(s);
		});
		return b.toString();
	}
    
    public static boolean startsWithVowel(String arg0) {
    	if ("aeiou".indexOf(Character.toLowerCase(arg0.charAt(0))) != -1) return true;
    	return false;
    }
    
    public static String plural(String singular) {
    	return plural(singular, 2);
    }
    
    public static String plural(String singular, int test) {
    	
    	if (test == 1)
    		return singular;
    	
        switch (singular.toLowerCase()) {
        case
             "is":
            return "are";
        case "person":
        	return "people";
        case "trash":
            return "trash";
        case "life":
        	return "lives";
        case "man":
        	return "men";
        case "woman":
        	return "women";
        case "child":
        	return "children";
        case "foot":
        	return "feet";
        case "tooth":
        	return "teeth";
        case "dozen":
        	return "dozen";
        case "hundred":
        	return "hundred";
        case "thousand":
        	return "thousand";
        case "million":
        	return "million";
        case "datum":
        	return "data";
        case "criterion":
            return "criteria";
        case "analysis":
            return "analyses";
        case "fungus":
        	return "fungi";
        case "index":
        	return "indices";
        case "matrix":
        	return "matrices";
        case "settings":
        	return "settings";
        case "mouse":
        	return "mice";
        default:
        	
        	boolean consonant = singular.length() >= 2 && CONSONANTS.contains(Character.toString(singular.charAt(singular.length() - 2)));
        	
        	if (singular.endsWith("o") && consonant) {
        		return singular + "es";
            }
        	
        	if (singular.endsWith("y") && consonant) {
                return singular.substring(0, singular.length() - 1) + "ies";
            }
        	
            if (singular.endsWith("s") || singular.endsWith("sh") || singular.endsWith("ch") || singular.endsWith("x") || singular.endsWith("z")) {
                return singular + "es";
            }
            
            return singular + "s";

        }
    }
	
}
