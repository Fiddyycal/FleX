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
		
		FleXPlayer fp = this.command.getPlayer();
		
		Theme theme = fp.getTheme();
		String name = args[0];
		Badge badge = null;
		
		StringBuilder reason = new StringBuilder();
		
	    for (String arg : ArrayUtils.remove(args, args[0], args[1])) {
	    	
	        if (reason.length() != 0)
	        	reason.append(" ");
	        
	        reason.append(arg);
	        
	    }
		
		fp = Fukkit.getPlayer(name);
		badge = Memory.BADGE_CACHE.get(args[1]);
		
		if (fp == null) {
			this.command.playerNotFound(name);
			return false;
		}

		if (badge == null) {
			// TODO
			fp.sendMessage(theme.format("<engine><failure>That badge could not be found<pp>."));
			return false;
		}
		
		if (fp.getHistory().getBadges().badgeSet().contains(badge)) {
			// TODO
			fp.sendMessage(theme.format("<engine><sc>" + fp.getDisplayName(theme) + "<failure> already has that badge<pp>."));
			return false;
		}
		
		String reas = args.length > 2 ? reason.toString() : "No reason found";
		
		fp.getHistory().getBadges().onBadgeReceive(badge, reas);
		
		if (this.command.getPlayer() == fp || (this.command.getPlayer() != fp && fp.isOnline())) {
			fp.getPlayer().playSound(fp.getLocation(), VersionUtils.sound("LEVEL_UP", "ENTITY_PLAYER_LEVELUP"), 1F, 0.1F);
			fp.sendMessage(theme.format("<engine><success>You have been given the badge <spc>" + badge.getName() + "<pp>. (<sc>" + reas + "<pp>)"));
		}
		
		return true;
		
	}

}
