package org.fukkit.command.defaults.admin;

import java.util.HashMap;
import java.util.Map;

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

import io.flex.commons.file.Language;
import io.flex.commons.file.Variable;

@GlobalCommand
@ConsoleCommand
@FlaggedCommand(flags = { "-r", "-t", "-m", "-c", "-d", "-g" })
@RestrictCommand(permission = "flex.command.admin", disallow = {})
@Command(name = "admin", usage = "/<command> <subCommand> [<args>] [-f]")
public class AdminCommand extends FleXCommandAdapter {

	private static final Map<String, AbstractAdminSubCommand> sub_commands = new HashMap<String, AbstractAdminSubCommand>();
	
	public AdminCommand() {
		
		sub_commands.put("badge", new BadgeSubCommand(this));
		sub_commands.put("rank", new RankSubCommand(this));
		sub_commands.put("debug", new DebugSubCommand(this));
		sub_commands.put("broadcast", new BroadcastSubCommand(this));
		sub_commands.put("bot", new BotSubCommand(this));
		sub_commands.put("replay", new ReplaySubCommand(this));
		sub_commands.put("freeze", new FreezeSubCommand(this));
		sub_commands.put("shutdown", new ShutdownSubCommand(this));
		
	}
	
	public boolean perform(CommandSender sender, String[] args, String[] flags) {
		
		if (args.length == 0 || sub_commands.get(args[0].toLowerCase()) == null) {
			this.usage(sender,
				
				"/<command> broadcast/bc/say <message>", // TODO [-g]
				"/<command> bot create/remove <name> [uuid]",
				"/<command> badge add/remove <player> <badge> [reason]",
				"/<command> rank <player> <rank> [reason]",
				"/<command> database <name> [-r, -c, -l]",
				"/<command> replay <uid|list>",
				"/<command> freeze [reason]",
				"/<command> shutdown [reason]"
				
			);
			return false;
		}
		
		if (sender instanceof ConsoleCommandSender && !args[0].equalsIgnoreCase("setrank") && !args[0].equalsIgnoreCase("giverank") && !args[0].equalsIgnoreCase("rank") && !args[0].equalsIgnoreCase("r")) {
			
			sender.sendMessage(ThemeMessage.COMMAND_DENIED_STATE_CONSOLE.format(Memory.THEME_CACHE.stream().findFirst().get(), Language.ENGLISH, new Variable<String>("%command%", this.getName())));
			return true;
			
		}
		
		if (args[0].toLowerCase().equals("say") || args[0].toLowerCase().equals("bc"))
			args[0] = "broadcast";
		
		return sub_commands.get(args[0].toLowerCase()).test(sender, args, flags);
		
	}
	
}
