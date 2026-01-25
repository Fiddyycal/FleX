package org.fukkit.command.defaults;

import org.bukkit.command.CommandSender;
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
	
	public boolean perform(CommandSender sender, String[] args, String[] flags) {

		if (args.length != 0 && args.length != 1) {
			this.usage(sender);
			return false;
		}
		
		Fukkit.getPlayerAsync(args.length > 0 ? args[0] : sender.getName(), fp -> {
			
			if (fp == null) {
				this.playerNotFound(sender, args[0]);
				return;
			}
			
			((FleXPlayer)sender).openMenu(new FleXPanel(((FleXPlayer)sender), (FleXPlayer)fp, false), false);
			return;
			
		});
		
		return true;
		
	}
	
}
