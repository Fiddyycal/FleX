package org.fukkit.command.defaults;

import java.sql.SQLException;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.fukkit.Fukkit;
import org.fukkit.command.Command;
import org.fukkit.command.ConsoleCommand;
import org.fukkit.command.FlaggedCommand;
import org.fukkit.command.FleXCommandAdapter;
import org.fukkit.command.GlobalCommand;
import org.fukkit.command.RestrictCommand;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;

import io.flex.commons.utils.ArrayUtils;

@GlobalCommand
@ConsoleCommand
@FlaggedCommand(flags = { "-g" })
@RestrictCommand(permission = "flex.command.pardon", disallow = {})
@Command(name = "unmute", usage = "/<command> <player> [-g]", aliases = { "flexunmute", "untempmute", "untmute" })
public class UnMuteCommand extends FleXCommandAdapter {

	@Override
	public boolean perform(CommandSender sender, String[] args, String[] flags) {
		
		if (args.length != 1) {
			this.usage(sender);
			return false;
		}
		
		FleXPlayer fp = Fukkit.getPlayer(args[0]);
		
		if (fp == null) {
			this.playerNotFound(sender, args[0]);
			return false;
		}
		
		if (!fp.isMuted()) {
			
			if (sender instanceof ConsoleCommandSender)
				sender.sendMessage(fp.getName() + " is not muted.");
			
			else ((FleXPlayer)sender).sendMessage(((FleXPlayer)sender).getTheme().format("<engine><failure>" + fp.getDisplayName(((FleXPlayer)sender).getTheme()) + Theme.reset + " <failure>is not muted<pp>."));
				
			return false;
			
		}
		
		boolean global = ArrayUtils.contains(flags, "-g");
		
		try {
			fp.getMute().pardon();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (sender instanceof ConsoleCommandSender)
			sender.sendMessage(fp.getName() + " had their most recent mute pardoned.");
		
		if (fp.isOnline())
			fp.sendMessage(fp.getTheme().format("<engine><success>Your most recent mute has been pardoned<pp>."));
		
		Fukkit.getServerHandler().getOnlinePlayersUnsafe().stream().forEach(p -> {
			
			if (global || p.getRank().isStaff())
				p.sendMessage(p.getTheme().format("<engine><failure>" + fp.getDisplayName(((FleXPlayer)sender).getTheme()) + Theme.reset + " <failure>had their most recent mute pardoned<pp>."));
			
		});
		
		return true;
		
	}
	
}
