package org.fukkit.command.defaults;

import java.util.Arrays;

import org.fukkit.Fukkit;
import org.fukkit.Memory;
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
@Command(name = "mask", usage = "/<command> [rank]")
public class MaskCommand extends FleXCommandAdapter {
	
	public boolean perform(String[] args, String[] flags) {

		if (args.length != 0 && args.length != 1) {
			this.usage();
			return false;
		}

		FleXPlayer player = this.getPlayer();
		Theme theme = this.getPlayer().getTheme();
		Rank rank = player.getRank();
		
		if (args.length > 0) {
			
			Rank mask = Memory.RANK_CACHE.get(args[0]);
			
			if (mask == null) {
				
				FleXPlayerMaskEvent event = new FleXPlayerMaskEvent(player, mask, Result.FAILURE);
				
				Fukkit.getEventFactory().call(event);
				
				if (event.isCancelled())
					return false;

				Arrays.stream(ThemeMessage.MASK_FAILURE_NOT_FOUND.format(theme, player.getLanguage(), new Variable<String>("%mask%", args[0]))).forEach(m -> {
					player.sendMessage(theme.format(m));
				});
				
				return false;
				
			}
			
			if (mask == rank) {
				
				this.usage("/unmask");
				return false;
				
			}
			
			if (!canMask(rank, mask)) {
				
				FleXPlayerMaskEvent event = new FleXPlayerMaskEvent(player, mask, Result.FAILURE);
				
				Fukkit.getEventFactory().call(event);
				
				if (event.isCancelled())
					return false;
				
				Arrays.stream(ThemeMessage.MASK_FAILURE_DENIED.format(theme, player.getLanguage(), new Variable<String>("%mask%", mask.getName()))).forEach(m -> {
					player.sendMessage(theme.format(m));
				});
				
				return false;
				
			}
			
			FleXPlayerMaskEvent event = new FleXPlayerMaskEvent(player, mask, Result.SUCCESS);
			
			Fukkit.getEventFactory().call(event);
			
			if (event.isCancelled())
				return false;
			
			player.setMask(mask);
			
			Arrays.stream(ThemeMessage.MASK_SUCCESS.format(theme, player.getLanguage(), new Variable<String>("%mask%", mask.getName()))).forEach(m -> {
				player.sendMessage(theme.format(m));
			});
			
			return true;
			
		}
		
		StringBuilder masks = new StringBuilder();
		
		for (Rank mask : Memory.RANK_CACHE)
			if (canMask(rank, mask))
				masks.append(masks.length() > 0 ? "<sp>,<reset> <sc>" + mask + " <pp>(<reset>" + mask.getDisplay(theme, true) + "<pp>)" : "<sc>" + mask + "<reset> <pp>(<sc>" + mask.getDisplay(theme, true) + "<pp>)");
		
		Arrays.stream(ThemeMessage.MASK_LIST.format(theme, player.getLanguage(), new Variable<String>("%masks%", masks.toString()))).forEach(m -> {
			player.sendMessage(theme.format(m));
		});
		
		return true;
		
	}
	
	private static boolean canMask(Rank rank, Rank mask) {
		
		if (rank.getName().equalsIgnoreCase("owner"))
			return true;
		
		return !rank.getName().equalsIgnoreCase("bot") && mask != rank && mask.getWeight() > 0 && mask.getWeight() < rank.getWeight() && !mask.isStaff();
		
	}
	
}
