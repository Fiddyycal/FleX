package org.fukkit.scoreboard.value;

import org.bukkit.ChatColor;

import io.flex.commons.utils.NumUtils;

public class FlashValue {

	private static ChatColor[] colors = { ChatColor.GOLD, ChatColor.GRAY };
	
	public String get() {
		return colors[NumUtils.getRng().getInt(0, colors.length)] + ChatColor.BOLD.toString();
	}

}
