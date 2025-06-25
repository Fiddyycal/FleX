package io.flex.commons.utils;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import io.flex.commons.RandomNumberGenerator;

public class NumUtils {
	
	public static final long SECOND_TO_MILLIS = 1000L;
	public static final long MINUTE_TO_MILLIS = SECOND_TO_MILLIS * 60L;
	public static final long HOUR_TO_MILLIS = MINUTE_TO_MILLIS * 60L;
	public static final long DAY_TO_MILLIS = HOUR_TO_MILLIS * 24L;
	public static final long MONTH_TO_MILLIS = DAY_TO_MILLIS * 30L;
	public static final long YEAR_TO_MILLIS = MONTH_TO_MILLIS * 12L;
	public static final long DECADE_TO_MILLIS = YEAR_TO_MILLIS * 10L;

	private static final RandomNumberGenerator rng = new RandomNumberGenerator();
	
	public static RandomNumberGenerator getRng() {
		return rng;
	}
	
	public static int getNextId(Predicate<Integer> predicate) {
		return getNextId(predicate, 0);
	}
	
	public static int getNextId(Predicate<Integer> predicate, int min) {
		
		for (int i = min; i < Integer.MAX_VALUE; i++) {
			
			if (predicate.test(i))
				return i;
			
		}
		
		return Integer.MAX_VALUE;
		
	}
	
    public static boolean canParseAsInt(String arg0) {
    	try {
			Integer.parseInt(arg0);
			return true;
		} catch (Exception e) {
			return false;
		}
    }
    
    public static boolean canParseAsDouble(String arg0) {
    	try {
			double d = Double.parseDouble(arg0);
			return (d == Math.floor(d)) && !Double.isInfinite(d) == false;
		} catch (Exception e) {
			return false;
		}
    }
    
    public static boolean isWithinRange(double input, double number, double deviation) {
        return (input - deviation <= number && number <= input + deviation);
    }
    
    public static double roundToDecimal(double value, int places) {
    	
        if (places < 0)
        	throw new IllegalArgumentException("Cannot round to or lower than 0.");
        
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        
        long tmp = Math.round(value);
        return (double) tmp / factor;
        
    }
    
    public static int getFractional(double number, int places) {
    	
    	if (places > String.valueOf(Integer.MAX_VALUE).length())
    		places = String.valueOf(Integer.MAX_VALUE).length();
    	
		String frac = String.valueOf(number).split("\\.")[1];
		return Integer.parseInt(frac.length() >= places ? frac.substring(0, places) : frac);
		
    }
    
    public static double getPercentage(double total, double fraction) {
    	return fraction * 100 / total;
    }
    
    public static String toRomanNumeral(int value) {
    	
        if (value <= 0)
        	throw new IllegalArgumentException("Value cannot be 0 or lower.");
        
        String s = "";
        
        while (value >= 1000) {
            s += "M";
            value -= 1000;        
        } while (value >= 900) {
            s += "CM";
            value -= 900;
        } while (value >= 500) {
            s += "D";
            value -= 500;
        } while (value >= 400) {
            s += "CD";
            value -= 400;
        } while (value >= 100) {
            s += "C";
            value -= 100;
        } while (value >= 90) {
            s += "XC";
            value -= 90;
        } while (value >= 50) {
            s += "L";
            value -= 50;
        } while (value >= 40) {
            s += "XL";
            value -= 40;
        } while (value >= 10) {
            s += "X";
            value -= 10;
        } while (value >= 9) {
            s += "IX";
            value -= 9;
        } while (value >= 5) {
            s += "V";
            value -= 5;
        } while (value >= 4) {
            s += "IV";
            value -= 4;
        } while (value >= 1) {
            s += "I";
            value -= 1;
        }
        
        return s;
        
    }
	
	public static String asTime(long ms) {
		return asTime(ms, true);
	}
	
	public static String asTime(long ms, boolean fileSafe) {
		return (fileSafe ? new SimpleDateFormat("hh_mm_ss a") : new SimpleDateFormat("hh:mm:ss a")).format(ms);
	}
	
	public static String asDate(long ms) {
		return asDate(ms, true);
	}
	
	public static String asDate(long ms, boolean fileSafe) {
		return (fileSafe ? new SimpleDateFormat("dd-MM-yyyy") : new SimpleDateFormat("EEE, MMM d, yyyy")).format(ms);
	}
	
	public static String asDateTime(long ms) {
		return asDateTime(ms, true);
	}
	
	public static String asDateTime(long ms, boolean fileSafe) {
		return (fileSafe ? new SimpleDateFormat("[hh_mm_ss a] dd-MM-yyyy") : new SimpleDateFormat("(hh:mm:ss a) EEE, MMM d, yyyy")).format(ms);
	}
    
	public static String asString(long ms) {
		
		if (ms <= 0)
			return "Now";

		long years = TimeUnit.MILLISECONDS.toDays(ms) / 365;
		ms -= TimeUnit.DAYS.toMillis(years) * 365;

		long months = TimeUnit.MILLISECONDS.toDays(ms) / 30;
		ms -= TimeUnit.DAYS.toMillis(months) * 30;
		
		long days = TimeUnit.MILLISECONDS.toDays(ms);
		ms -= TimeUnit.DAYS.toMillis(days);
		
        long hours = TimeUnit.MILLISECONDS.toHours(ms);
        ms -= TimeUnit.HOURS.toMillis(hours);
        
        long minutes = TimeUnit.MILLISECONDS.toMinutes(ms);
        ms -= TimeUnit.MINUTES.toMillis(minutes);
        
        long seconds = TimeUnit.MILLISECONDS.toSeconds(ms);
        
        StringBuilder sb = new StringBuilder();
        
        if (years > 0)
        	sb.append((sb.length() > 0 ? ", " : "") + years + (years != 1 ? " Years" : " Year"));
        
        if (months > 0)
        	sb.append((sb.length() > 0 ? ", " : "") + months + (months != 1 ? " Months" : " Month"));
        
        if (days > 0)
        	sb.append((sb.length() > 0 ? ", " : "") + days + (days != 1 ? " Days" : " Day"));
        
        if (hours > 0)
        	sb.append((sb.length() > 0 ? ", " : "") +  + hours + (hours != 1 ? " Hours" : " Hour"));
        
        if (minutes > 0)
        	sb.append((sb.length() > 0 ? ", " : "") +  + minutes + (minutes != 1 ? " Minutes" : " Minute"));
        
        if (seconds > 0)
        	sb.append((sb.length() > 0 ? ", " : "") +  + seconds + (seconds != 1 ? " Seconds" : " Second"));
        
        return sb.toString().isEmpty() ? "< 1 Second" : sb.toString();
        
	}
	
	public static String asClock(double seconds, boolean showFractional, boolean showZeros) {
		
		int total = (int) seconds;
		int days = (int) TimeUnit.SECONDS.toDays(total);
		total -= TimeUnit.DAYS.toSeconds(days);
		
		int hours = (int) TimeUnit.SECONDS.toHours(total);
		total -= TimeUnit.HOURS.toSeconds(hours);
		
		int minutes = (int) TimeUnit.SECONDS.toMinutes(total);
		total -= TimeUnit.MINUTES.toSeconds(minutes);
		
		int secs = (int) TimeUnit.SECONDS.toSeconds(total);
		int fractional = getFractional(seconds, 2);
		
        String ap = showZeros ? "%02d" : "%d";
        String decimal = showFractional ? ":" + ap : "";
        String formatted = String.format(ap + ":" + ap + decimal, minutes, secs, fractional);
        
        if (days > 0)
        	formatted = String.format(ap + ":" + ap + ":" + ap + ":" + ap + decimal, days, hours, minutes, secs, fractional);
        
        else if (hours > 0)
        	formatted = String.format(ap + ":" + ap + ":" + ap + decimal, hours, minutes, secs, fractional);
        
        return formatted;
        
	}
	
}
