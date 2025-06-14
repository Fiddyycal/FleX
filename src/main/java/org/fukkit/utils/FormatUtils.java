package org.fukkit.utils;

import java.util.stream.IntStream;

import org.bukkit.ChatColor;

import io.flex.commons.utils.NumUtils;

public class FormatUtils {
	
	public static final ChatColor[] RAINBOW = {
    		
    	ChatColor.DARK_RED, ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.GREEN,
    	ChatColor.DARK_GREEN, ChatColor.AQUA, ChatColor.DARK_AQUA, ChatColor.BLUE, ChatColor.DARK_BLUE,
    	ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE
    		
    };
	
	public static ChatColor horseshoeOpposite(ChatColor c) {
		
		if (c.isFormat())
			throw new IllegalArgumentException("Cannot find the horseshoe-opposite of a format ChatColor.");
		
		switch (c) {
		
		case AQUA:
			return ChatColor.DARK_AQUA;
		case BLACK:
			return ChatColor.DARK_GRAY;
		case BLUE:
			return ChatColor.DARK_BLUE;
		case DARK_AQUA:
			return ChatColor.AQUA;
		case DARK_BLUE:
			return ChatColor.BLUE;
		case DARK_GRAY:
			return ChatColor.GRAY;
		case DARK_GREEN:
			return ChatColor.GREEN;
		case DARK_PURPLE:
			return ChatColor.LIGHT_PURPLE;
		case DARK_RED:
			return ChatColor.RED;
		case GOLD:
			return ChatColor.YELLOW;
		case GRAY:
			return ChatColor.DARK_GRAY;
		case GREEN:
			return ChatColor.DARK_GREEN;
		case LIGHT_PURPLE:
			return ChatColor.DARK_PURPLE;
		case RED:
			return ChatColor.DARK_RED;
		case WHITE:
			return ChatColor.GRAY;
		case YELLOW:
			return ChatColor.GOLD;
		default:
			return ChatColor.RESET;
		}
		
	}
	
	public static String getRainbowString(String s, boolean bold, boolean seemless) {
		
		StringBuilder sb = new StringBuilder();
		
		char[] chars = s.toCharArray();
		
    	int i = 0;
    	
    	boolean flip = false;
        
        for (int j = 0; j < chars.length; j++) {
        	
			sb.append(RAINBOW[i] + (bold ? ChatColor.BOLD.toString() : "") + chars[j]);
			
			if (seemless) {
				
				if (flip)
		    		i--;
				
				else i++;
				
				if (i == 0)
					flip = false;
				
				if (i == RAINBOW.length - 1)
					flip = true;
				
			} else {
				
				if (j != 0 && j % (RAINBOW.length - 1) == 0)
	        		i = 0;
				
				else i++;
				
			}
			
		}
        
        return sb.toString();
        
	}
	
	public static String[] getShimmerArray(String s, String shinePrefix, String highlightPrefix, boolean bold) {
		
		String[] shimmer = new String[s.length() + 3];
		String thicc = (bold ? "" + ChatColor.BOLD : "");
		
		shimmer[0] = FormatUtils.format(shinePrefix + thicc + ChatColor.DARK_GRAY + thicc + s);
		shimmer[1] = FormatUtils.format(shinePrefix + thicc + highlightPrefix + thicc + s.substring(0, 1) + ChatColor.DARK_GRAY + thicc + s.substring(1));
		shimmer[shimmer.length-1] = FormatUtils.format(shinePrefix + thicc + s);
		
		IntStream.range(0, shimmer.length-3).forEach(i -> {
			
			String shine = shinePrefix + thicc + s.substring(0, i);
			String glint = ChatColor.WHITE + thicc + s.substring(i, i + 1);
			String highlight = i + 1 < s.length() ? highlightPrefix + thicc + s.substring(i + 1, i + 2) : "";
			String gray = i + 2 < s.length() ? ChatColor.DARK_GRAY + thicc + s.substring(i + 2) : "";
			
			shimmer[i + 2] = FormatUtils.format(shine + glint + highlight + gray);
			
		});
		
		return shimmer;
    
	}
	
	public static boolean isBold(String s) {
		return s.contains(ChatColor.BOLD.toString());
	}
	
	public static boolean isItalic(String s) {
		return s.contains(ChatColor.ITALIC.toString());
	}
	
	public static boolean isUnderline(String s) {
		return s.contains(ChatColor.UNDERLINE.toString());
	}
	
	public static boolean isStrikeThrough(String s) {
		return s.contains(ChatColor.STRIKETHROUGH.toString());
	}
	
	public static boolean isMagic(String s) {
		return s.contains(ChatColor.MAGIC.toString());
	}
	
	public static String format(String s) {
		
		if (s == null)
			return null;
		
		s = s.replace("&g", ChatColor.COLOR_CHAR + "g");
		s = s.replace("&h", ChatColor.COLOR_CHAR + "h");
		s = s.replace("&l", ChatColor.COLOR_CHAR + "l");
		
		while (s.contains(ChatColor.COLOR_CHAR + "h"))
			s = s.replaceFirst(ChatColor.COLOR_CHAR + "h", RAINBOW[NumUtils.getRng().getInt(0, RAINBOW.length - 1)].toString());
		
		return ChatColor.translateAlternateColorCodes('&', rainbow(s));
		
	}
	
	public static String rainbow(String check) {
	
		String checking = ChatColor.COLOR_CHAR + "g";
		
		if (!check.contains(checking))
			return check;
		
		StringBuilder builder = new StringBuilder();
		
		String[] sp = check.split(checking);
		
		for (int i = 0; i < sp.length; i++) {
			
			String[] spl = sp[i].split(Character.toString(ChatColor.COLOR_CHAR));
			
			for (int j = 0; j < spl.length; j++) {
				String append = (j != 0 ? ChatColor.COLOR_CHAR : "") + spl[j];
				builder.append(j == 0 && i != 0 ? FormatUtils.getRainbowString(append, check.contains(checking + ChatColor.COLOR_CHAR + "l"), true) : append);
			}
			
		}
		
		return builder.toString();
		
	}
	
}
