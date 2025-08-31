package org.fukkit.command.defaults;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.fukkit.Fukkit;
import org.fukkit.command.Command;
import org.fukkit.command.FleXCommandAdapter;
import org.fukkit.command.GlobalCommand;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.player.FleXPlayerMaskEvent;
import org.fukkit.event.player.FleXPlayerMaskEvent.Result;
import org.fukkit.reward.Rank;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;

import io.flex.commons.file.Variable;

@GlobalCommand
@Command(name = "unmask", usage = "/<command>")
public class UnMaskCommand extends FleXCommandAdapter {
	
	public boolean perform(CommandSender sender, String[] args, String[] flags) {

		if (args.length != 0) {
			this.usage(sender);
			return false;
		}
		
		FleXPlayer player = ((FleXPlayer)sender);
		Theme theme = ((FleXPlayer)sender).getTheme();
		Rank mask = player.getMask();
		
		if (mask == null) {
			
			Arrays.stream(ThemeMessage.UNMASK_FAILURE.format(theme, player.getLanguage())).forEach(m -> {
				player.sendMessage(theme.format(m));
			});
			
			return false;
			
		}
		
		FleXPlayerMaskEvent event = new FleXPlayerMaskEvent(player, mask, Result.UNMASK);
		
		Fukkit.getEventFactory().call(event);
		
		if (event.isCancelled())
			return false;
		
		player.setMask(null);
		
		Arrays.stream(ThemeMessage.UNMASK_SUCCESS.format(theme, player.getLanguage(),
				
				new Variable<String>("%mask%", mask.getName()),
				new Variable<String>("%rank%", player.getRank().getName()))).forEach(m -> {
					
			player.sendMessage(theme.format(m));
			
		});
		
		return true;
		
	}
	
}
