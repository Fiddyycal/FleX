package org.fukkit.command.defaults.admin;

import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.reward.Badge;
import org.fukkit.theme.Theme;
import org.fukkit.utils.VersionUtils;

import io.flex.commons.utils.ArrayUtils;

public class BadgeSubCommand extends AbstractAdminSubCommand {
	
	public BadgeSubCommand(AdminCommand command) {
		super(command, "givebadge", "badge", "b");
	}

	@Override
	public boolean perform(String[] args, String[] flags) {
		
		if (args.length < 2) {
			this.command.usage("/<command> givebadge/badge/b <player> <badge> [reason]");
			return false;
		}
		
		FleXPlayer player = this.command.getPlayer();
		
		Theme theme = player.getTheme();
		String name = args[0];
		
		StringBuilder reason = new StringBuilder();
		
	    for (String arg : ArrayUtils.remove(args, args[0], args[1])) {
	    	
	        if (reason.length() != 0)
	        	reason.append(" ");
	        
	        reason.append(arg);
	        
	    }
		
		FleXPlayer fp = Fukkit.getPlayer(name);
		Badge badge = Memory.BADGE_CACHE.get(args[1]);
		
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
		this.command.getPlayer().sendMessage(theme.format("<engine><sc>Adding badge to<reset> <spc>" + fp.getDisplayName(theme) + "<pp>..."));
		
		fp.getHistoryAsync(history -> {
			
			if (!player.isOnline())
				return;
			
			if (history.getBadges().badgeSet().contains(badge)) {
				// TODO
				player.sendMessage(theme.format("<engine><sc>" + fp.getDisplayName(theme) + "<failure> already has that badge<pp>."));
				return;
				
			}
			
			String reas = args.length > 2 ? reason.toString() : "No reason found";
			
			history.getBadges().onBadgeReceive(badge, reas);
			
			if (this.command.getPlayer() == fp || (player != fp && fp.isOnline())) {
				
				fp.getPlayer().playSound(fp.getLocation(), VersionUtils.sound("LEVEL_UP", "ENTITY_PLAYER_LEVELUP"), 1F, 0.1F);
				fp.sendMessage(theme.format("<engine><success>You have been given the badge <spc>" + badge.getName() + "<pp>. (<sc>" + reas + "<pp>)"));
				
			}
			
		}, () -> {
			
			if (!player.isOnline())
				return;
			
			player.sendMessage(theme.format("<engine><sc>" + fp.getDisplayName(theme) + "<failure>'s badge history failed to load, please try again later<pp>..."));
			
		});
		
		return true;
		
	}

}
