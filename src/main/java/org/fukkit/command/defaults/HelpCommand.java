package org.fukkit.command.defaults;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.fukkit.Memory;
import org.fukkit.command.Command;
import org.fukkit.command.ConsoleCommand;
import org.fukkit.command.FleXCommand;
import org.fukkit.command.FleXCommandAdapter;
import org.fukkit.command.GlobalCommand;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;

import io.flex.FleX.Task;
import io.flex.commons.file.Language;
import io.flex.commons.file.Variable;
import io.flex.commons.utils.NumUtils;
import io.flex.commons.utils.StringUtils;

@GlobalCommand
@ConsoleCommand
@Command(name = "help", usage = "/<command> [#]", aliases = { "support", "?" })
public class HelpCommand extends FleXCommandAdapter {
	
	public boolean perform(String[] args, String[] flags) {
		
		if (this.getSender() instanceof ConsoleCommandSender) {
			
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
			this.usage();
			return false;
		}
		
		FleXPlayer player = this.getPlayer();
		
		if (args.length == 1) {
			if (!NumUtils.canParseAsInt(args[0])) {
				this.invalid();
				return false;
			}
		}
		
		Theme theme = player.getTheme();
		Language lang = player.getLanguage();
		
		int num = args.length == 1 ? Integer.parseInt(args[0]) : 1;
		
		// TODO WTF IS GOING ON HERE, REDOOOOOOOOOOOO
		
		String[] page = this.page(Arrays.asList("kick", "ban"), num, 5);
		
		if (page == null || page.length == 0) {
			player.sendMessage(ThemeMessage.COMMAND_HELP_NOT_FOUND.format(theme, lang, new Variable<Integer>("%page%", num)));
			return false;
		}
		
		player.sendMessage(ThemeMessage.COMMAND_HELP_PAGE_TITLE.format(theme, lang, new Variable<Integer>("%page%", num)));
		
		Arrays.stream(page).forEach(c -> {
			
			FleXCommand command = Memory.COMMAND_CACHE.get(c);
			BukkitCommand bukkitCmd = (BukkitCommand) command;
			
			if (command == null)
				return;
			
			String[] message = ThemeMessage.COMMAND_HELP_PAGE_LINE.format(theme, lang,
					
					new Variable<String>("%command%", bukkitCmd.getName()),
					new Variable<String>("%description%", bukkitCmd.getDescription()),
					new Variable<Integer>("%page%", num),
					new Variable<String>("%usage%", StringUtils.join(command.getUses(), ", ")),
					new Variable<String>("%permission%", bukkitCmd.getPermission() != null ? bukkitCmd.getPermission() : "n/a")
					
			);
			
			IntStream.range(0, message.length).forEach(i -> message[i] = message[i].replace("<command>", bukkitCmd.getName()));
			player.sendMessage(message);
			
		});
		return true;
		
	}
	
	public <T> String[] page(List<T> list, int page, int length) {
		
		page--;
		int mult = page * length;
		int size = mult + length;
		
		boolean misMatch = size <= list.size();
		length = misMatch ? length : list.size() - mult;
		
		return length > 0 ? list.subList(mult, misMatch ? size : list.size()).toArray(new String[length]) : null;
		
	}
	
}
