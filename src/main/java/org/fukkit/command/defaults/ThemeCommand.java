package org.fukkit.command.defaults;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.fukkit.Memory;
import org.fukkit.command.Command;
import org.fukkit.command.FleXCommandAdapter;
import org.fukkit.command.GlobalCommand;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeGui;
import org.fukkit.theme.ThemeMessage;

import io.flex.commons.file.Variable;

@GlobalCommand
@Command(name = "theme", usage = "/<command> [theme]")
public class ThemeCommand extends FleXCommandAdapter {
	
	public boolean perform(CommandSender sender, String[] args, String[] flags) {

		if (args.length != 0 && args.length != 1) {
			this.usage(sender);
			return false;
		}
		
		if (args.length > 0) {
			
			FleXPlayer player = ((FleXPlayer)sender);
			
			Theme theme = Memory.THEME_CACHE.get(args[0]);
			
			if (theme == null) {
				
				Arrays.stream(ThemeMessage.THEME_FAILURE_NOT_FOUND.format(player.getTheme(), player.getLanguage(), new Variable<String>("%theme%", args[0]))).forEach(m -> {
					player.sendMessage(player.getTheme().format(m));
				});
				
				return false;
				
			}
			
			player.setTheme(theme);
			
			Arrays.stream(ThemeMessage.THEME_SELECT_SUCCESS.format(theme, player.getLanguage(), new Variable<String>("%theme%", theme.getName()))).forEach(m -> {
				player.sendMessage(theme.format(m));
			});
			
			return true;
			
		}
		
		((FleXPlayer)sender).openMenu(new ThemeGui(((FleXPlayer)sender)), false);
		return true;
		
	}
	
}
