package org.fukkit.command.defaults;

import org.fukkit.clickable.Menu;
import org.fukkit.command.Command;
import org.fukkit.command.GlobalCommand;
import org.fukkit.consequence.ConvictionType;
import org.fukkit.consequence.gui.SanctionGui;
import org.fukkit.entity.FleXPlayer;

@GlobalCommand
@Command(name = "report", usage = "/<command> <player>", aliases = { "flexreport", "hacking", "hacker", "cheating", "cheater" })
public class ReportCommand extends AbstractSanctionCommand {
	
	@Override
	protected Menu getMenu(FleXPlayer other, boolean ip, boolean silent) {
		return new SanctionGui(this.getPlayer(), other, ConvictionType.REPORT, false, false);
	}
	
}