package org.fukkit.command.defaults;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.fukkit.command.Command;
import org.fukkit.command.FleXCommandAdapter;
import org.fukkit.command.GlobalCommand;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.LanguageGui;
import org.fukkit.theme.ThemeMessage;

import io.flex.commons.file.Language;
import io.flex.commons.file.Variable;

@GlobalCommand
@Command(name = "language", usage = "/<command> [language]", aliases = { "lang" })
public class LanguageCommand extends FleXCommandAdapter {
	
	public boolean perform(CommandSender sender, String[] args, String[] flags) {

		if (args.length != 0 && args.length != 1) {
			this.usage(sender);
			return false;
		}
		
		if (args.length > 0) {
			
			FleXPlayer player = ((FleXPlayer)sender);
			
			Language lang;
			
			try {
				lang = Language.valueOf(args[0].toUpperCase());
			} catch (Exception e) {
				lang = null;
			}
			
			if (lang == null) {
				
				Arrays.stream(ThemeMessage.LANGUAGE_FAILURE_NOT_FOUND.format(player.getTheme(), player.getLanguage(), new Variable<String>("%language%", args[0]))).forEach(m -> {
					player.sendMessage(player.getTheme().format(m));
				});
				
				return false;
				
			}
			
			player.setLanguage(lang);
			
			Arrays.stream(ThemeMessage.LANGUAGE_SELECT_SUCCESS.format(player.getTheme(), player.getLanguage(),
					
					new Variable<String>("%language%", lang.toString()),
					new Variable<String>("%lang%", lang.name()))).forEach(m -> {
						
				player.sendMessage(player.getTheme().format(m));
				
			});
			
			return true;
			
		}
		
		((FleXPlayer)sender).openMenu(new LanguageGui(((FleXPlayer)sender)), false);
		return true;
		
	}
	
}
