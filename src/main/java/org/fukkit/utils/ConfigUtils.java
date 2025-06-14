package org.fukkit.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import io.flex.commons.Nullable;

public class ConfigUtils {

	public static String sequence(FileConfiguration config, String path) {
		return sequence(config, path, null);
	}
	
	public static String sequence(FileConfiguration config, String path, @Nullable String def) {
		
		String sequence = config.getString(path + ".Sequence.Advanced", "");
		
		ChatColor chatColor;
		
		try {
			chatColor = ChatColor.valueOf(config.getString(path + ".Sequence.ChatColor"));
		} catch (IllegalArgumentException e) {
			chatColor = ChatColor.WHITE;
		} catch (NullPointerException e) {
			chatColor = null;
		}
		
		boolean strikeThrough = config.getBoolean(path + ".Sequence.StrikeThrough", false),
				underline = config.getBoolean(path + ".Sequence.Underline", false),
				italic = config.getBoolean(path + ".Sequence.Italic", false),
				magic = config.getBoolean(path + ".Sequence.Magic", false),
				bold = config.getBoolean(path + ".Sequence.Bold", false);
		
		sequence = (chatColor != null ? chatColor : "") + sequence + (bold ? ChatColor.BOLD : "") + (italic ? ChatColor.ITALIC : "") +
				(underline ? ChatColor.UNDERLINE : "") + (strikeThrough ? ChatColor.STRIKETHROUGH : "") + (magic ? ChatColor.MAGIC : "");
		
		return sequence.equals("") && def != null ? def : sequence;
		
	}
	
}
