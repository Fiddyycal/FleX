package org.fukkit.command.defaults;

import org.bukkit.command.CommandSender;
import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.clickable.Menu;
import org.fukkit.command.FleXCommandAdapter;
import org.fukkit.command.GlobalCommand;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.reward.Rank;
import org.fukkit.theme.ThemeMessage;

import io.flex.commons.file.Variable;
import io.flex.commons.utils.ArrayUtils;
import io.flex.commons.utils.StringUtils;

@GlobalCommand
public abstract class AbstractSanctionCommand extends FleXCommandAdapter {

	@Override
	public boolean perform(CommandSender sender, String[] args, String[] flags) {
		
		boolean report = StringUtils.equalsIgnoreCaseAny(this.command, "report", "flexreport", "hacking", "hacker", "cheating", "cheater");
		
		if (!((FleXPlayer)sender).hasPermission("flex.command.punish") && !report) {
			this.noPermission(sender);
			return false;
		}
		
		if (args.length != 1) {
			this.usage(sender);
			return false;
		}
		
		FleXPlayer fp = Fukkit.getPlayer(args[0]);
		
		if (fp == null) {
			this.playerNotFound(sender, args[0]);
			return false;
		}
		
		if ((StringUtils.equalsIgnoreCaseAny(this.command, "kick", "flexkick") || report) && !fp.isOnline()) {
			this.playerNotOnline(sender, fp);
			return false;
		}
		
		Rank dev = Memory.RANK_CACHE.get("Developer");
		
		boolean developer = dev != null && ((FleXPlayer)sender).getRank() == dev;
		
		if (!developer) {
			
			if (((FleXPlayer)sender) == fp) {
				((FleXPlayer)sender).sendMessage((report ? ThemeMessage.REPORT_FAILURE_SELF : ThemeMessage.PUNISHMENT_FAILURE_SELF).format(((FleXPlayer)sender).getTheme(), ((FleXPlayer)sender).getLanguage(), new Variable<String>("%punishment%", this.command)));
				return false;
			}
			
			if (((FleXPlayer)sender).getRank().isStaff() && ((FleXPlayer)sender).getRank().getWeight() < fp.getRank().getWeight()) {
				
				if (report) {
					((FleXPlayer)sender).sendMessage(ThemeMessage.REPORT_FAILURE_DENIED.format(((FleXPlayer)sender).getTheme(), ((FleXPlayer)sender).getLanguage()));
					return false;
				}
				
				((FleXPlayer)sender).sendMessage(ThemeMessage.PUNISHMENT_FAILURE_DENIED.format(((FleXPlayer)sender).getTheme(), ((FleXPlayer)sender).getLanguage(), new Variable<String>("%punishment%", this.command)));
				return false;
				
			}
			
		}
		
		boolean ip = ArrayUtils.contains(flags, "-i");
		boolean silent = ArrayUtils.contains(flags, "-s");
		
		Menu sanction = this.getMenu((FleXPlayer)sender, fp, ip, silent);
		
		((FleXPlayer)sender).openMenu(sanction, false);
		return true;
		
	}
	
	protected abstract Menu getMenu(FleXPlayer player, FleXPlayer other, boolean ip, boolean silent);
	
}
