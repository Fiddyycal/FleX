package org.fukkit.command.defaults.bot;

import org.bukkit.command.CommandSender;
import org.fukkit.command.Command;
import org.fukkit.command.ConsoleCommand;
import org.fukkit.command.FleXCommandAdapter;
import org.fukkit.command.RestrictCommand;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;
import org.fukkit.utils.FormatUtils;

import io.flex.FleX.Task;

import io.flex.commons.cache.cell.BiCell;
import io.flex.commons.console.Console;
import io.flex.commons.file.Variable;
import io.flex.commons.utils.NumUtils;

@ConsoleCommand
@RestrictCommand(permission = "flex.command.bot.error", disallow = {})
@Command(name = "error", usage = "/<command> [#]")
public class ErrorCommand extends FleXCommandAdapter {

	@Override
	public boolean perform(CommandSender sender, String[] args, String[] flags) {
		
		if (args.length != 0 && args.length != 1) {
			this.usage(sender);
			return false;
		}
		
		BiCell<Throwable, String> log = Console.ERROR_LOG.isEmpty() ? null : Console.ERROR_LOG.get(0);
		Theme theme = sender instanceof FleXPlayer ? ((FleXPlayer)sender).getTheme() : null;
		
		String messageFormat = "<pp>[<spc>From\\s<sc>" + FormatUtils.format("&8[&fBot&8]&r &fFleX") + "<pp>]\\s<lore>";
		
		@SuppressWarnings("unused")
		Variable<?>[] variables = {
				
				new Variable<String>("%rank%", FormatUtils.format("&8[&fBot&8]")),
				new Variable<String>("%player%", FormatUtils.format("&fFleX")),
				new Variable<String>("%name%", FormatUtils.format("&fFleX")),
				new Variable<String>("%display%", FormatUtils.format("&8[&fBot&8]&r &fFleX"))
				
		};
		
		if (log == null) {
			
			if (sender instanceof FleXPlayer) {

				((FleXPlayer)sender).sendMessage(theme.format(messageFormat + "What? No errors were found..."));
				((FleXPlayer)sender).sendMessage(theme.format(messageFormat + "It seems the engine is having a good day for once."));
				
			} else Task.print("-O_O-",
					
					"FleX Bot: What? No errors were found...",
					"FleX Bot: FleX Bot: It seems the engine is having a good day for once.");
			
			return false;
			
		}
		
		if (args.length == 0) {
			
			if (sender instanceof FleXPlayer) {

				((FleXPlayer)sender).sendMessage(theme.format(messageFormat + "Printing latest stacktrace to console..."));
				((FleXPlayer)sender).sendMessage(theme.format(messageFormat + log.a().getMessage()));
			    
			    Task.print("-O_O-", "FleX Bot: " + ((FleXPlayer)sender).getName() + " printed the stacktrace below using \"/error\".");
				
			} else Task.print("-O_O-", "FleX Bot: Printing the latest stacktrace logged...");
			
			Console.print(log);
			return true;
			
		}
		
		if (!NumUtils.canParseAsInt(args[0])) {
			
			if (sender instanceof FleXPlayer) {

				((FleXPlayer)sender).sendMessage(theme.format(messageFormat + "I can only search for error logs using the log-specific index number."));
				((FleXPlayer)sender).sendMessage(theme.format(messageFormat + "Please note, index numbers cannot be higher than " + Integer.MAX_VALUE + "."));
				
			} else Task.print("-O_O-",
						
						"FleX Bot: I can only search for error logs using the log-specific index number.",
						"FleX Bot: Please note, index numbers cannot be higher than " + Integer.MAX_VALUE + ".");
			
			return false;
			
		}
		
		if (Console.ERROR_LOG.size() < Integer.parseInt(args[0])) {

			if (sender instanceof FleXPlayer) {

				((FleXPlayer)sender).sendMessage(theme.format(messageFormat + "That error log doesn't appear to exist."));
				((FleXPlayer)sender).sendMessage(theme.format(messageFormat + (Console.ERROR_LOG.isEmpty() ? "It seems the engine is having a good day for once." : "Are you sure you entered the correct number?")));
				
			} else Task.print("-O_O-",
					
					"FleX Bot: That log doesn't appear to exist.",
                    "FleX Bot: " + (Console.ERROR_LOG.isEmpty() ? "It seems the engine is having a good day for once." : "Are you sure you entered the correct number?"));
			
			return false;
			
		}
		
		log = Console.ERROR_LOG.get(Integer.parseInt(args[0])-1);
		
		if (sender instanceof FleXPlayer) {

			((FleXPlayer)sender).sendMessage(theme.format(messageFormat + "Printing logged stacktrace to console..."));
			((FleXPlayer)sender).sendMessage(theme.format(messageFormat + log.a().getMessage()));
		    
		    Task.print("-O_O-", "FleX Bot: " + ((FleXPlayer)sender).getName() + " printed the stacktrace below using \"/error " + args[0] + "\".");
			
		} else Task.print("-O_O-", "FleX Bot: Printing logged stacktrace...");
		
		Console.print(log);
		return true;
		
	}

}
