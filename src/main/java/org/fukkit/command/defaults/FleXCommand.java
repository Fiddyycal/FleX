package org.fukkit.command.defaults;

import org.fukkit.Fukkit;
import org.fukkit.command.Command;
import org.fukkit.command.FleXCommandAdapter;
import org.fukkit.command.GlobalCommand;
import org.fukkit.command.RestrictCommand;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.panel.FleXPanel;

@GlobalCommand
@RestrictCommand(permission = "flex.command.flex", disallow = {})
@Command(name = "flex", usage = "/<command> <player>", aliases = { "fl", "flexpanel", "panel", "fpanel", "history", "fhistory", "flowlineenforcement", "flowlineenforcementpanel", "fle", "flepanel" })
public class FleXCommand extends FleXCommandAdapter {
	
	public boolean perform(String[] args, String[] flags) {

		if (args.length != 0 && args.length != 1) {
			this.usage();
			return false;
		}
		
		FleXPlayer fp = args.length > 0 ? Fukkit.getPlayer(args[0]) : this.getPlayer();
		
		if (fp == null) {
			this.playerNotFound(args[0]);
			return false;
		}
		
		this.getPlayer().openMenu(new FleXPanel(this.getPlayer(), fp, false), false);
		return true;
		
	}
	
}
