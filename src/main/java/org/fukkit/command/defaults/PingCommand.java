package org.fukkit.command.defaults;

import org.fukkit.api.helper.PlayerHelper;
import org.fukkit.command.Command;
import org.fukkit.command.FleXCommandAdapter;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;
import org.fukkit.utils.FormatUtils;

import io.flex.commons.file.Language;
import io.flex.commons.file.Variable;

@Command(name = "ping", usage = "/<command> [player]", aliases = { "latency", "connection", "ms" })
public class PingCommand extends FleXCommandAdapter {

	@SuppressWarnings("deprecation")
	public boolean perform(String[] args, String[] flags) {
		
		if (args.length != 0 && args.length != 1) {
			this.usage();
			return false;
		}
		
		FleXPlayer fp = args.length == 1 ? PlayerHelper.getPlayer(args[0]) : this.getPlayer();
		int ping = fp != null ? fp.getPing() : -1;
		
		Theme theme = this.getPlayer().getTheme();
		Language lang = this.getPlayer().getLanguage();
		
		if (fp == null) {
			this.playerNotFound(args[0]);
			return false;
		}
		
		if (!fp.isOnline()) {
			this.playerNotOnline(fp);
			return false;
		}
		
		if (fp.getServer() != this.getPlayer().getServer()) {
			this.playerNotAccessible(fp);
    		return false;
		}

		Variable<?>[] variables = {
				
				new Variable<Integer>("%ping%", ping),
				new Variable<String>("%name%", fp.getDisplayName()),
				new Variable<String>("%player%", fp.getName()),
				new Variable<String>("%display%", fp.getDisplayName(theme)),
				new Variable<String>("%connection%", FormatUtils.format(this.getConnection(ping, true)))
				
		};
		
		this.getPlayer().sendMessage(this.getPlayer() != fp ? ThemeMessage.PING_SHOW_OTHER.format(theme, lang, variables) : ThemeMessage.PING_SHOW.format(theme, lang, variables));
		this.getPlayer().sendMessage(this.getPlayer() != fp ? ThemeMessage.PING_CONNECTION_OTHER.format(theme, lang, variables) : ThemeMessage.PING_CONNECTION.format(theme, lang, variables));
		return true;
		
	}
	
	private String getConnection(int ping, boolean spectral) {
		
		String[] bar = {

			spectral ? "&8&l||||||||||||||||||||" : "&8&l||||||||||||||||||||",
			spectral ? "&4&l|&8&l|||||||||||||||||||" : "&4&l|&8&l|||||||||||||||||||",
			spectral ? "&4&l|&c&l|&8&l||||||||||||||||||" : "&c&l||&8&l||||||||||||||||||",
			spectral ? "&4&l|&c&l|&6&l|&8&l|||||||||||||||||" : "&6&l|||&8&l|||||||||||||||||",
			spectral ? "&4&l|&c&l|&6&l|&e&l|&8&l||||||||||||||||" : "&e&l||||&8&l||||||||||||||||",
			spectral ? "&4&l|&c&l|&6&l|&e&l||&8&l|||||||||||||||" : "&e&l|||||&8&l|||||||||||||||",
			spectral ? "&4&l|&c&l|&6&l|&e&l||&a&l|&8&l||||||||||||||" : "&a&l|||||&2&l|&8&l||||||||||||||",
			spectral ? "&4&l|&c&l|&6&l|&e&l||&a&l|&2&l|&8&l|||||||||||||" : "&a&l||||||&2&l|&8&l|||||||||||||",
		    spectral ? "&4&l|&c&l|&6&l|&e&l||&a&l||&2&l|&8&l||||||||||||" : "&a&l|||||||&2&l|&8&l||||||||||||",
		    spectral ? "&4&l|&c&l|&6&l|&e&l||&a&l|||&2&l|&8&l|||||||||||" : "&a&l||||||||&2&l|&8&l|||||||||||",
		    spectral ? "&4&l|&c&l|&6&l|&e&l||&a&l||||&2&l|&8&l||||||||||" : "&a&l|||||||||&2&l|&8&l||||||||||",
		    spectral ? "&4&l|&c&l|&6&l|&e&l||&a&l|||||&2&l|&8&l|||||||||" : "&a&l||||||||||&2&l|&8&l|||||||||",
		    spectral ? "&4&l|&c&l|&6&l|&e&l||&a&l||||||&2&l|&8&l||||||||" : "&a&l|||||||||||&2&l|&8&l||||||||",
		    spectral ? "&4&l|&c&l|&6&l|&e&l||&a&l|||||||&2&l|&8&l|||||||" : "&a&l||||||||||||&2&l|&8&l|||||||",
		    spectral ? "&4&l|&c&l|&6&l|&e&l||&a&l||||||||&2&l|&8&l||||||" : "&a&l|||||||||||||&2&l|&8&l||||||",
		    spectral ? "&4&l|&c&l|&6&l|&e&l||&a&l|||||||||&2&l|&8&l|||||" : "&a&l||||||||||||||&2&l|&8&l|||||",
		    spectral ? "&4&l|&c&l|&6&l|&e&l||&a&l||||||||||&2&l|&8&l||||" : "&a&l|||||||||||||||&2&l|&8&l||||",
		    spectral ? "&4&l|&c&l|&6&l|&e&l||&a&l|||||||||||&2&l|&8&l|||" : "&a&l||||||||||||||||&2&l|&8&l|||",
			spectral ? "&4&l|&c&l|&6&l|&e&l||&a&l||||||||||||&2&l|&8&l||" : "&a&l|||||||||||||||||&2&l|&8&l||",
			spectral ? "&4&l|&c&l|&6&l|&e&l||&a&l|||||||||||||&2&l|&8&l|" : "&a&l||||||||||||||||||&2&l|&8&l|",
			spectral ? "&4&l|&c&l|&6&l|&e&l||&a&l||||||||||||||&2&l|" : "&a&l|||||||||||||||||||&2&l|"
			
		};
		
		if (ping < 1)
			return "&b&lHost&8&l/&4&lError&r " + bar[0];
		
		if (ping <= 5)
			return "&2&n&lExceptional&r " + bar[20];
		
		else if (ping <= 10)
			return "&2&lExcellent&r " + bar[19];

		else if (ping <= 20)
			return "&2&lGreat&r " + bar[18];

		else if (ping <= 30)
			return "&2&lVery Good&r " + bar[16];

		else if (ping <= 40)
			return "&a&lGood&r " + bar[15];

		else if (ping <= 50) 
			return "&a&lOptimal&r " + bar[14];

		else if (ping <= 60)
			return "&e&lAdequate&r " + bar[12];

		else if (ping <= 70)
			return "&e&lFair&r " + bar[11];

		else if (ping <= 80)
			return "&e&lSubstantial&r " + bar[10];

		else if (ping <= 90)
			return "&6&lPoor&r " + bar[8];

		else if (ping <= 100)
			return "&6&lVery Poor&r " + bar[7];

		else if (ping <= 200)
			return "&c&lBad&r " + bar[6];

		else if (ping <= 400)
			return "&c&lVery Bad&r " + bar[4];

		else if (ping <= 600)
			return "&c&lUnplayable&r " + bar[3];

		else if (ping <= 800)
			return "&4&lBroken&r " + bar[2];

		else if (ping <= 1000)
			return "&4&lHello?&r " + bar[1];

		return "&4&lTimed out&r " + bar[0];
		
	}
	
}
