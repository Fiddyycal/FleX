package org.fukkit.command.defaults;

import org.bukkit.Bukkit;
import org.fukkit.Fukkit;
import org.fukkit.command.Command;
import org.fukkit.command.FleXCommandAdapter;
import org.fukkit.command.GlobalCommand;
import org.fukkit.command.RestrictCommand;
import org.fukkit.entity.FleXPlayer;

@GlobalCommand
@RestrictCommand(permission = "flex.command.staff", disallow = {})
@Command(name = "staff", usage = "/<command> <message>", aliases = { "s" })
public class StaffCommand extends FleXCommandAdapter {

	@Override
    public boolean perform(String[] args, String[] flags) {
		
		if (args.length < 1) {
			this.usage();
        	return false;
		}
		
		StringBuilder arguments = new StringBuilder();
		
		for (int i = 0; i < args.length; i++)
			arguments.append((arguments.length() > 0 ? " " : "") + args[i]);
		
		Bukkit.getOnlinePlayers().forEach(p -> {
			
			FleXPlayer fp = Fukkit.getPlayerExact(p);
			
			if (fp == null)
				return;
			
			if (fp.hasPermission("flex.command.staff"))
				fp.sendMessage(fp.getTheme().format("<pp>[<spc>Staff\\s<sc>" + fp.getDisplayName(this.getPlayer().getTheme()) + "<pp>]\\s<lore>" + arguments.toString()));
			
		});
		
    	return true;
        
    }

}
