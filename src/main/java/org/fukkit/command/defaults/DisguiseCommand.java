package org.fukkit.command.defaults;

import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.fukkit.Fukkit;
import org.fukkit.Memory;
import org.fukkit.PlayerState;
import org.fukkit.api.helper.MojangHelper;
import org.fukkit.command.Command;
import org.fukkit.command.CooldownCommand;
import org.fukkit.command.FlaggedCommand;
import org.fukkit.command.FleXCommandAdapter;
import org.fukkit.command.GlobalCommand;
import org.fukkit.command.RestrictCommand;
import org.fukkit.disguise.Disguise;
import org.fukkit.disguise.FleXSkin;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.player.FleXPlayerDisguiseEvent.Result;
import org.fukkit.event.player.FleXPlayerPreDisguiseEvent;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;
import org.fukkit.utils.BukkitUtils;
import org.fukkit.utils.ThemeUtils;

import io.flex.FleX.Task;
import io.flex.FleXMissingResourceException;
import io.flex.commons.Nullable;
import io.flex.commons.Severity;
import io.flex.commons.console.Console;
import io.flex.commons.file.Language;
import io.flex.commons.file.Variable;
import io.flex.commons.utils.ArrayUtils;

@GlobalCommand
@FlaggedCommand(flags = { "-f", "-s" })
@CooldownCommand(delay = 20, timeUnit = TimeUnit.SECONDS)
@RestrictCommand(permission = "flex.command.disguise", disallow = { PlayerState.INGAME_PVE_ONLY, PlayerState.INGAME, PlayerState.SPECTATING, PlayerState.UNKNOWN })
@Command(name = "disguise", aliases = { "d" }, usage = "/<command>")
public class DisguiseCommand extends FleXCommandAdapter {
	
	public boolean perform(CommandSender sender, String[] args, String[] flags) {
		
		FleXPlayer player = (FleXPlayer) sender;
		
		boolean canFlip = player.hasPermission("flex.command.disguise.flip");
		boolean canKeepSkin = player.hasPermission("flex.command.disguise.keepskin");
		boolean canChooseSkin = player.hasPermission("flex.command.disguise.chooseskin");
		boolean canChooseNameAndSkin = player.hasPermission("flex.command.disguise.custom");
		
		if (canChooseNameAndSkin && args.length != 0 && args.length != 1 && args.length != 2) {
			this.usage(sender);
			return false;
		}
		
		else if (canChooseSkin && args.length != 0 && args.length != 1) {
			this.usage(sender);
			return false;
		}
		
		else if (args.length != 0) {
			this.usage(sender);
			return false;
		}
		
		boolean flipped = ArrayUtils.contains(flags, "-f");
		boolean original = ArrayUtils.contains(flags, "-s");
		
		boolean generateNameAndSkin = args.length == 0;
		boolean generateNameOnly = args.length == 1;
		boolean chooseBoth = args.length == 2;
		
		if ((chooseBoth && !canChooseNameAndSkin) || (generateNameOnly && !canChooseSkin) || (flipped && !canFlip) || (original && !canKeepSkin)) {
			this.noPermission(sender);
			return false;
		}
		
		Theme theme = player.getTheme();
		Language lang = player.getLanguage();
		
		Variable<?>[] refresh = ThemeUtils.getNameVariables(player, theme);
		Variable<?>[] variables = ArrayUtils.add(refresh,
				
				new Variable<Boolean>("%flipped%", flipped),
				new Variable<Boolean>("%keep_skin%", original));
		
		// TODO add more custom messages
		if (generateNameAndSkin || generateNameOnly)
			player.sendMessage(ThemeMessage.DISGUISE_PREDISGUISE.format(theme, lang, variables));
		
		disguisePlayerAsync(player, !generateNameAndSkin ? args[0] : null, generateNameOnly ? args[0] : chooseBoth ? args[1] : null, flipped, original);
		return true;
		
	}
	
	public static void disguisePlayerAsync(FleXPlayer player, @Nullable String name, @Nullable String skinName, boolean flipped, boolean original) {
		
		BukkitUtils.asyncThread(() -> {
			
			String parseName = name;
			
			boolean randomName = name == null;
			boolean randomSkin = skinName == null;
			
			Theme theme = player.getTheme();
			Language lang = player.getLanguage();
			
			FleXPlayerPreDisguiseEvent preDisguise = null;
			
			FleXSkin skin = null;
			
			try {
				
				if (randomName)
					parseName = Memory.SKIN_CACHE.getRandomName();
				
				if (parseName != null && (parseName.equalsIgnoreCase("Chadthedj") || parseName.equalsIgnoreCase("Fiddycal") || parseName.equalsIgnoreCase("5Ocal")))
					parseName = Memory.SKIN_CACHE.getRandomName();
				
				if (parseName == null)
					throw new NullPointerException("name cannot be null.");
				
				if (randomSkin)
					skin = Memory.SKIN_CACHE.getRandom();
				
				else {
					
					skin = Memory.SKIN_CACHE.get(skinName);
					
					if (skin == null)
						skin = MojangHelper.getSkin(skinName);
					
				}
				
				if (skin == null)
					throw new NullPointerException("skin cannot be null.");
				
				Disguise disguise = new Disguise(parseName, skin, randomName, randomSkin);
				
				preDisguise = new FleXPlayerPreDisguiseEvent(player, disguise, Result.SUCCESS);
				
			} catch (Exception e) {
				
				e.printStackTrace();
				
				String nameDisplay = randomName ? ChatColor.MAGIC + "random" + ChatColor.RESET : parseName;
				String skinDisplay = randomSkin ? ChatColor.MAGIC + "random" + ChatColor.RESET : skinName;
				
				// TODO add more custom messages for different permissions.
				player.sendMessage(ThemeMessage.DISGUISE_FAILURE_ERROR.format(theme, lang, new Variable<String>("%name%", nameDisplay), new Variable<String>("%skin%", skinDisplay)));
				
				FleXMissingResourceException exception = new FleXMissingResourceException("An error occurred loading skin " + (randomSkin ? "[RANDOM]" : skinName) + ".");
				
				Task.debug(exception.getMessage());
				
				Console.log("Disguise", Severity.NOTICE, exception);
				
				preDisguise = new FleXPlayerPreDisguiseEvent(player, null, Result.FAILURE);
				
			}
			
			FleXPlayerPreDisguiseEvent parsePreDisguise = preDisguise;
			
			FleXSkin parseSkin = skin;
			
			BukkitUtils.mainThread(() -> {
				
				if (!player.isOnline())
					return;
				
				Fukkit.getEventFactory().call(parsePreDisguise);
				
				Disguise disguise = parsePreDisguise.getDisguise();
				
				if (disguise == null || parseSkin == null || parsePreDisguise.isCancelled())
					return;
				
				player.setDisguise(disguise);
				
				player.sendMessage(ThemeMessage.DISGUISE_SUCCESS.format(theme, lang, new Variable<String>("%display%", player.getDisplayName(theme, false))));
				
			});
			
		});
		
	}
	
	@Override
	public void usage(CommandSender sender) {
		
		FleXPlayer player = (FleXPlayer) sender;
		
		boolean flip = player.hasPermission("flex.command.disguise.flip");
		boolean keep = player.hasPermission("flex.command.disguise.keepskin");
		boolean skin = player.hasPermission("flex.command.disguise.chooseskin");
		boolean custom = player.hasPermission("flex.command.disguise.custom");
		
		String allowedFlags = (flip || keep ? " [" + (flip ? "-f" : "") + (flip && keep ? ", " : "") + (keep ? "-s" : "") + "]" : "");
		
		super.usage(sender, (custom ? "/<command> <name> <skin>" : skin ? "/<command> <skin>" : this.getUsage()) + allowedFlags);
		
	}
	
}
