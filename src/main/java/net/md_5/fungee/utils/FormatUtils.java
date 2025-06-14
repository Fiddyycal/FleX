package net.md_5.fungee.utils;

import net.md_5.bungee.api.ChatColor;

public class FormatUtils {
	
	public static String format(String s) {
		return s != null ? ChatColor.translateAlternateColorCodes('&', s.replace("&g", ChatColor.COLOR_CHAR + "g")) : null;
	}

}
