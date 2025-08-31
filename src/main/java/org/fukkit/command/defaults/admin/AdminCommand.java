package org.fukkit.command.defaults.admin;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.fukkit.Memory;
import org.fukkit.command.Command;
import org.fukkit.command.ConsoleCommand;
import org.fukkit.command.FlaggedCommand;
import org.fukkit.command.FleXCommandAdapter;
import org.fukkit.command.GlobalCommand;
import org.fukkit.command.RestrictCommand;
import org.fukkit.theme.ThemeMessage;

import io.flex.commons.cache.Cache;
import io.flex.commons.cache.LinkedCache;
import io.flex.commons.file.Language;
import io.flex.commons.file.Variable;

@GlobalCommand
@ConsoleCommand
@FlaggedCommand(flags = { "-r", "-t", "-m", "-c", "-d", "-g" })
@RestrictCommand(permission = "flex.command.admin", disallow = {})
@Command(name = "admin", usage = "/<command> <subCommand> [<args>] [-f]", aliases = { "owner", "developer", "dev" })
public class AdminCommand extends FleXCommandAdapter {

	private static final Cache<AbstractAdminSubCommand, String> sub_command_cache = new LinkedCache<AbstractAdminSubCommand, String>((subCommand, alias) -> {
		return Arrays.stream(subCommand.getAliases()).anyMatch(a -> alias.equalsIgnoreCase(a));
	});
	
	public AdminCommand() {

		sub_command_cache.add(new RankSubCommand(this));
		sub_command_cache.add(new BadgeSubCommand(this));
		sub_command_cache.add(new FreezeSubCommand(this));
		sub_command_cache.add(new ShutdownSubCommand(this));
		sub_command_cache.add(new BotSubCommand(this));
		sub_command_cache.add(new BroadcastSubCommand(this));
		sub_command_cache.add(new BlacklistAddSubCommand(this));
		sub_command_cache.add(new DebugSubCommand(this));
		
	}
	
	public boolean perform(CommandSender sender, String[] args, String[] flags) {
		
		if (args.length == 0 || sub_command_cache.get(args[0]) == null) {
			this.usage(sender,

				"/<command> setrank/giverank/rank/r <player> <rank> [reason]",
				"/<command> givebadge/badge/b <player> <badge> [reason]",
				"/<command> database/configuration <name> [-r, -c, -l]",
				"/<command> say/broadcast/bc <message> [-g]",
				"/<command> blacklist/bl <player> [reason]",
				"/<command> spawn/create/bot <name> [uuid]",
				"/<command> freeze/frozen/pause [reason]",
				"/<command> shutdown/stop [reason]"
				
			);
			return false;
		}
		
		if (sender instanceof ConsoleCommandSender && !args[0].equalsIgnoreCase("setrank") && !args[0].equalsIgnoreCase("giverank") && !args[0].equalsIgnoreCase("rank") && !args[0].equalsIgnoreCase("r")) {
			
			sender.sendMessage(ThemeMessage.COMMAND_DENIED_STATE_CONSOLE.format(Memory.THEME_CACHE.stream().findFirst().get(), Language.ENGLISH, new Variable<String>("%command%", this.getName())));
			return true;
			
		}
		
		return sub_command_cache.get(args[0]).test(sender, args, flags);
		
	}
	
}
