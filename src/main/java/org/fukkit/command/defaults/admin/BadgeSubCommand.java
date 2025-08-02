package org.fukkit.command.defaults.admin;

import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.reward.Badge;
import org.fukkit.theme.Theme;

import io.flex.commons.utils.ArrayUtils;

public class BadgeSubCommand extends AbstractAdminSubCommand {
	
	public BadgeSubCommand(AdminCommand command) {
		super(command, "badge", "b");
	}

	@Override
	public boolean perform(String[] args, String[] flags) {
		
		if (args.length < 3) {
			this.command.usage("/<command> badge/b add/remove <player> <badge> [reason]");
			return false;
		}
		
		boolean add = args[0].equalsIgnoreCase("add");
		
		if (!add && !args[0].equalsIgnoreCase("remove")) {
			this.command.usage("/<command> badge/b add/remove <player> <badge> [reason]");
			return false;
		}
		
		FleXPlayer player = this.command.getPlayer();
		
		Theme theme = player.getTheme();
		String name = args[1];
		
		StringBuilder reason = new StringBuilder();
		
	    for (String arg : ArrayUtils.remove(args, args[0], args[1], args[2])) {
	    	
	        if (reason.length() != 0)
	        	reason.append(" ");
	        
	        reason.append(arg);
	        
	    }
		
		FleXPlayer fp = Fukkit.getPlayer(name);
		Badge badge = Memory.BADGE_CACHE.get(args[2]);
		
		if (fp == null) {
			this.command.playerNotFound(name);
			return false;
		}

		if (badge == null) {
			// TODO
			this.command.getPlayer().sendMessage(theme.format("<engine><failure>That badge could not be found<pp>."));
			return false;
		}
		
		// TODO
		this.command.getPlayer().sendMessage(theme.format("<engine><sc>" + (add ? "Adding badge to" : "Removing badge from") + " <reset> <spc>" + fp.getDisplayName(theme) + "<pp>..."));
		
		fp.getHistoryAsync(history -> {
			
			if (!player.isOnline())
				return;

			boolean exists = history.getBadges().badgeSet().contains(badge);
			
			String reas = reason.length() > 0 ? reason.toString() : "No reason found";
			
			if (add) {
				
				if (exists) {
					// TODO
					player.sendMessage(theme.format("<engine><sc>" + fp.getDisplayName(theme) + "<failure> already has that badge<pp>."));
					return;
				
				}
			
				history.getBadges().onBadgeReceive(badge, reas);
				
			} else {

				if (!exists) {
					// TODO
					player.sendMessage(theme.format("<engine><sc>" + fp.getDisplayName(theme) + "<failure> does not have that badge<pp>."));
					return;
				
				}
			
				history.getBadges().onBadgeRemove(badge, reas);
				
			}
			
		}, () -> {
			
			if (!player.isOnline())
				return;
			
			player.sendMessage(theme.format("<engine><sc>" + fp.getDisplayName(theme) + "<failure>'s badge history failed to load, please try again later<pp>..."));
			
		});
		
		return true;
		
	}

}
