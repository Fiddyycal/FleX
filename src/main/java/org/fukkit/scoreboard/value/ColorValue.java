package org.fukkit.scoreboard.value;

import org.bukkit.ChatColor;

import io.flex.commons.utils.ArrayUtils;
import io.flex.commons.utils.NumUtils;

public class ColorValue {

	private static ChatColor[] colors = ArrayUtils.remove(ChatColor.values(), ChatColor.RESET, ChatColor.WHITE, ChatColor.MAGIC, ChatColor.BOLD, ChatColor.STRIKETHROUGH, ChatColor.UNDERLINE, ChatColor.ITALIC);
	
	public String get() {
		return colors[NumUtils.getRng().getInt(0, colors.length-1)] + "color" + ChatColor.RED + "!";
	}

}
