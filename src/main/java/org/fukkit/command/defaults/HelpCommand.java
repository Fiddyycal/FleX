package org.fukkit.command.defaults;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.fukkit.Memory;
import org.fukkit.command.Command;
import org.fukkit.command.ConsoleCommand;
import org.fukkit.command.FleXCommandAdapter;
import org.fukkit.command.GlobalCommand;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;

import io.flex.FleX.Task;
import io.flex.commons.file.Language;
import io.flex.commons.file.Variable;
import io.flex.commons.utils.NumUtils;

@GlobalCommand
@ConsoleCommand
@Command(name = "help", usage = "/<command> [#]", aliases = { "support", "?" })
public class HelpCommand extends FleXCommandAdapter {
	
	public boolean perform(CommandSender sender, String[] args, String[] flags) {
		
		if (sender instanceof ConsoleCommandSender) {
			
			Task.print("-O_O-",
					
					"FleX Bot: I would love to help! Let's go over all the gestures.",
					"FleX Bot: I'll group them based on similarities, explanations will be specified.",
					"",
					"[Error Genstures]",
					"FleX Bot: I can retrieve an error log if you use \"error\".",
					"FleX Bot: Optionally you can list any error log by entering the errors index number.",
					"",
					"[Reboot Genstures]",
					"FleX Bot: I can reload the server if you use \"reload\".",
					"FleX Bot: However, reloading the server by force can be damaging.",
					"FleX Bot: I recommend you restart the server instead. You can do this using \"restart\".",
					"FleX Bot: I can also shutdown the server if you use \"stop\".",
					"",
					"FleX Bot: If you need anything else just ask, I may be able to help!");
			
			return true;
			
		}
		
		if (args.length != 0 && args.length != 1) {
			this.usage(sender);
			return false;
		}
		
		FleXPlayer player = ((FleXPlayer)sender);
		
		if (args.length == 1) {
			if (!NumUtils.canParseAsInt(args[0])) {
				this.invalid(sender);
				return false;
			}
		}
		
		Theme theme = player.getTheme();
		Language lang = player.getLanguage();
		
		int num = args.length == 1 ? Integer.parseInt(args[0]) : 1;
		
		List<FleXCommandAdapter> commands = Memory.COMMAND_CACHE.stream()
				
				.filter(c -> c.canUse(sender))
				.sorted(Comparator.comparing(FleXCommandAdapter::getName))
				.collect(Collectors.toList());
		
		List<FleXCommandAdapter> page = this.page(commands, num, 6);
		
		if (page == null || page.size() == 0) {
			player.sendMessage(ThemeMessage.COMMAND_HELP_NOT_FOUND.format(theme, lang, new Variable<Integer>("%page%", num)));
			return false;
		}
		
		player.sendMessage(ThemeMessage.COMMAND_HELP_PAGE_TITLE.format(theme, lang, new Variable<Integer>("%page%", num)));
		
		page.forEach(command -> {

			if (command == null)
				return;
			
			String usage = command.getUses().length > 1 ? "/" + command.getName().toLowerCase() + " <subCommand> [<args>] [-f]" : command.getUsage();
			BukkitCommand bukkitCmd = (BukkitCommand) command;
			
			String[] message = ThemeMessage.COMMAND_HELP_PAGE_LINE.format(theme, lang,
					
					new Variable<String>("%command%", bukkitCmd.getName()),
					new Variable<String>("%description%", bukkitCmd.getDescription()),
					new Variable<Integer>("%page%", num),
					new Variable<String>("%usage%", usage),
					new Variable<String>("%permission%", bukkitCmd.getPermission() != null ? bukkitCmd.getPermission() : "n/a")
					
			);
			
			IntStream.range(0, message.length).forEach(i -> message[i] = message[i].replace("<command>", bukkitCmd.getName()));
			player.sendMessage(message);
			
		});
		
		return true;
		
	}
	
	public List<FleXCommandAdapter> page(List<FleXCommandAdapter> list, int page, int length) {
		
	    page--;
	    
	    int start = page * length;
	    int end = Math.min(start + length, list.size());
	    
	    if (start >= list.size() || start < 0)
	        return null;
	    
	    return list.subList(start, end);
	    
	}
	
}
