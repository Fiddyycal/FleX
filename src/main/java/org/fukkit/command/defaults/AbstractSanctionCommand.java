package org.fukkit.command.defaults;

import org.fukkit.Fukkit;
import org.fukkit.clickable.Menu;
import org.fukkit.command.FleXCommandAdapter;
import org.fukkit.command.GlobalCommand;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.ThemeMessage;

import io.flex.commons.file.Variable;
import io.flex.commons.utils.ArrayUtils;
import io.flex.commons.utils.StringUtils;

@GlobalCommand
public abstract class AbstractSanctionCommand extends FleXCommandAdapter {

	@Override
	public boolean perform(String[] args, String[] flags) {
		
		boolean report = StringUtils.equalsIgnoreCaseAny(this.command, "report", "flexreport", "hacking", "hacker", "cheating", "cheater");
		
		if (!this.getPlayer().hasPermission("flex.command.punish") && !report) {
			this.noPermission();
			return false;
		}
		
		if (args.length != 1) {
			this.usage();
			return false;
		}
		
		FleXPlayer fp = Fukkit.getPlayer(args[0]);
		
		if (fp == null) {
			this.playerNotFound(args[0]);
			return false;
		}
		
		if ((StringUtils.equalsIgnoreCaseAny(this.command, "kick", "flexkick") || report) && !fp.isOnline()) {
			this.playerNotOnline(fp);
			return false;
		}
		
		if (this.getPlayer() == fp) {
			this.getPlayer().sendMessage((report ? ThemeMessage.REPORT_FAILURE_SELF : ThemeMessage.PUNISHMENT_FAILURE_SELF).format(this.getPlayer().getTheme(), this.getPlayer().getLanguage(), new Variable<String>("%punishment%", this.command)));
			return false;
		}
		
		if (this.getPlayer().getRank().isStaff() && this.getPlayer().getRank().getWeight() < fp.getRank().getWeight()) {
			
			if (report) {
				this.getPlayer().sendMessage(ThemeMessage.REPORT_FAILURE_DENIED.format(this.getPlayer().getTheme(), this.getPlayer().getLanguage()));
				return false;
			}
			
			this.getPlayer().sendMessage(ThemeMessage.PUNISHMENT_FAILURE_DENIED.format(this.getPlayer().getTheme(), this.getPlayer().getLanguage(), new Variable<String>("%punishment%", this.command)));
			return false;
			
		}
		
		boolean ip = ArrayUtils.contains(flags, "-i");
		boolean silent = ArrayUtils.contains(flags, "-s");
		
		Menu sanction = this.getMenu(fp, ip, silent);
		
		this.getPlayer().openMenu(sanction, false);
		return true;
		
	}
	
	protected abstract Menu getMenu(FleXPlayer other, boolean ip, boolean silent);
	
}
